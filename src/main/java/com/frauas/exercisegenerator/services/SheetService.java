package com.frauas.exercisegenerator.services;

import com.frauas.exercisegenerator.documents.*;
import com.frauas.exercisegenerator.dtos.CreateSheetDto;
import com.frauas.exercisegenerator.helpers.CategoryUpsertHelper;
import com.frauas.exercisegenerator.helpers.CourseUpsertHelper;
import com.frauas.exercisegenerator.repositories.ExerciseRepository;
import com.frauas.exercisegenerator.repositories.SheetRepository;
import com.frauas.exercisegenerator.repositories.UserRepository;
import com.frauas.exercisegenerator.util.TokenUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
public class SheetService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private SheetRepository sheetRepository;

    @Autowired
    private TokenUtil tokenUtil;


    @Autowired
    private CourseUpsertHelper courseUpsertHelper;
    @Autowired
    private CategoryUpsertHelper categoryUpsertHelper;

    @Autowired
    private ModelMapper modelMapper;

    public List<Sheet> getSheets() {
        return this.sheetRepository.findAll();
    }

    public Sheet getSheetById(String id) {
        return this.sheetRepository.findById(id)
                .orElseThrow(() -> new HttpServerErrorException(HttpStatus.NOT_FOUND));
    }

    public Sheet createSheet(HttpServletRequest request, CreateSheetDto createSheetDto) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());

        tokenUtil.validateToken(token);

        Optional<User> user = this.userRepository.findByUsername(tokenUtil.getUsernameFromToken(token));
        if(user.isPresent() == false)
        {
            throw new RuntimeException("username not valid");
        }


        ArrayList<Course> courses = courseUpsertHelper.upsertCoursesFromDto(createSheetDto.getCourses());
        ArrayList<Category> categories = categoryUpsertHelper.upsertCategoriesFromDto(createSheetDto.getCategories());

        ArrayList<Exercise> exercises = new ArrayList<>();
        createSheetDto.getExercises().forEach(exerciseId -> {
            Optional<Exercise> optional = this.exerciseRepository.findById(exerciseId);

            optional.ifPresent(exercises::add);
        });

        Sheet sheet = modelMapper.map(createSheetDto, Sheet.class);

        sheet.setAuthor(user.get());
        sheet.setPublishedAt(LocalDateTime.now());
        sheet.setCourses(courses);
        sheet.setCategories(categories);
        sheet.setExercises(exercises);

        return sheetRepository.save(sheet);
    }

    public void deleteSheetById(String id) {
        sheetRepository.deleteById(id);
    }
}

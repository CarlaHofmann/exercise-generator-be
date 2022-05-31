package com.frauas.exercisegenerator.services;

import com.frauas.exercisegenerator.documents.*;
import com.frauas.exercisegenerator.dtos.CreateSheetDto;
import com.frauas.exercisegenerator.helpers.CategoryUpsertHelper;
import com.frauas.exercisegenerator.helpers.CourseUpsertHelper;
import com.frauas.exercisegenerator.repositories.ExerciseRepository;
import com.frauas.exercisegenerator.repositories.SheetRepository;
import com.frauas.exercisegenerator.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SheetService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private SheetRepository sheetRepository;


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

    public Sheet createSheet(CreateSheetDto createSheetDto) {
        // TODO: Use actual author resolution via login credentials
        Optional<User> user = this.userRepository.findByUsername("default");

        if (user == null) {
            user = Optional.of(User.builder().username("default").build());
            user = Optional.of(this.userRepository.save(user.get()));
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

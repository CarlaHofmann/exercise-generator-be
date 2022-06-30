package com.frauas.exercisegenerator.services;

import com.frauas.exercisegenerator.documents.*;
import com.frauas.exercisegenerator.dtos.SheetDto;
import com.frauas.exercisegenerator.repositories.ExerciseRepository;
import com.frauas.exercisegenerator.repositories.SheetRepository;
import com.frauas.exercisegenerator.repositories.UserRepository;
import com.frauas.exercisegenerator.util.TokenUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    private ModelMapper modelMapper;

    public List<Sheet> getSheets() {
        return this.sheetRepository.findAll();
    }

    public Sheet getSheetById(String id) {
        return this.sheetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sheet with id '" + id + "' could not be found!"));
    }

    public Sheet prepareSheet(HttpServletRequest request, SheetDto sheetDto) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());

        tokenUtil.validateToken(token);

        Optional<User> user = this.userRepository.findByUsername(tokenUtil.getUsernameFromToken(token));
        if (user.isPresent() == false) {
            throw new RuntimeException("username not valid");
        }


        ArrayList<Course> courses = new ArrayList<>();
        sheetDto.getCourses().forEach(courseDto -> {
            Course course = Course.builder()
                    .name(courseDto.getName())
                    .build();
            courses.add(course);
        });

        ArrayList<Category> categories = new ArrayList<>();
        sheetDto.getCategories().forEach(categorieDto -> {
            Category category = Category.builder()
                    .name(categorieDto.getName())
                    .build();
            categories.add(category);
        });

        ArrayList<Exercise> exercises = new ArrayList<>();
        this.exerciseRepository.findAllById(sheetDto.getExercises()).forEach(exercises::add);

        Sheet sheet = modelMapper.map(sheetDto, Sheet.class);

        if (sheetDto.getIsPublished()) {
            sheet.setPublishedAt(LocalDateTime.now());
            sheet.setIsPublished(true);
        }

        if (sheetDto.getUseNumericTitles()) {
            sheet.setUseNumericTitles(true);
        }

        if (sheetDto.getShowSolutions()) {
            sheet.setShowSolutions(true);
        }

        sheet.setAuthor(user.get());
        sheet.setCourses(courses);
        sheet.setCategories(categories);
        sheet.setExercises(exercises);

        return sheet;
    }

    public Sheet createSheet(HttpServletRequest request, SheetDto sheetDto) {
        Sheet sheet = prepareSheet(request, sheetDto);

        return sheetRepository.save(sheet);
    }

    public Sheet updateSheetById(String id, SheetDto sheetDto) {
        Sheet sheet = sheetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sheet with id '" + id + "' could not be found!"));

        modelMapper.map(sheetDto, sheet);

        ArrayList<Course> courses = new ArrayList<>();
        sheetDto.getCourses().forEach(courseDto -> {
            Course course = Course.builder()
                    .name(courseDto.getName())
                    .build();
            courses.add(course);
        });

        ArrayList<Category> categories = new ArrayList<>();
        sheetDto.getCategories().forEach(categorieDto -> {
            Category category = Category.builder()
                    .name(categorieDto.getName())
                    .build();
            categories.add(category);
        });

        ArrayList<Exercise> exercises = new ArrayList<>();
        exerciseRepository.findAllById(sheetDto.getExercises()).forEach(exercises::add);

        if (sheetDto.getIsPublished() && !sheet.getIsPublished()) {
            sheet.setPublishedAt(LocalDateTime.now());
            sheet.setIsPublished(true);
        } else if (!sheetDto.getIsPublished() && sheet.getIsPublished()) {
            sheet.setPublishedAt(null);
            sheet.setIsPublished(false);
        }

        if (sheetDto.getUseNumericTitles() && !sheet.getUseNumericTitles()) {
            sheet.setUseNumericTitles(true);
        } else if (!sheetDto.getUseNumericTitles() && sheet.getUseNumericTitles()) {
            sheet.setUseNumericTitles(false);
        }

        if (sheetDto.getShowSolutions() && !sheet.getShowSolutions()) {
            sheet.setShowSolutions(true);
        } else if (!sheetDto.getShowSolutions() && sheet.getShowSolutions()) {
            sheet.setShowSolutions(false);
        }

        sheet.setCourses(courses);
        sheet.setCategories(categories);
        sheet.setExercises(exercises);

        return sheetRepository.save(sheet);
    }

    public void deleteSheetById(String id) {
        sheetRepository.deleteById(id);
    }
}

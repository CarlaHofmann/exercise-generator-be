package com.frauas.exercisegenerator.services;

import com.frauas.exercisegenerator.documents.*;
import com.frauas.exercisegenerator.dtos.SheetDto;
import com.frauas.exercisegenerator.repositories.AuthorRepository;
import com.frauas.exercisegenerator.repositories.ExerciseRepository;
import com.frauas.exercisegenerator.repositories.SheetRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SheetService {
    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private SheetRepository sheetRepository;

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

    public Sheet createSheet(SheetDto sheetDto) {
        // TODO: Use actual author resolution via login credentials
        Author author = this.authorRepository.findByName("default");

        if (author == null) {
            author = Author.builder().name("default").build();
            author = this.authorRepository.save(author);
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

        if(sheetDto.getIsPublished()){
            sheet.setPublishedAt(LocalDateTime.now());
            sheet.setIsPublished(true);
        }
        if(sheetDto.getUseNumericTitles()){
            sheet.setUseNumericTitles(true);
        }
        if(sheetDto.getShowSolution()){
            sheet.setShowSolution(true);
        }

        sheet.setAuthor(author);
        sheet.setCourses(courses);
        sheet.setCategories(categories);
        sheet.setExercises(exercises);

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

        if(sheetDto.getIsPublished() && !sheet.getIsPublished()){
            sheet.setPublishedAt(LocalDateTime.now());
            sheet.setIsPublished(true);
        } else if (!sheetDto.getIsPublished() && sheet.getIsPublished()) {
            sheet.setPublishedAt(null);
            sheet.setIsPublished(false);
        }

        if(sheetDto.getUseNumericTitles() && !sheet.getUseNumericTitles()){
            sheet.setUseNumericTitles(true);
        } else if (!sheetDto.getUseNumericTitles() && sheet.getUseNumericTitles()) {
            sheet.setUseNumericTitles(false);
        }

        if(sheetDto.getShowSolution() && !sheet.getShowSolution()){
            sheet.setShowSolution(true);
        } else if (!sheetDto.getShowSolution() && sheet.getShowSolution()) {
            sheet.setShowSolution(false);
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

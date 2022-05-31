package com.frauas.exercisegenerator.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.frauas.exercisegenerator.documents.Author;
import com.frauas.exercisegenerator.documents.Category;
import com.frauas.exercisegenerator.documents.Course;
import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.documents.Sheet;
import com.frauas.exercisegenerator.dtos.CreateSheetDto;
import com.frauas.exercisegenerator.exceptions.DocumentNotFoundException;
import com.frauas.exercisegenerator.helpers.CategoryUpsertHelper;
import com.frauas.exercisegenerator.helpers.CourseUpsertHelper;
import com.frauas.exercisegenerator.repositories.AuthorRepository;
import com.frauas.exercisegenerator.repositories.ExerciseRepository;
import com.frauas.exercisegenerator.repositories.SheetRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

@Service
public class SheetService {
    @Autowired
    private AuthorRepository authorRepository;

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
                .orElseThrow(() -> new HttpServerErrorException(HttpStatus.NOT_FOUND,
                        "Sheet with id '" + id + "' could not be found!"));
    }

    public Sheet createSheet(CreateSheetDto createSheetDto) {
        // TODO: Use actual author resolution via login credentials
        Author author = this.authorRepository.findByName("default");

        if (author == null) {
            author = Author.builder().name("default").build();
            author = this.authorRepository.save(author);
        }

        ArrayList<Course> courses = courseUpsertHelper.upsertCoursesFromDto(createSheetDto.getCourses());
        ArrayList<Category> categories = categoryUpsertHelper.upsertCategoriesFromDto(createSheetDto.getCategories());

        ArrayList<Exercise> exercises = new ArrayList<>();
        this.exerciseRepository.findAllById(createSheetDto.getExercises()).forEach(exercises::add);

        Sheet sheet = modelMapper.map(createSheetDto, Sheet.class);

        sheet.setAuthor(author);
        sheet.setPublishedAt(LocalDateTime.now());
        sheet.setCourses(courses);
        sheet.setCategories(categories);
        sheet.setExercises(exercises);

        return sheetRepository.save(sheet);
    }

    public Sheet updateSheetById(String id, CreateSheetDto createSheetDto) {
        Optional<Sheet> optionalSheet = this.sheetRepository.findById(id);

        if (optionalSheet.isEmpty()) {
            throw new DocumentNotFoundException("Sheet with id '" + id + "' could not be found!");
        }

        Sheet sheet = optionalSheet.get();

        modelMapper.map(createSheetDto, sheet);

        ArrayList<Category> categories = categoryUpsertHelper.upsertCategoriesFromDto(createSheetDto.getCategories());
        ArrayList<Exercise> exercises = new ArrayList<>();
        this.exerciseRepository.findAllById(createSheetDto.getExercises()).forEach(exercises::add);

        sheet.setCategories(categories);
        sheet.setExercises(exercises);

        return sheet;
    }

    public void deleteSheetById(String id) {
        sheetRepository.deleteById(id);
    }
}

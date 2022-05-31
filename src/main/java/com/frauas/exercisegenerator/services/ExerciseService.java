package com.frauas.exercisegenerator.services;

import java.util.ArrayList;
import java.util.List;

import com.frauas.exercisegenerator.documents.Author;
import com.frauas.exercisegenerator.documents.Category;
import com.frauas.exercisegenerator.documents.Course;
import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.dtos.CreateExerciseDto;
import com.frauas.exercisegenerator.helpers.CategoryUpsertHelper;
import com.frauas.exercisegenerator.helpers.CourseUpsertHelper;
import com.frauas.exercisegenerator.repositories.AuthorRepository;
import com.frauas.exercisegenerator.repositories.CategoryRepository;
import com.frauas.exercisegenerator.repositories.ExerciseRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExerciseService {

    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CourseUpsertHelper courseUpsertHelper;

    @Autowired
    CategoryUpsertHelper categoryUpsertHelper;

    public List<Exercise> getAllExercises() {
        return this.exerciseRepository.findAll();
    }

    public Exercise getExerciseById(String id) {
        return this.exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Exercise with id '" + id + "' could not be found!"));
    }

    public Exercise createExerciseFromDto(CreateExerciseDto exerciseDto) {
        // TODO: Use actual author resolution via login credentials
        Author author = this.authorRepository.findByName("default");

        if (author == null) {
            author = Author.builder().name("default").build();
            author = this.authorRepository.save(author);
        }

        ArrayList<Course> courses = courseUpsertHelper.upsertCoursesFromDto(exerciseDto.getCourses());
        ArrayList<Category> categories = categoryUpsertHelper.upsertCategoriesFromDto(exerciseDto.getCategories());

        Exercise exercise = this.modelMapper.map(exerciseDto, Exercise.class);

        exercise.setAuthor(author);
        exercise.setCourses(courses);
        exercise.setCategories(categories);

        return this.exerciseRepository.save(exercise);
    }

    public Exercise updateExerciseById(String id, CreateExerciseDto exerciseDto) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Exercise with id '" + id + "' could not be found!"));

        modelMapper.map(exerciseDto, exercise);

        ArrayList<Course> courses = courseUpsertHelper.upsertCoursesFromDto(exerciseDto.getCourses());
        ArrayList<Category> categories = categoryUpsertHelper.upsertCategoriesFromDto(exerciseDto.getCategories());

        exercise.setCourses(courses);
        exercise.setCategories(categories);

        return exerciseRepository.save(exercise);
    }

    public void deleteExerciseById(String id) {
        exerciseRepository.deleteById(id);
    }
}

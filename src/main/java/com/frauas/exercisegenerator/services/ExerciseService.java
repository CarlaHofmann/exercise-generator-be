package com.frauas.exercisegenerator.services;

import com.frauas.exercisegenerator.documents.Category;
import com.frauas.exercisegenerator.documents.Course;
import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.documents.User;
import com.frauas.exercisegenerator.dtos.CreateExerciseDto;
import com.frauas.exercisegenerator.helpers.CategoryUpsertHelper;
import com.frauas.exercisegenerator.helpers.CourseUpsertHelper;
import com.frauas.exercisegenerator.repositories.CategoryRepository;
import com.frauas.exercisegenerator.repositories.ExerciseRepository;
import com.frauas.exercisegenerator.repositories.CategoryRepository;
import com.frauas.exercisegenerator.repositories.ExerciseRepository;
import com.frauas.exercisegenerator.repositories.UserRepository;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExerciseService {

    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    UserRepository userRepository;

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
                .orElseThrow(() -> new HttpServerErrorException(HttpStatus.NOT_FOUND));
    }

    public Exercise createExerciseFromDto(CreateExerciseDto exerciseDto) {
        // TODO: Use actual author resolution via login credentials
        Optional<User> authorOpt = this.userRepository.findByUsername("default");
        User author;

        if (authorOpt.isPresent() == false) {
            author = User.builder().username("default").build();
            author = this.userRepository.save(author);
        }
        else {
            author = authorOpt.get();
        }

        ArrayList<Course> courses = courseUpsertHelper.upsertCoursesFromDto(exerciseDto.getCourses());
        ArrayList<Category> categories = categoryUpsertHelper.upsertCategoriesFromDto(exerciseDto.getCategories());

        Exercise exercise = this.modelMapper.map(exerciseDto, Exercise.class);

        exercise.setAuthor(author);
        exercise.setCourses(courses);
        exercise.setCategories(categories);

        return this.exerciseRepository.save(exercise);
    }

    public void deleteExerciseById(String id) {
        exerciseRepository.deleteById(id);
    }
}

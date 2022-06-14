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
import com.frauas.exercisegenerator.util.TokenUtil;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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

    @Autowired
    private TokenUtil tokenUtil;

    public List<Exercise> getAllExercises() {
        return this.exerciseRepository.findAll();
    }

    public Exercise getExerciseById(String id) {
        return this.exerciseRepository.findById(id)
                .orElseThrow(() -> new HttpServerErrorException(HttpStatus.NOT_FOUND));
    }

    public Exercise createExerciseFromDto(HttpServletRequest request, CreateExerciseDto exerciseDto) {

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());

        tokenUtil.validateToken(token);

        Optional<User> user = this.userRepository.findByUsername(tokenUtil.getUsernameFromToken(token));
        if(user.isPresent() == false)
        {
            throw new RuntimeException("username not valid");
        }

        ArrayList<Course> courses = courseUpsertHelper.upsertCoursesFromDto(exerciseDto.getCourses());
        ArrayList<Category> categories = categoryUpsertHelper.upsertCategoriesFromDto(exerciseDto.getCategories());

        Exercise exercise = this.modelMapper.map(exerciseDto, Exercise.class);

        exercise.setAuthor(user.get());
        exercise.setCourses(courses);
        exercise.setCategories(categories);

        return this.exerciseRepository.save(exercise);
    }

    public void deleteExerciseById(String id) {
        exerciseRepository.deleteById(id);
    }
}

package com.frauas.exercisegenerator.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.frauas.exercisegenerator.documents.User;
import com.frauas.exercisegenerator.documents.Category;
import com.frauas.exercisegenerator.documents.Course;
import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.documents.Image;
import com.frauas.exercisegenerator.dtos.ExerciseDto;
import com.frauas.exercisegenerator.helpers.CategoryUpsertHelper;
import com.frauas.exercisegenerator.helpers.CourseUpsertHelper;
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
    private TokenUtil tokenUtil;

    @Autowired
    ImageService imageService;

    public List<Exercise> getAllExercises() {
        List<Exercise> exercises = this.exerciseRepository.findAll();

        exercises.forEach(exercise -> imageService.hydrateExerciseImageContent(exercise));

        return exercises;
    }

    public Exercise getExerciseById(String id) {
        Exercise exercise = this.exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Exercise with id '" + id + "' could not be found!"));

        imageService.hydrateExerciseImageContent(exercise);

        return exercise;
    }

    public Exercise prepareExerciseFromDto(HttpServletRequest request, ExerciseDto exerciseDto) {

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());

        tokenUtil.validateToken(token);

        Optional<User> user = this.userRepository.findByUsername(tokenUtil.getUsernameFromToken(token));
        if(user.isPresent() == false)
        {
            throw new RuntimeException("username not valid");
        }

        ArrayList<Course> courses = new ArrayList<>();
        exerciseDto.getCourses().forEach(courseDto -> {
            Course course = Course.builder()
                    .name(courseDto.getName())
                    .build();
            courses.add(course);
        });

        ArrayList<Category> categories = new ArrayList<>();
        exerciseDto.getCategories().forEach(categoryDto -> {
            Category category = Category.builder()
                    .name(categoryDto.getName())
                    .build();
            categories.add(category);
        });

        ArrayList<Image> images = new ArrayList<>();
        exerciseDto.getImages().forEach(imageDto -> {
            try {
                Image image = imageService.saveImage(imageDto);
                images.add(image);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error during save process of image with reference '" + imageDto.getReference() + "':\n"
                                + e.getMessage());
            }
        });

        Exercise exercise = this.modelMapper.map(exerciseDto, Exercise.class);

        if (exerciseDto.getIsPublished()) {
            exercise.setPublishedAt(LocalDateTime.now());
            exercise.setIsPublished(true);
        }

        exercise.setAuthor(user.get());
        exercise.setCourses(courses);
        exercise.setCategories(categories);
        exercise.setImages(images);

        return exercise;
    }

    public Exercise createExerciseFromDto(HttpServletRequest request, ExerciseDto exerciseDto) {
        Exercise exercise = prepareExerciseFromDto(request, exerciseDto);

        return this.exerciseRepository.save(exercise);
    }

    public Exercise updateExerciseById(String id, ExerciseDto exerciseDto) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Exercise with id '" + id + "' could not be found!"));

        ArrayList<Course> courses = new ArrayList<>();
        exerciseDto.getCourses().forEach(courseDto -> {
            Course course = Course.builder()
                    .name(courseDto.getName())
                    .build();
            courses.add(course);
        });

        ArrayList<Category> categories = new ArrayList<>();
        exerciseDto.getCategories().forEach(categorieDto -> {
            Category category = Category.builder()
                    .name(categorieDto.getName())
                    .build();
            categories.add(category);
        });

        // Delete all images first
        // * Note: This is kind of a "rambo" method, but the safest and least complex
        // * way to prevent duplicate or dangling image files
        exercise.getImages().forEach(image -> imageService.deleteImageFile(image));

        // Create new images from dto
        ArrayList<Image> images = new ArrayList<>();
        exerciseDto.getImages().forEach(imageDto -> {
            try {
                Image image = imageService.saveImage(imageDto);
                images.add(image);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error during save process of image with reference '" + imageDto.getReference() + "':\n"
                                + e.getMessage());
            }
        });

        modelMapper.map(exerciseDto, exercise);

        if (exerciseDto.getIsPublished() && !exercise.getIsPublished()) {
            exercise.setPublishedAt(LocalDateTime.now());
            exercise.setIsPublished(true);
        } else if (!exerciseDto.getIsPublished() && exercise.getIsPublished()) {
            exercise.setPublishedAt(null);
            exercise.setIsPublished(false);
        }

        exercise.setCourses(courses);
        exercise.setCategories(categories);
        exercise.setImages(images);

        return exerciseRepository.save(exercise);
    }

    public void deleteExerciseById(String id) {
        Exercise exercise = this.exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Exercise with id '" + id + "' could not be found!"));

        exercise.getImages().forEach(image -> imageService.deleteImageFile(image));

        exerciseRepository.delete(exercise);
    }
}

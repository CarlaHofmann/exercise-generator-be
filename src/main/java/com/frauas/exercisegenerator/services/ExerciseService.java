package com.frauas.exercisegenerator.services;

import java.util.List;
import java.util.Optional;

import com.frauas.exercisegenerator.documents.Author;
import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.dtos.CreateExerciseDto;
import com.frauas.exercisegenerator.repositories.AuthorRepository;
import com.frauas.exercisegenerator.repositories.ExerciseRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExerciseService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    AuthorRepository authorRepository;

    public List<Exercise> getAllExercises() {
        return this.exerciseRepository.findAll();
    }

    public Optional<Exercise> getExerciseById(String id) {
        return this.exerciseRepository.findById(id);
    }

    public Exercise createExerciseFromDto(CreateExerciseDto exerciseDto) {
        // TODO: Use actual author resolution via login credentials
        Author author = this.authorRepository.findByName("default");

        if (author == null) {
            author = Author.builder().name("default").build();
            author = this.authorRepository.save(author);
        }

        Exercise exercise = this.modelMapper.map(exerciseDto, Exercise.class);

        exercise.setAuthor(author);

        return this.exerciseRepository.save(exercise);
    }
}

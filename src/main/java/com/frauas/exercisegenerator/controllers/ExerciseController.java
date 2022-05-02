package com.frauas.exercisegenerator.controllers;

import java.util.List;
import java.util.Optional;

import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.dtos.CreateExerciseDto;
import com.frauas.exercisegenerator.services.ExerciseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exercise")
public class ExerciseController {

    @Autowired
    ExerciseService exerciseService;

    @GetMapping
    public List<Exercise> getAllExercises() {
        return this.exerciseService.getAllExercises();
    }

    @GetMapping("/{id}")
    public Optional<Exercise> getExerciseById(@PathVariable String id) {
        return this.exerciseService.getExerciseById(id);
    }

    @PostMapping
    public Exercise createExercise(@RequestBody CreateExerciseDto exerciseDto) {
        return this.exerciseService.createExerciseFromDto(exerciseDto);
    }
}

package com.frauas.exercisegenerator.controllers;

import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.dtos.CreateExerciseDto;
import com.frauas.exercisegenerator.services.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Exercise getExerciseById(@PathVariable String id) {
        return this.exerciseService.getExerciseById(id);
    }

    @PostMapping
    public Exercise createExercise(@RequestBody CreateExerciseDto exerciseDto) {
        return this.exerciseService.createExerciseFromDto(exerciseDto);
    }

    @DeleteMapping("/{id}")
    public void deleteExercise(@PathVariable String id) {
        exerciseService.deleteExerciseById(id);
    }
}

package com.frauas.exercisegenerator.controllers;

import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.dtos.CreateExerciseDto;
import com.frauas.exercisegenerator.services.ExerciseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExerciseController {

    @Autowired
    ExerciseService exerciseService;

    @PostMapping("/exercise")
    public Exercise createExercise(@RequestBody CreateExerciseDto exerciseDto) {
        return this.exerciseService.createExerciseFromDto(exerciseDto);
    }
}

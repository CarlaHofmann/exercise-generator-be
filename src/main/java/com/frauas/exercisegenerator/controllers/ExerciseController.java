package com.frauas.exercisegenerator.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.dtos.CreateExerciseDto;
import com.frauas.exercisegenerator.helpers.StringHelper;
import com.frauas.exercisegenerator.services.ExerciseService;
import com.frauas.exercisegenerator.services.LatexGeneratorService;

@RestController
@RequestMapping("/exercise")
public class ExerciseController {

    @Autowired
    ExerciseService exerciseService;

    @Autowired
    LatexGeneratorService latexGeneratorService;

    @GetMapping
    public List<Exercise> getAllExercises() {
        return exerciseService.getAllExercises();
    }

    @GetMapping("/{id}")
    public Exercise getExerciseById(@PathVariable String id) {
        return exerciseService.getExerciseById(id);
    }

    @GetMapping(path = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getExercisePdf(@PathVariable String id) {
        Exercise exercise = exerciseService.getExerciseById(id);

        byte[] contents = latexGeneratorService.createExercisePdf(exercise);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        String filename = StringHelper.toSnakeCase(exercise.getTitle() + ".pdf");
        headers.setContentDisposition(ContentDisposition
                .attachment()
                .filename(filename)
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        return response;
    }

    @PostMapping
    public Exercise createExercise(@RequestBody CreateExerciseDto exerciseDto) {
        return exerciseService.createExerciseFromDto(exerciseDto);
    }

    @PutMapping("/{id}")
    public Exercise updatExercise(@PathVariable String id, @RequestBody CreateExerciseDto exerciseDto) {
        return exerciseService.updateExerciseById(id, exerciseDto);
    }

    @DeleteMapping("/{id}")
    public void deleteExercise(@PathVariable String id) {
        exerciseService.deleteExerciseById(id);
    }
}

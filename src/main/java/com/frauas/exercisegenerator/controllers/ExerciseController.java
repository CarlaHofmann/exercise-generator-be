package com.frauas.exercisegenerator.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.dtos.ExerciseDto;
import com.frauas.exercisegenerator.helpers.StringHelper;
import com.frauas.exercisegenerator.services.ExerciseService;
import com.frauas.exercisegenerator.services.LatexGeneratorService;
import com.frauas.exercisegenerator.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/exercise")
public class ExerciseController {

    @Autowired
    ExerciseService exerciseService;

    @Autowired
    TokenUtil tokenUtil;

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
    public Exercise createExercise(HttpServletRequest request, @RequestBody ExerciseDto exerciseDto) {
        return exerciseService.createExerciseFromDto(request, exerciseDto);
    }

    @PostMapping(path = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> previewExerciseDto(HttpServletRequest request, @RequestBody ExerciseDto exerciseDto) {
        Exercise exercise = exerciseService.prepareExerciseFromDto(request, exerciseDto);

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

    @PutMapping("/{id}")
    public Exercise updatExercise(HttpServletRequest request, HttpServletResponse response, @PathVariable String id, @RequestBody ExerciseDto exerciseDto) throws IOException
    {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());
        String givenUsername = tokenUtil.getUsernameFromToken(token);
        String actualUsername = exerciseService.getExerciseById(id).getAuthor().getUsername();

        if(givenUsername.equals(actualUsername) == false){
            response.sendError(UNAUTHORIZED.value());
        }

        return exerciseService.updateExerciseById(id, exerciseDto);
    }

    @DeleteMapping("/{id}")
    public void deleteExercise(HttpServletRequest request, HttpServletResponse response,@PathVariable String id) throws IOException
    {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());
        String givenUsername = tokenUtil.getUsernameFromToken(token);
        String actualUsername = exerciseService.getExerciseById(id).getAuthor().getUsername();

        if(givenUsername.equals(actualUsername) == false){
            response.sendError(UNAUTHORIZED.value());
        }

        exerciseService.deleteExerciseById(id);
    }
}

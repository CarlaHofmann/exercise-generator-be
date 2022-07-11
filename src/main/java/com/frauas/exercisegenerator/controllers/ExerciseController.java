package com.frauas.exercisegenerator.controllers;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.server.ResponseStatusException;

import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.dtos.ExerciseDto;
import com.frauas.exercisegenerator.helpers.StringHelper;
import com.frauas.exercisegenerator.repositories.UserRepository;
import com.frauas.exercisegenerator.services.ExerciseService;
import com.frauas.exercisegenerator.services.LatexGeneratorService;
import com.frauas.exercisegenerator.util.TokenUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/exercise")
public class ExerciseController {

    @Autowired
    ExerciseService exerciseService;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    LatexGeneratorService latexGeneratorService;

    @Autowired
    UserRepository userRepository;

    @GetMapping
    @Operation(security = { @SecurityRequirement(name = "bearerAuth"), @SecurityRequirement(name = "") })
    public List<Exercise> getAllExercises(HttpServletRequest request) {
        List<Exercise> allExercises = exerciseService.getAllExercises();
        List<Exercise> allowedExercises = new ArrayList<>();

        for (Exercise e : allExercises) {
            String authorizationHeader = request.getHeader(AUTHORIZATION);

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring("Bearer ".length());
                tokenUtil.validateToken(token);

                String username = tokenUtil.getUsernameFromToken(token);

                if (username.equals(e.getAuthor().getUsername())) {
                    allowedExercises.add(e);
                }
            } else {
                if (e.getIsPublished()) {
                    allowedExercises.add(e);
                }
            }
        }

        return allowedExercises;
    }

    @GetMapping("/{id}")
    @Operation(security = { @SecurityRequirement(name = "bearerAuth"), @SecurityRequirement(name = "") })
    public Exercise getExerciseById(HttpServletRequest request, @PathVariable String id) {
        Exercise exercise = exerciseService.getExerciseById(id);

        if (!exercise.getIsPublished()) {
            String authorizationHeader = request.getHeader(AUTHORIZATION);

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring("Bearer ".length());
                tokenUtil.validateToken(token);

                String username = tokenUtil.getUsernameFromToken(token);

                if (!username.equals(exercise.getAuthor().getUsername())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                            "Users may only see their own unpublished exercises");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Unauthorized users may not see unpublished exercises");
            }

        }
        return exercise;
    }

    @GetMapping(path = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(security = { @SecurityRequirement(name = "bearerAuth"), @SecurityRequirement(name = "") })
    public ResponseEntity<byte[]> getExercisePdf(HttpServletRequest request, @PathVariable String id) {
        Exercise exercise = exerciseService.getExerciseById(id);

        if (!exercise.getIsPublished()) {
            String authorizationHeader = request.getHeader(AUTHORIZATION);

            if (authorizationHeader == null) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Unauthorized users may not generate pdf files for unpublished exercises");
            }

            String token = authorizationHeader.substring("Bearer ".length());
            tokenUtil.validateToken(token);

            String username = tokenUtil.getUsernameFromToken(token);

            if (!username.equals(exercise.getAuthor().getUsername())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Users may only generate pdf files of unpublished exercises for their own exercises");
            }
        }

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
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public Exercise createExercise(HttpServletRequest request, @RequestBody ExerciseDto exerciseDto) {
        return exerciseService.createExerciseFromDto(request, exerciseDto);
    }

    @PostMapping(path = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
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
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public Exercise updateExercise(HttpServletRequest request, HttpServletResponse response, @PathVariable String id,
            @RequestBody ExerciseDto exerciseDto) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());
        String givenUsername = tokenUtil.getUsernameFromToken(token);
        String actualUsername = exerciseService.getExerciseById(id).getAuthor().getUsername();

        if (givenUsername.equals(actualUsername) == false) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Users may only update exercises which they've authored themselves");
        }

        return exerciseService.updateExerciseById(id, exerciseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public void deleteExercise(HttpServletRequest request, HttpServletResponse response, @PathVariable String id)
            throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());
        String givenUsername = tokenUtil.getUsernameFromToken(token);
        String actualUsername = exerciseService.getExerciseById(id).getAuthor().getUsername();

        if (givenUsername.equals(actualUsername) == false) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Users may only delete exercises which they've authored themselves");
        }

        exerciseService.deleteExerciseById(id);
    }
}

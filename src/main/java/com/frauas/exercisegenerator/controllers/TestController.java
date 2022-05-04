package com.frauas.exercisegenerator.controllers;

import java.util.Optional;

import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.repositories.ExerciseRepository;
import com.frauas.exercisegenerator.services.LatexGeneratorService;

import org.openapitools.api.TestApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController implements TestApi {
    @Autowired
    private LatexGeneratorService latexGeneratorService;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @GetMapping(value = "/latex")
    public ResponseEntity<byte[]> getLatexPdf(
            @RequestParam(name = "content", defaultValue = "Hello World!") String content) throws Exception {
        byte[] contents = latexGeneratorService.renderLatexPdfContent(
                "\\begin{frame}[fragile]\n\\frametitle{Example of Normal Slide}\n" + content + "\n\\end{frame}");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Here you have to set the actual filename of your pdf
        String filename = "base.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);

        return response;
    }

    @GetMapping(value = "/latex/{exerciseId}")
    public ResponseEntity<byte[]> getExerciseLatex(@PathVariable String exerciseId) throws Exception {
        Optional<Exercise> optionalExercise = exerciseRepository.findById(exerciseId);

        if (!optionalExercise.isPresent())
            return ResponseEntity.notFound().build();

        Exercise exercise = optionalExercise.get();

        String contentString = "\\section{" + exercise.getTitle() + "}\n\\begin{frame}[fragile]\n\\frametitle{"
                + exercise.getTitle() + "}\n"
                + exercise.getText() + "\n\\end{frame}";
        byte[] contents = latexGeneratorService.renderLatexPdfContent(contentString);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Here you have to set the actual filename of your pdf
        String filename = "base.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        return response;
    }
}

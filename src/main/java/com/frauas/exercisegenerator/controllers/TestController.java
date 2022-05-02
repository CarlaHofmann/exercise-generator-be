package com.frauas.exercisegenerator.controllers;

import com.frauas.exercisegenerator.services.LatexGeneratorService;

import org.openapitools.api.TestApi;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController implements TestApi {
    private LatexGeneratorService latexGeneratorService;

    public TestController(LatexGeneratorService latexGeneratorService) {
        this.latexGeneratorService = latexGeneratorService;
    }

    @GetMapping(value = "/latex")
    public ResponseEntity<byte[]> getLatexPdf(
            @RequestParam(name = "content", defaultValue = "Hello World!") String content) throws Exception {
        byte[] contents = latexGeneratorService.renderLatexPdfContent(content);

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

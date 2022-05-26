package com.frauas.exercisegenerator.controllers;

import com.frauas.exercisegenerator.services.ImageService;
import com.frauas.exercisegenerator.services.LatexGeneratorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    private LatexGeneratorService latexGeneratorService;

    @Autowired
    private ImageService imageService;

    @PostMapping("/image")
    public ResponseEntity<String> saveImage(@RequestBody String imageString) throws Exception {
        return ResponseEntity.ok(imageService.saveImage(imageString));
    }

    @GetMapping("/image")
    public ResponseEntity<String> getImage() throws Exception {
        return ResponseEntity.ok(imageService.getImage());
    }

    @GetMapping("/latex-file")
    public ResponseEntity<byte[]> getLatexFile() throws Exception {
        byte[] contents = latexGeneratorService.getLatexFile();

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

package com.frauas.exercisegenerator.controllers;

import com.frauas.exercisegenerator.services.ImageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
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
}

package com.frauas.exercisegenerator.controllers;

import com.frauas.exercisegenerator.services.TestService;
import org.openapitools.api.TestApi;
import org.openapitools.model.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController implements TestApi {
    private TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("beispiel")
    public String test(){
        return testService.test();
    }

    public ResponseEntity<String> getTest(){
        return ResponseEntity.ok(testService.test());
    }

    public ResponseEntity<Book> getBook(@PathVariable("bookNumber") Integer bookNumber){
        return ResponseEntity.ok(testService.getBook(bookNumber));
    }
}

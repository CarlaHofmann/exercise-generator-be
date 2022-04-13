package com.frauas.exercisegenerator.controllers;

import com.frauas.exercisegenerator.services.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private TestService testService;

    public TestController(TestService testService){
        this.testService = testService;
    }

    @GetMapping(value = "/test")
    public String test(){
        return testService.test();
    }
}

package com.frauas.exercisegenerator.controllers;

import com.frauas.exercisegenerator.documents.Course;
import com.frauas.exercisegenerator.dtos.CreateCourseDto;
import com.frauas.exercisegenerator.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {
    @Autowired
    CourseService courseService;

    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable String id) {
        return courseService.getCourseById(id);
    }

    @PostMapping
    public Course createCourse(@RequestBody CreateCourseDto courseDto) {
        return courseService.createCourse(courseDto);
    }

    @DeleteMapping("/{id}")
    public void deleteCaourse(@PathVariable String id) {
        courseService.deleteCourseById(id);
    }
}

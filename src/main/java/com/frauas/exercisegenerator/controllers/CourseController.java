package com.frauas.exercisegenerator.controllers;

import java.util.List;

import com.frauas.exercisegenerator.documents.Course;
import com.frauas.exercisegenerator.dtos.CreateCourseDto;
import com.frauas.exercisegenerator.services.CourseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

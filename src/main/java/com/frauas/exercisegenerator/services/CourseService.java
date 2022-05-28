package com.frauas.exercisegenerator.services;

import com.frauas.exercisegenerator.documents.Course;
import com.frauas.exercisegenerator.dtos.CreateCourseDto;
import com.frauas.exercisegenerator.repositories.CourseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

@Service
public class CourseService {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(String id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new HttpServerErrorException(HttpStatus.NOT_FOUND));
    }

    public Course createCourse(CreateCourseDto courseDto) {
        Course course = modelMapper.map(courseDto, Course.class);

        return courseRepository.save(course);
    }

    public void deleteCourseById(String id) {
        courseRepository.deleteById(id);
    }
}

package com.frauas.exercisegenerator.helpers;

import java.util.ArrayList;
import java.util.List;

import com.frauas.exercisegenerator.documents.Course;
import com.frauas.exercisegenerator.dtos.CreateCourseDto;
import com.frauas.exercisegenerator.repositories.CourseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseUpsertHelper {
    @Autowired
    CourseRepository courseRepository;

    public ArrayList<Course> upsertCoursesFromDto(List<CreateCourseDto> dtoCourses) {
        ArrayList<Course> courses = new ArrayList<>();

        dtoCourses.forEach(courseDto -> {
            Course course = this.courseRepository.findByName(courseDto.getName());

            if (course == null) {
                course = Course.builder()
                        .name(courseDto.getName())
                        .build();

                course = this.courseRepository.save(course);
            }

            courses.add(course);
        });

        return courses;
    }
}

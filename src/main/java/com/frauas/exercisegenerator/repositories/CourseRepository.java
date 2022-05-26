package com.frauas.exercisegenerator.repositories;

import com.frauas.exercisegenerator.documents.Course;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<Course, String> {

    Course findByName(String name);
}

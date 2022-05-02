package com.frauas.exercisegenerator.repositories;

import com.frauas.exercisegenerator.documents.Author;
import com.frauas.exercisegenerator.documents.Exercise;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExerciseRepository extends MongoRepository<Exercise, String> {

    Exercise findByAuthor_Name(String name);
}

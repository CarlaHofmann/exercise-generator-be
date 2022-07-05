package com.frauas.exercisegenerator.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.documents.Sheet;

public interface SheetRepository extends MongoRepository<Sheet, String> {
    public List<Sheet> findByExercisesContaining(String id);

    public List<Sheet> findByExercisesContaining(Exercise exercise);

    Sheet findByAuthor_username(String name);
}

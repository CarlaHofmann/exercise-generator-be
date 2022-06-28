package com.frauas.exercisegenerator.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.documents.Sheet;

public interface SheetRepository extends MongoRepository<Sheet, String> {

    Sheet findByAuthor_Name(String name);

    public List<Sheet> findByExercisesContaining(String id);

    public List<Sheet> findByExercisesContaining(Exercise exercise);
}

package com.frauas.exercisegenerator.repositories;

import com.frauas.exercisegenerator.documents.Sheet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SheetRepository extends MongoRepository<Sheet, String> {

    Sheet findByAuthor_Name(String name);
}

package com.frauas.exercisegenerator.repositories;

import com.frauas.exercisegenerator.documents.Category;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<Category, String> {

}

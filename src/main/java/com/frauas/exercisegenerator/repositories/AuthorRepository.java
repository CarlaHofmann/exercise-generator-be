package com.frauas.exercisegenerator.repositories;

import com.frauas.exercisegenerator.documents.Author;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuthorRepository extends MongoRepository<Author, String> {

    Author findByName(String name);
}

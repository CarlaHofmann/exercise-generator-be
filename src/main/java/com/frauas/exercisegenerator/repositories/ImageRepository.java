package com.frauas.exercisegenerator.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.frauas.exercisegenerator.documents.Image;

public interface ImageRepository extends MongoRepository<Image, String> {
}

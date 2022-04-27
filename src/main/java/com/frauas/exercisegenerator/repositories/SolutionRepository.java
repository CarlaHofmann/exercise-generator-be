package com.frauas.exercisegenerator.repositories;

import com.frauas.exercisegenerator.documents.Solution;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SolutionRepository extends MongoRepository<Solution, String> {
}

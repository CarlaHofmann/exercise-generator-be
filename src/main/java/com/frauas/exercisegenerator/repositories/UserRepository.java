package com.frauas.exercisegenerator.repositories;

import com.frauas.exercisegenerator.documents.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);

    void deleteUserByUsername(String username);
}

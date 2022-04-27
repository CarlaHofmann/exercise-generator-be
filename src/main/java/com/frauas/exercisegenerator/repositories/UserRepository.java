package com.frauas.exercisegenerator.repositories;

import com.frauas.exercisegenerator.documents.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    User findByUserName(String userName);
}

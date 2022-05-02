package com.frauas.exercisegenerator.services;

import org.openapitools.model.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LoginService {

    public void logIn(LoginRequest loginData) {
        System.out.println(loginData);
        if (!loginData.equals(new LoginRequest().name("Simon").password("Passwort"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "wrong login data");
        }
    }
}

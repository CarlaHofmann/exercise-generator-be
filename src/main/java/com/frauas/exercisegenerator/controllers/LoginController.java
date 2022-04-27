package com.frauas.exercisegenerator.controllers;

import com.frauas.exercisegenerator.services.LoginService;
import org.openapitools.api.LoginApi;
import org.openapitools.model.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController implements LoginApi {
    private LoginService loginService;

    public LoginController(LoginService loginService){
        this.loginService = loginService;
    }

    public ResponseEntity<Void> logIn(LoginRequest loginData){
        loginService.logIn(loginData);
        return ResponseEntity.ok().build();
    }
}

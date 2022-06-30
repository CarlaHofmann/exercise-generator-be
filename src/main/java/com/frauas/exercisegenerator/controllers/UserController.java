package com.frauas.exercisegenerator.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frauas.exercisegenerator.documents.User;
import com.frauas.exercisegenerator.dtos.CreateUserDto;
import com.frauas.exercisegenerator.services.UserService;
import com.frauas.exercisegenerator.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    TokenUtil tokenUtil;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{username}")
    public Optional<User> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @DeleteMapping("/{username}")
    public void deleteExercise(@PathVariable String username) {
        userService.deleteUserByUsername(username);
    }

    @GetMapping("/refreshtoken")
    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refreshtoken = authorizationHeader.substring("Bearer ".length());

                if (tokenUtil.validateToken(refreshtoken)) {
                    User user = userService.getUserByUsername(tokenUtil.getUsernameFromToken(refreshtoken)).get();
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    if (user.isAdmin()) {
                        authorities.add(new SimpleGrantedAuthority("admin"));
                    }
                    authorities.add(new SimpleGrantedAuthority("professor"));
                    org.springframework.security.core.userdetails.User secUser = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
                    String accessToken = tokenUtil.genAccessToken(request.getRequestURI(), secUser);
                    Map<String, String> tokens = new HashMap<>();
                    tokens.put("accessToken", accessToken);
                    tokens.put("refreshToken", refreshtoken);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), tokens);
                } else {
                    response.sendError(FORBIDDEN.value());
                }
            } catch (Exception e) {
                response.setHeader("error", e.getMessage());

                Map<String, String> error = new HashMap<>();
                error.put("Error in:", "UserController.refreshToken");
                error.put("errorMessage", e.getMessage());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("RefreshToken is missing");
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping
    public User createUser(@RequestBody CreateUserDto userDto) {
        return userService.createUser(userDto);
    }
}

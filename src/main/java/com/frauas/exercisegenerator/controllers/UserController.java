package com.frauas.exercisegenerator.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frauas.exercisegenerator.documents.Category;
import com.frauas.exercisegenerator.documents.User;
import com.frauas.exercisegenerator.dtos.CreateCategoryDto;
import com.frauas.exercisegenerator.dtos.CreateUserDto;
import com.frauas.exercisegenerator.services.CategoryService;
import com.frauas.exercisegenerator.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/users")
public class UserController
{
	@Autowired
	UserService userService;

	@GetMapping
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/id")
	public Optional<User> getUserById(@PathVariable String id) {
		return userService.getUserById(id);
	}

	@GetMapping("/username")
	public Optional<User> getUserByusername(@PathVariable String username) {
		return userService.getUserByUsername(username);
	}

	@GetMapping("/refreshtoken")
	public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String authorizationHeader = request.getHeader(AUTHORIZATION);

		if(authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
		{
			try
			{
				String refreshtoken = authorizationHeader.substring("Bearer ".length());
				Algorithm algorithm = Algorithm.HMAC256("superDollesGeheimnis".getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build();
				DecodedJWT decodedJWT = verifier.verify(refreshtoken);
				String username = decodedJWT.getSubject();
				User user = userService.getUserByUsername(username).get();
				//user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

				//TO_DO: Manage roles
				String accessToken = JWT.create()
						.withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
						.withIssuer(request.getRequestURI().toString())
						.withClaim("roles", "simpleUser")
						.sign(algorithm);

				Map<String, String> tokens = new HashMap<>();
				tokens.put("accessToken", accessToken);
				tokens.put("refreshToken", refreshtoken);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);
			}
			catch (Exception e)
			{
				response.setHeader("error", e.getMessage());

				//response.sendError(FORBIDDEN.value());

				Map<String, String> error = new HashMap<>();
				error.put("errorMessage", e.getMessage());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}
		}
		else {
			throw new RuntimeException("RefreshToken is missing");
		}

		return ResponseEntity.ok().build();
	}

	@PostMapping
	public User createUser(@RequestBody CreateUserDto userDto) {
		return userService.createUser(userDto);
	}
}

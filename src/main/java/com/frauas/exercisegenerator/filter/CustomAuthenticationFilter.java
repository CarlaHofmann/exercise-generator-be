package com.frauas.exercisegenerator.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frauas.exercisegenerator.util.TokenUtil;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter
{
	private TokenUtil tokenUtil;
	private final AuthenticationManager authenticationManager;

	public CustomAuthenticationFilter(TokenUtil tokenUtil, AuthenticationManager authenticationManager)
	{
		this.authenticationManager = authenticationManager;
		this.tokenUtil = tokenUtil;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException
	{
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		return authenticationManager.authenticate(authenticationToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException
	{
		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authResult.getPrincipal();

		String accessToken = tokenUtil.genAccessToken(request.getRequestURI().toString(), user);
		String refreshToken = tokenUtil.genRefreshToken("request", user.getUsername());

		Map<String, String> tokens = new HashMap<>();
		tokens.put("accessToken", accessToken);
		tokens.put("refreshToken", refreshToken);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		new ObjectMapper().writeValue(response.getOutputStream(), tokens);
	}

	/*@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException
	{
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		Map<String, String> error = new HashMap<>();
		error.put("errorMessage", "Hat nicht geklappt");
		new ObjectMapper().writeValue(response.getOutputStream(), error);
	}*/
}

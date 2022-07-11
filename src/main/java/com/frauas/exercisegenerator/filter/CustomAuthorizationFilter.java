package com.frauas.exercisegenerator.filter;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frauas.exercisegenerator.util.TokenUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    TokenUtil tokenUtil;

    public CustomAuthorizationFilter(TokenUtil tokenUtil) {
        super();
        this.tokenUtil = tokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // The login page has to be accessible for everyone or you cant log in
        if (request.getServletPath().equals("/login") || request.getServletPath().equals("/users/refreshtoken")) {
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring("Bearer ".length());

                try {
                    if (tokenUtil.validateToken(token)) {
                        String roles = tokenUtil.getRolesFromToken(token)[0];
                        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

                        if (roles.contains("admin")) {
                            authorities.add(new SimpleGrantedAuthority("admin"));
                        }
                        if (roles.contains("professor")) {
                            authorities.add(new SimpleGrantedAuthority("professor"));
                        }

                        // Tells Spring which Authority the current user has
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                tokenUtil.getUsernameFromToken(token), null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        filterChain.doFilter(request, response);
                    } else {
                        response.sendError(UNAUTHORIZED.value());
                    }
                } catch (Exception e) {
                    response.setHeader("error", e.getMessage());

                    Map<String, String> error = new HashMap<>();
                    error.put("Error in:", "CustomAuthorizationFilter.doFilterInternal");
                    error.put("errorMessage", e.getMessage());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);

                    log.warn("Authorization error", e);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}

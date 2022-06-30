package com.frauas.exercisegenerator.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.frauas.exercisegenerator.documents.User;
import com.frauas.exercisegenerator.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.stream.Collectors;

public class TokenUtil {
    @Autowired
    UserService userService;

    //Aktuell 5 Minuten
	private final int accessTokenDuration = 5 * 60 * 1000;

    //Aktuell 10 Stunden
	private final int refreshTokenDuration = 10 * 60 * 60 * 1000;

    private final String secret = "superDollesGeheimnis";

    private Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());

    public TokenUtil() {

    }

    public String genRefreshToken(String requestedURI, User user) {
        String token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenDuration))
                .withIssuer(requestedURI)
                .sign(algorithm);

        return token;
    }

    public String genRefreshToken(String requestedURI, String username) {
        String token = JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenDuration))
                .withIssuer(requestedURI)
                .sign(algorithm);

        return token;
    }

    public String genAccessToken(String requestedURI, org.springframework.security.core.userdetails.User user) {
        String token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenDuration))
                .withIssuer(requestedURI)
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining()))
                .sign(algorithm);

        return token;
    }

    public boolean validateToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();

        try {
            verifier.verify(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String getUsernameFromToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();

        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getSubject();
    }

    public String[] getRolesFromToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);

        String[] array = decodedJWT.getClaim("roles").asArray(String.class);

        //Only 1 role
        if (array == null) {
            String[] single = new String[1];
            single[0] = decodedJWT.getClaim("roles").asString();
            return single;
        }
        return array;
    }
}

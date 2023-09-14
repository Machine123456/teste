package com.cap.authenticationservice.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.cap.authenticationservice.model.User;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;
    
    private Set<String> invalidTokens = new HashSet<>(); // Store invalid tokens here

    public String generateToken(User user){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                .withIssuer("authentication-service")
                .withSubject(user.getUsername())
                .withExpiresAt(Instant.now().plusSeconds(1000))
                .sign(algorithm);
            return token;
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error while generating token", e);
        }
    }

    public String validateToken(String token){

        if (invalidTokens.contains(token)) {
            System.out.println("Invalid Token, the user of the token was logged off");
            return "";
        }

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            var jwt = JWT.require(algorithm).withIssuer("authentication-service").build();
            var verify = jwt.verify(token);
            var username = verify.getSubject();
            return username;
        } catch (JWTVerificationException e) {
            // Log the specific exception for troubleshooting
        if (e instanceof TokenExpiredException) {
            System.out.println("Token has expired.");
        } else if (e instanceof InvalidClaimException) {
            System.out.println("Invalid claim in the token.");
        } else {
            System.out.println("Token verification failed: " + e.getMessage());
        }
            return "";
        }
    }

    public void invalidateToken(String token) {
        invalidTokens.add(token);
    }

    private Instant genExpirationDate(int seconds){
        return LocalDateTime.now().plusSeconds(seconds).toInstant(ZoneOffset.of("+01:00"));
    }

    public String recoverToken(HttpServletRequest request) {
      
        var authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            String token = authHeader.replace("Bearer", "").trim();
            return token;
        }

        var cookies = request.getCookies();
        if(cookies != null) {
            for (var cookie : cookies)
                if(cookie.getName().equals("token"))
                    return cookie.getValue();
        }

        return null;
    
    }
}
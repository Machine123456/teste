package com.cap.authenticationservice.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cap.authenticationservice.dto.UserRequest;

import com.cap.authenticationservice.security.TokenService;
import com.cap.authenticationservice.service.AuthenticationService;
import com.cap.authenticationservice.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController{

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<String> registerUser(@RequestBody UserRequest userRequest, HttpServletResponse response){

        try {
            var user = authenticationService.mapToUser(userRequest);
            userService.saveUser(user);

            String token = authenticationService.authenticateUser(userRequest);
            response.addCookie(generateTokenCookie(token));

            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create user:\n" + e.getMessage());
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<String> loginUser(@RequestBody UserRequest userRequest, HttpServletResponse response) {
        try {
            System.out.println("Received login request: " + userRequest.getUsername() + " " + userRequest.getPassword());
            String token = authenticationService.authenticateUser(userRequest);
            response.addCookie(generateTokenCookie(token));
            System.out.println("Login Successfully");

            return ResponseEntity.ok(token);
        } catch ( Exception e) {
            System.out.println("Login Request Failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login Failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<String> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        try {

            String token = tokenService.recoverToken(request);

            if(token.isBlank())
                return ResponseEntity.ok("Logout successful: The user was not logged in");

            response.addCookie(generateEmptyTokenCookie());

            // Invalidate the token on the server (this is a simplified example)
            // In a real application, you might implement a token blacklist or invalidation mechanism
            tokenService.invalidateToken(token);

            return ResponseEntity.ok("Logout successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Logout failed: " + e.getMessage());
        }
    }

    private Cookie generateTokenCookie(String token){
        return token == null ? generateEmptyTokenCookie() : genTokenCookie(token ,(int) TimeUnit.HOURS.toSeconds(1));
    }

    private Cookie generateEmptyTokenCookie() { return genTokenCookie("",0) ; };

    private Cookie genTokenCookie(String token,int maxAge){
         Cookie cookie = new Cookie("token", token);
                cookie.setMaxAge(maxAge); // Set cookie expiration time as needed
                cookie.setPath("/"); // Set the cookie path as needed
                cookie.setHttpOnly(true);

        return cookie;
    }


    
}
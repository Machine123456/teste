package com.cap.authenticationservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cap.authenticationservice.dto.UserResponse;
import com.cap.authenticationservice.security.TokenService;
import com.cap.authenticationservice.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final TokenService tokenService;

    @GetMapping("/users")
    @ResponseBody
    public ResponseEntity<List<UserResponse>> getUsers() {
       
       try {
            var users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        }
    }

    @CrossOrigin
    @GetMapping("/getUserFromToken")
    @ResponseBody
    public ResponseEntity<UserResponse> getUserFromToken(HttpServletRequest request) {
    try {
        String token = tokenService.recoverToken(request);
        String uname = tokenService.validateToken(token);

        var user = userService.findUserByUsername(uname);
        return ResponseEntity.ok(user);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }
    }
}

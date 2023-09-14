package com.cap.authenticationservice.service;

import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cap.authenticationservice.dto.UserRequest;
import com.cap.authenticationservice.model.Role;
import com.cap.authenticationservice.model.User;
import com.cap.authenticationservice.repository.RoleRepository;
import com.cap.authenticationservice.security.TokenService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

    
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;  
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;


    public String authenticateUser(UserRequest userRequest) throws AuthenticationException {

        // Create a UsernamePasswordAuthenticationToken with the provided username and password
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword());
        
        // Use the AuthenticationManager to authenticate the user
        Authentication authentication = authenticationManager.authenticate(authRequest);

        var token = tokenService.generateToken((User)authentication.getPrincipal());

        return token;
    }

  

    
    public User mapToUser(UserRequest userRequest) {

        Role userRole = roleRepository.findByAuthority("ROLE_USER").orElseThrow(() -> new IllegalArgumentException("Roles are not properly fetched in the database"));
        // Check requested authorities TODO
        User user = User.builder()
        .username(userRequest.getUsername())
        .email(userRequest.getEmail())
        .password(passwordEncoder.encode(userRequest.getPassword()))
        .authorities(Set.of(userRole))
        .build();
        
        return user;
    }

    
   

}

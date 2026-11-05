package com.skillgap.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillgap.dto.auth.AuthResponseDto;
import com.skillgap.dto.auth.LoginRequestDto;
import com.skillgap.dto.auth.RegisterRequestDto;
import com.skillgap.service.auth.AuthenticationService;
import com.skillgap.service.auth.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;    
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/auth")
@RequiredArgsConstructor 
@io.swagger.v3.oas.annotations.tags.Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthenticationController {

    private final AuthenticationService authService;

    @Operation(summary = "Register a new user", 
               description = "Validates registration data, checks if the email is already in use, hashes the password, saves the user to the database with ROLE_USER, and returns a JWT token.")
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @Operation(summary = "Authenticate an existing user", 
               description = "Verifies user credentials (email and password), and if valid, generates and returns a new JWT token for subsequent authenticated requests.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }
        

}

package com.skillgap.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.skillgap.exception.auth.PasswordsDoNotMatchException;
import com.skillgap.exception.auth.UserAlreadyExistsException;

import lombok.extern.slf4j.Slf4j;


@ControllerAdvice
@Slf4j 
public class GlobalExceptionHandler {

    @ExceptionHandler(SkillNotFoundException.class)
    public ResponseEntity<Object> handleSkillNotFoundException(SkillNotFoundException ex) {
        return new ResponseEntity<>(
            Map.of("error", "Not Found", "message", ex.getMessage()), 
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(NoMarketDataException.class)
    public ResponseEntity<Object> handleNoMarketDataException(NoMarketDataException ex) {
        return new ResponseEntity<>(
            Map.of("error", "Not Found", "message", ex.getMessage()), 
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
            Map.of("error", "Not found", "message", ex.getMessage()),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ProviderNotFoundException.class)
    public ResponseEntity<Object> handleProviderNotFoundException(ProviderNotFoundException ex) {
        return new ResponseEntity<>(
            Map.of("error", "Not found", "message", ex.getMessage()),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(SkillAlreadyExistsException.class)
    public ResponseEntity<Object> handleSkillAlreadyExistsException(SkillAlreadyExistsException ex) {
        return new ResponseEntity<>(
            Map.of("error", "Conflict", "message", ex.getMessage()),
            HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(AliasAlreadyExistsException.class)
    public ResponseEntity<Object> handleAliasAlreadyExistsException(AliasAlreadyExistsException ex) {
        return new ResponseEntity<>(
            Map.of("error", "Conflict", "message", ex.getMessage()),
            HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(JobOfferNotFoundException.class)
    public ResponseEntity<Object> handleJobOfferNotFoundException(JobOfferNotFoundException ex) {
        return new ResponseEntity<>(
            Map.of("error", "Not Found", "message", ex.getMessage()),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(PasswordsDoNotMatchException.class)
    public ResponseEntity<Object> handlePasswordDoNotMatchException(PasswordsDoNotMatchException ex) {
        return new ResponseEntity<>(
            Map.of("error", "Bad Request", "message", ex.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(
            Map.of("error", "Conflict", "message", ex.getMessage()),
            HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {

        log.warn("Failed login attempt: {}", ex.getMessage());

        return new ResponseEntity<>(
            Map.of("error", "Unauthorized", "message", "Invalid email or password"),
            HttpStatus.UNAUTHORIZED
        );
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(
            Map.of("error", "Internal Server Error", "message", ex.getMessage()), 
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

}

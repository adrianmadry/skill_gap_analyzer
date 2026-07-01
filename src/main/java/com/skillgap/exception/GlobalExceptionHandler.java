package com.skillgap.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.skillgap.exception.SkillNotFoundException;

@ControllerAdvice
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

package com.migrosone.controller.handler;

import com.migrosone.application.exception.CourierNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CourierNotFoundException.class)
    public ResponseEntity<?> handleCourierNotFound(CourierNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupported() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Map.of("error", "HTTP method not allowed for this endpoint"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("Internal server error"));
    }

    private Map<String, Object> error(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("timestamp", LocalDateTime.now());
        return map;
    }
}

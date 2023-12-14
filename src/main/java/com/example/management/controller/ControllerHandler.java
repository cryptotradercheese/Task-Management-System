package com.example.management.controller;

import com.example.management.exception.NoAuthorityException;
import com.example.management.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
public class ControllerHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleRunTimeException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto("Wrong arguments format"));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleNoAuthorityException(NoAuthorityException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponseDto(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(e.getMessage()));
    }
}


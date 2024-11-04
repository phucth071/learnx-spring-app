package com.hcmute.utezbe.exception;

import com.hcmute.utezbe.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    ResponseEntity<Response> handlingException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body(Response.builder().code(400).success(false).message(e.getMessage()).build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<Response> handlingResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Response.builder().code(409).success(false).message(e.getMessage()).build());
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<Response> handlingAuthenticationException(AuthenticationException e) {
        return ResponseEntity.badRequest().body(Response.builder().code(403).success(false).message(e.getMessage()).build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<Response> handlingAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.badRequest().body(Response.builder().code(403).success(false).message(e.getMessage()).build());
    }
}

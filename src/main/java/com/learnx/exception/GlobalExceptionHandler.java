package com.learnx.exception;

import com.learnx.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    ResponseEntity<Response<?>> handlingException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body(Response.builder().code(400).success(false).message(e.getMessage()).build());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Object>> handleValidationException(
            MethodArgumentNotValidException exception) {

        String error = exception.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.builder().code(400).success(false).message(error).build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<Response<?>> handlingResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(200).body(Response.builder().code(200).success(false).message(e.getMessage()).build());
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<Response<?>> handlingAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(403).body(Response.builder().code(403).success(false).message(e.getMessage()).build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<Response<?>> handlingAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(403).body(Response.builder().code(403).success(false).message(e.getMessage()).build());
    }
}

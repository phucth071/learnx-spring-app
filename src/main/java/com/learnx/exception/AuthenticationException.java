package com.learnx.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String s) {
        super(s);
    }

    public AuthenticationException() {
        super("An error occurred");
    }
}

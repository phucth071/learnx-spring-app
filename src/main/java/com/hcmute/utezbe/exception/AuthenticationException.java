package com.hcmute.utezbe.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String s) {
        super(s);
    }

    public AuthenticationException() {
        super("An error occurred");
    }
}

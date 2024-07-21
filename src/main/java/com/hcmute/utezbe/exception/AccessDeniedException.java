package com.hcmute.utezbe.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String s) {
        super(s);
    }

    public AccessDeniedException() {
        super("Access denied");
    }
}

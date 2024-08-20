package com.hcmute.utezbe.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String s) {
        super(s);
    }

    public AccessDeniedException() {
        super("You do not have permission to do this action!");
    }
}

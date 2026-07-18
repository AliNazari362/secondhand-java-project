package com.secondhand.exception;

public class PasswordIsNotCorrectException extends RuntimeException {
    public PasswordIsNotCorrectException(String message) {
        super(message);
    }
}

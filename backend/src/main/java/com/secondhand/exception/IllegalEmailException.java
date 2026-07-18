package com.secondhand.exception;

public class IllegalEmailException extends RuntimeException {
    public IllegalEmailException(String message) {
        super(message);
    }
}

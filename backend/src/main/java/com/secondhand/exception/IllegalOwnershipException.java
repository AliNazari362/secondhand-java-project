package com.secondhand.exception;

public class IllegalOwnershipException extends RuntimeException {
    public IllegalOwnershipException(String message) {
        super(message);
    }
}

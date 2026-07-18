package com.secondhand.exception;

public class RatingIsAlreadyExistException extends RuntimeException {
    public RatingIsAlreadyExistException(String message) {
        super(message);
    }
}

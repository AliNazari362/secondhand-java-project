package com.secondhand.exception;

public class CommentIsAlreadyExistException extends RuntimeException {
    public CommentIsAlreadyExistException(String message) {
        super(message);
    }
}

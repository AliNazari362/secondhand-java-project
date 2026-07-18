package com.secondhand.exception;

public class ChatroomNotFoundException extends RuntimeException {
    public ChatroomNotFoundException(String message) {
        super(message);
    }
}

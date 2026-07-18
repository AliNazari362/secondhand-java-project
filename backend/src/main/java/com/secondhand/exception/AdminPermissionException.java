package com.secondhand.exception;

public class AdminPermissionException extends RuntimeException {
    public AdminPermissionException(String message) {
        super(message);
    }
}

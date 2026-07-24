package com.secondhand.exception;

/**
 * Exception thrown when an authenticated user attempts to perform an action they are
 * not authorized to carry out (e.g., accessing an admin-only endpoint).
 *
 * <p>This exception is mapped to HTTP 403 Forbidden by {@link GlobalExceptionHandler}.</p>
 */
public class ForbiddenException extends RuntimeException {

    /**
     * Constructs a new {@code ForbiddenException} with the specified detail message.
     *
     * @param message a human-readable description of why the action is forbidden
     */
    public ForbiddenException(String message) {
        super(message);
    }
}

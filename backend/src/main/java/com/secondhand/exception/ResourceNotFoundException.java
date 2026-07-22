package com.secondhand.exception;

/**
 * Exception thrown when a requested resource cannot be found in the system
 * (e.g., looking up a user or advertisement by an ID that does not exist).
 *
 * <p>This exception is mapped to HTTP 404 Not Found by {@link GlobalExceptionHandler}.</p>
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code ResourceNotFoundException} with the specified detail message.
     *
     * @param message a human-readable description identifying the missing resource
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

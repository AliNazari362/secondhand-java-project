package com.secondhand.exception;

/**
 * Exception thrown when a client attempts to create a resource that already exists
 * (e.g., registering with a username or email that is already taken).
 *
 * <p>This exception is mapped to HTTP 409 Conflict by {@link GlobalExceptionHandler}.</p>
 */
public class ResourceAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new {@code ResourceAlreadyExistsException} with the specified detail message.
     *
     * @param message a human-readable description identifying the conflicting resource
     */
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
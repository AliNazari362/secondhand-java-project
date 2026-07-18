package SpecialException;

public class CommentIsAlreadyExistException extends RuntimeException {
    public CommentIsAlreadyExistException(String message) {
        super(message);
    }
}

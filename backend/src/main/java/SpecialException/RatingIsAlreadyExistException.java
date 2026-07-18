package SpecialException;

public class RatingIsAlreadyExistException extends RuntimeException {
    public RatingIsAlreadyExistException(String message) {
        super(message);
    }
}

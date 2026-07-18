package SpecialException;

public class IllegalFavoriteException extends RuntimeException {
    public IllegalFavoriteException(String message) {
        super(message);
    }
}

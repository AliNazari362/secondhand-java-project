package SpecialException;

public class AccountIsNotAccessibleException extends RuntimeException {
    public AccountIsNotAccessibleException(String message) {
        super(message);
    }
}

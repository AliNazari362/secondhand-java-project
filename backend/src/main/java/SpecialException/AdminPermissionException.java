package SpecialException;

public class AdminPermissionException extends RuntimeException {
    public AdminPermissionException(String message) {
        super(message);
    }
}

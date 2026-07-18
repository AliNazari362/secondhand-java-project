package SpecialException;

public class NotParticipantException extends RuntimeException {
    public NotParticipantException(String message) {
        super(message);
    }
}

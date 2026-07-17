package SpecialException;

public class AdvertisementIsAlreadySoldException extends RuntimeException {
    public AdvertisementIsAlreadySoldException(String message) {
        super(message);
    }
}

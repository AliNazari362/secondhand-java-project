package com.secondhand.exception;

public class AdvertisementIsNotAvailableException extends RuntimeException {
    public AdvertisementIsNotAvailableException(String message) {
        super(message);
    }
}

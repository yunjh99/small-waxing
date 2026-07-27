package com.example.waxing.global.error.exception;

public class InvalidEventImageException extends ApiException {

    public InvalidEventImageException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}

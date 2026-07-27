package com.example.waxing.global.error.exception;

public class EventEndedAccessException extends ApiException {

    private static final String MESSAGE = "종료된 이벤트입니다.";

    public EventEndedAccessException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 403;
    }
}
package com.example.waxing.global.error.exception;

public class EventDeletedAccessException extends ApiException {

    private static final String MESSAGE = "삭제된 이벤트입니다.";

    public EventDeletedAccessException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 403;
    }
}
package com.example.waxing.global.error.exception;

public class EventAlreadyDeletedException extends ApiException {

    private static final String MESSAGE = "이미 삭제된 이벤트입니다.";

    public EventAlreadyDeletedException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
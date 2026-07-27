package com.example.waxing.global.error.exception;

public class FaqAlreadyDeletedException extends ApiException {

    private static final String MESSAGE = "이미 삭제된 공지입니다.";

    public FaqAlreadyDeletedException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
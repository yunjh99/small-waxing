package com.example.waxing.global.error.exception;

public class NoticeNotDeletedException extends ApiException {

    private static final String MESSAGE = "삭제되지 않은 이벤트입니다.";

    public NoticeNotDeletedException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
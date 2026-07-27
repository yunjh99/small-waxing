package com.example.waxing.global.error.exception;

/** 종료된 이벤트에 복구 요청을 보낸 경우 발생한다. */
public class EventEndedRestoreException extends ApiException {

    private static final String MESSAGE = "종료된 이벤트는 복구할 수 없습니다.";

    public EventEndedRestoreException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}

package com.example.waxing.global.error.exception;

public class EventNotFoundException extends ApiException {

	private static final String MESSAGE = "이벤트를 찾을 수 없습니다";

	public EventNotFoundException() {
		super(MESSAGE);
	}

	@Override
	public int getStatusCode() {
		return 404;
	}
}

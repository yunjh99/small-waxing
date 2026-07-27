package com.example.waxing.global.error.exception;

public class NoticeNotFoundException extends ApiException {

	private static final String MESSAGE = "공지사항를 찾을 수 없습니다";

	public NoticeNotFoundException() {
		super(MESSAGE);
	}

	public NoticeNotFoundException(String fieldName, String message) {
		super(MESSAGE);
		addValidation(fieldName, message);
	}

	@Override
	public int getStatusCode() {
		return 404;
	}
}

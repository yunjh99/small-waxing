package com.example.waxing.global.error.exception;

public class FaqNotFoundException extends ApiException {

	private static final String MESSAGE = "FAQ를 찾을 수 없습니다";

	public FaqNotFoundException() {
		super(MESSAGE);
	}

	public FaqNotFoundException(String fieldName, String message) {
		super(MESSAGE);
		addValidation(fieldName, message);
	}

	@Override
	public int getStatusCode() {
		return 404;
	}
}

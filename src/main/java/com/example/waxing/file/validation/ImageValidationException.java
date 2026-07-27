package com.example.waxing.file.validation;

/** 이미지 파일 공통 검증에 실패했을 때 발생하는 내부 예외다. */
public class ImageValidationException extends RuntimeException {

    public ImageValidationException(String message) {
        super(message);
    }
}

package com.example.waxing.global.error.exception;

/** 공지사항에 허용되지 않는 이미지가 첨부됐을 때 사용하는 400 예외다. */
public class InvalidNoticeImageException extends ApiException {

    public InvalidNoticeImageException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}

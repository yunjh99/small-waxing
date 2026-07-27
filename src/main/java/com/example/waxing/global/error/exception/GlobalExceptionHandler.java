package com.example.waxing.global.error.exception;

import com.example.waxing.global.common.FailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  @ResponseBody
  public ResponseEntity<FailResponse> handleApiException(ApiException ex) {

    FailResponse response = FailResponse.builder()
            .status(ex.getStatusCode())
            .message(ex.getMessage())
            .build();

    return ResponseEntity
            .status(ex.getStatusCode())
            .body(response);
  }

  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ResponseEntity<FailResponse> handleException(Exception ex) {

    FailResponse response = FailResponse.builder()
            .status(500)
            .message("서버 오류가 발생했습니다.")
            .build();

    return ResponseEntity
            .status(500)
            .body(response);
  }
}

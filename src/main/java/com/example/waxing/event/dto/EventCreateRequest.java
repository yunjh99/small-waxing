package com.example.waxing.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

/**
 * 관리자 이벤트 생성 요청.
 *
 * 이미지의 확장자와 크기는 파일 검증기에서 별도로 확인한다.
 */
public record EventCreateRequest(
        @NotBlank(message = "제목은 필수입니다.")
        String title,

        String content,

        @NotNull(message = "시작일은 필수입니다.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,

        @NotNull(message = "종료일은 필수입니다.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,

        MultipartFile thumbnail,
        MultipartFile bodyImage
) {

    /**
     * 종료일은 시작일과 같거나 이후여야 한다.
     * null 여부는 각 필드의 @NotNull이 검증한다.
     */
    @AssertTrue(message = "종료일은 시작일보다 빠를 수 없습니다.")
    public boolean isDateRangeValid() {
        return startDate == null
                || endDate == null
                || !endDate.isBefore(startDate);
    }
}

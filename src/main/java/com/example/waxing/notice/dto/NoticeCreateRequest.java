package com.example.waxing.notice.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record NoticeCreateRequest(
        @NotBlank(message = "제목은 필수입니다.")
        String title,

        String content,

        MultipartFile bodyImage
) {
}

package com.example.waxing.faq.dto;

import com.example.waxing.faq.domain.FaqType;
import jakarta.validation.constraints.NotBlank;

public record FaqCreateRequest(
        @NotBlank(message = "질문은 필수입니다.")
        String title,

        @NotBlank(message = "답변은 필수입니다.")
        String content,

        FaqType type
) {
}

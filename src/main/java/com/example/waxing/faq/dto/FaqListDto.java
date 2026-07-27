package com.example.waxing.faq.dto;

import com.example.waxing.faq.domain.FaqType;

import java.time.LocalDateTime;

public record FaqListDto(
        Long id,
        FaqType type,
        String title,
        String content,
        LocalDateTime createdAt
) {
}

package com.example.waxing.notice.dto;

import java.time.LocalDateTime;

public record NoticeDetailDto(
        Long id,
        String title,
        String content,
        int viewCount,
        LocalDateTime createdAt,
        LocalDateTime deletedAt,
        String bodyImageUrl,
        String bodyImageName
) {
}

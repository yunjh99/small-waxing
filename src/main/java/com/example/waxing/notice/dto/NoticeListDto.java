package com.example.waxing.notice.dto;

import java.time.LocalDateTime;

public record NoticeListDto(
        Long id,
        String title,
        int viewCount,
        LocalDateTime createdAt,
        String userName
) {
}

package com.example.waxing.event.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EventDetailDto(
        Long id,
        String title,
        String content,
        LocalDate startDate,
        LocalDate endDate,
        Integer viewCount,
        LocalDateTime createdAt,
        LocalDateTime deletedAt,
        String thumbnailUrl,
        String thumbnailName,
        String bodyImageUrl,
        String bodyImageName
) {
}

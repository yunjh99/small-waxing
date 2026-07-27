package com.example.waxing.event.dto;

import java.time.LocalDate;

public record EventListDto (
    Long id,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    String thumbnailUrl
){
}

package com.example.waxing.popup.dto;

import com.example.waxing.popup.domain.Popup;

import java.time.LocalDate;

public record PopupDto(
        Long id,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String imageUrl,
        String imageName
) {
    public static PopupDto from(Popup popup) {
        return new PopupDto(
                popup.getId(),
                popup.getTitle(),
                popup.getStartDate(),
                popup.getEndDate(),
                "/files/" + popup.getImage().getId(),
                popup.getImage().getOriginalName()
        );
    }
}

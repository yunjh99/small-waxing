package com.example.waxing.siteimage.dto;

import com.example.waxing.siteimage.domain.SiteTextSlot;

public record SiteTextDto(
        SiteTextSlot slot,
        String title,
        String subtitle,
        String description,
        String horizontalPosition,
        String verticalPosition,
        boolean customText
) {
}

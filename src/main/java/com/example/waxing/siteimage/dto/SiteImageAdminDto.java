package com.example.waxing.siteimage.dto;

import com.example.waxing.siteimage.domain.SiteImageSlot;

public record SiteImageAdminDto(
        SiteImageSlot slot,
        String label,
        String imageUrl,
        String originalName,
        String recommendedSize,
        String menuKey,
        SiteTextDto text,
        boolean customImage
) {
}

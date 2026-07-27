package com.example.waxing.siteimage.dto;

public record GalleryImageDto(
        Long id,
        String imageUrl,
        String originalName,
        boolean uploaded
) {
}

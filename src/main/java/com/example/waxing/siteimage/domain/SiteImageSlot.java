package com.example.waxing.siteimage.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SiteImageSlot {
    MAIN_HERO("메인 상단", "/img/main/main_logo_2.png", "1920 × 810px", "main"),
    MAIN_SERVICE_1("메인 서비스 1 - 브라질리언", "/img/main/brazilian.png", "1751 × 898px", "main"),
    MAIN_SERVICE_2("메인 서비스 2 - 얼굴", "/img/main/face.png", "1751 × 898px", "main"),
    MAIN_SERVICE_3("메인 서비스 3 - 바디", "/img/main/body.png", "1751 × 898px", "main"),
    MAIN_SERVICE_4("메인 서비스 4 - 임산부", "/img/main/baby.png", "1751 × 898px", "main"),
    SPECIAL_HERO("스몰왁싱 특별함 상단", "/img/main/smallwaxing.png", "1920 × 820px", "special"),
    LOCATION_HERO("오시는 길 상단", "/img/main/woman.png", "1920 × 820px", "location"),
    GALLERY_HERO("둘러보기 상단", "/img/main/gallery.png", "1920 × 820px", "gallery"),
    SPECIAL_CONTENT_1("특별함 본문 1", "/img/smallwaxing/01.png", "696 × 490px", "special"),
    SPECIAL_CONTENT_2("특별함 본문 2", "/img/smallwaxing/02.png", "696 × 490px", "special"),
    SPECIAL_CONTENT_3("특별함 본문 3", "/img/smallwaxing/03.png", "696 × 490px", "special"),
    GALLERY_SLIDE_1("둘러보기 슬라이드 1", "/img/gallery/1.png", "1200 × 650px", "gallery"),
    GALLERY_SLIDE_2("둘러보기 슬라이드 2", "/img/gallery/2.png", "1200 × 650px", "gallery"),
    GALLERY_SLIDE_3("둘러보기 슬라이드 3", "/img/gallery/3.png", "1200 × 650px", "gallery"),
    GALLERY_SLIDE_4("둘러보기 슬라이드 4", "/img/gallery/4.png", "1200 × 650px", "gallery");

    private final String label;
    private final String defaultUrl;
    private final String recommendedSize;
    private final String menuKey;
}

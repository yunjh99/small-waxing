package com.example.waxing.siteimage.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SiteTextSlot {
    MAIN_HERO(SiteImageSlot.MAIN_HERO, "SMALL WAXING", "PROFESSIONAL WAXING SHOP", "", "center", "center"),
    MAIN_SERVICE_1(SiteImageSlot.MAIN_SERVICE_1, "브라질리언왁싱", "Brazilian\nWaxing", "피부 마찰을 줄여\n착색을 예방하는\n인기있는 왁싱!", "left", "top"),
    MAIN_SERVICE_2(SiteImageSlot.MAIN_SERVICE_2, "얼굴왁싱", "Face\nWaxing", "잔털과 각질을\n안전하게 제거하는\n페이스 왁싱!", "left", "top"),
    MAIN_SERVICE_3(SiteImageSlot.MAIN_SERVICE_3, "바디왁싱", "Body\nWaxing", "각질이 제거되며\n피부가 부드러워지고\n환해지는 왁싱!", "left", "top"),
    MAIN_SERVICE_4(SiteImageSlot.MAIN_SERVICE_4, "임산부왁싱", "Pregnant\nWaxing", "출산 전·후 쾌적하게\n상태를 유지하는\n안전한 임산부 왁싱!", "left", "top"),
    SPECIAL_HERO(SiteImageSlot.SPECIAL_HERO, "스몰왁싱의 특별함", "SPECIAL SMALL WAXING", "10년 업력의 전문성,\n노더블딥 원칙으로 완성하는 믿을 수 있는 왁싱", "right", "center"),
    LOCATION_HERO(SiteImageSlot.LOCATION_HERO, "오시는 길", "LOCATION", "", "center", "center"),
    GALLERY_HERO(SiteImageSlot.GALLERY_HERO, "스몰왁싱 공간", "SMALL WAXING SPACE", "스몰왁싱의 내부 공간을 소개합니다.\n언제나 청결하고 편안한 환경을 유지합니다.", "center", "center"),
    SPECIAL_CONTENT_1(SiteImageSlot.SPECIAL_CONTENT_1, "한 사람만을 위한\n섬세한 맞춤 왁싱", "PERSONAL CARE", "피부 상태와 모질, 컨디션을 세심하게 확인하고 고객 한 분 한 분에게 꼭 맞는 방법으로 편안하고 정교하게 시술합니다.", "left", "center"),
    SPECIAL_CONTENT_2(SiteImageSlot.SPECIAL_CONTENT_2, "기본을 지키는\n노더블딥 원칙", "CLEAN & SAFE", "한 번 사용한 스틱은 다시 왁스에 넣지 않습니다. 눈에 보이지 않는 부분까지 철저한 위생 원칙을 지켜 안심할 수 있습니다.", "left", "center"),
    SPECIAL_CONTENT_3(SiteImageSlot.SPECIAL_CONTENT_3, "경험으로 완성한\n숙련된 전문성", "PROFESSIONAL", "10년 이상의 현장 경험과 꾸준한 해외 교육을 바탕으로 자극과 통증은 줄이고 만족도 높은 결과를 만들어갑니다.", "left", "center"),
    SERVICE_CONTENT_BRAZILIAN(SiteImageSlot.MAIN_SERVICE_1, "섬세하고 편안한 브라질리언 왁싱", "BRAZILIAN WAXING", "개인의 피부 컨디션과 모질을 세심하게 확인한 뒤, 자극은 줄이고 깔끔함은 오래 유지될 수 있도록 맞춤 시술합니다. 프라이빗한 공간에서 편안하고 위생적인 관리를 경험해 보세요.", "left", "center"),
    SERVICE_CONTENT_FACE(SiteImageSlot.MAIN_SERVICE_2, "더 맑고 정돈된 인상, 페이스 왁싱", "FACE WAXING", "눈썹, 인중, 헤어라인과 얼굴 전체의 잔털을 섬세하게 정돈합니다. 메이크업 밀착력을 높이고 얼굴선을 또렷하게 살려 자연스럽고 깨끗한 인상을 만들어 드립니다.", "left", "center"),
    SERVICE_CONTENT_BODY(SiteImageSlot.MAIN_SERVICE_3, "원하는 부위를 매끈하게, 바디 왁싱", "BODY WAXING", "팔, 다리, 겨드랑이, 등과 배 등 필요한 부위만 선택해 관리할 수 있습니다. 피부 상태와 털의 방향을 고려한 섬세한 테크닉으로 매끈하고 단정한 바디라인을 완성합니다.", "left", "center"),
    SERVICE_CONTENT_PREGNANT(SiteImageSlot.MAIN_SERVICE_4, "엄마의 편안함을 생각한 임산부 왁싱", "PREGNANT WAXING", "임신 중 변화한 신체와 민감해진 피부를 충분히 고려해 편안한 자세와 안전한 방식으로 진행합니다. 출산 전 위생 관리가 필요한 시기에 부담 없이 받을 수 있도록 세심하게 케어합니다.", "left", "center");

    private final SiteImageSlot imageSlot;
    private final String defaultTitle;
    private final String defaultSubtitle;
    private final String defaultDescription;
    private final String defaultHorizontal;
    private final String defaultVertical;

    public static SiteTextSlot fromImageSlot(SiteImageSlot imageSlot) {
        for (SiteTextSlot slot : values()) {
            if (slot.imageSlot == imageSlot) return slot;
        }
        return null;
    }
}

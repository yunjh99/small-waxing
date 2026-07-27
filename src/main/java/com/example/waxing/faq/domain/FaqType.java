package com.example.waxing.faq.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FaqType {

    RESERVATION("reservation", "예약"),
    SERVICE("service", "서비스"),
    AVAILABLE("available", "가능 여부"),
    AFTERCARE("aftercare", "사후관리"),
    BUSINESS("business", "매장정보");

    private final String key;
    private final String label;   // 추가

    public static FaqType ofValue(String value) {

        for (FaqType type : values()) {
            if (type.key.equalsIgnoreCase(value)) {
                return type;
            }
        }

        throw new IllegalArgumentException("유효하지 않은 FAQ 타입입니다: " + value);
    }

}
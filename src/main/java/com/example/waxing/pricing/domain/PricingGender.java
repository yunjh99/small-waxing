package com.example.waxing.pricing.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PricingGender {
    FEMALE("여성"),
    MALE("남성");

    private final String label;
}

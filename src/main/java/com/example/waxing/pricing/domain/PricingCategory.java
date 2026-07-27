package com.example.waxing.pricing.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PricingCategory {
    FACE("FACE"),
    BODY("BODY");

    private final String label;
}

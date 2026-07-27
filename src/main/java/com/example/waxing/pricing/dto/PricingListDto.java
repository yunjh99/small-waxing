package com.example.waxing.pricing.dto;

import com.example.waxing.pricing.domain.Pricing;
import com.example.waxing.pricing.domain.PricingCategory;
import com.example.waxing.pricing.domain.PricingGender;

import java.math.BigDecimal;

public record PricingListDto(
        Long id,
        PricingGender gender,
        PricingCategory category,
        String name,
        BigDecimal price
) {

    public static PricingListDto from(Pricing pricing) {
        return new PricingListDto(
                pricing.getId(),
                pricing.getGender(),
                pricing.getCategory(),
                pricing.getName(),
                pricing.getPrice()
        );
    }
}

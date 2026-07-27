package com.example.waxing.pricing.dto;

import com.example.waxing.pricing.domain.Pricing;
import com.example.waxing.pricing.domain.PricingCategory;
import com.example.waxing.pricing.domain.PricingGender;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record PricingCreateRequest(
        @NotNull(message = "성별을 선택해 주세요.")
        PricingGender gender,

        @NotNull(message = "구분을 선택해 주세요.")
        PricingCategory category,

        @NotBlank(message = "메뉴명을 입력해 주세요.")
        String name,

        @DecimalMin(value = "0", message = "가격은 0원 이상이어야 합니다.")
        BigDecimal price,

        @PositiveOrZero(message = "노출 순서는 0 이상이어야 합니다.")
        int displayOrder
) {

    public static PricingCreateRequest from(Pricing pricing) {
        return new PricingCreateRequest(
                pricing.getGender(),
                pricing.getCategory(),
                pricing.getName(),
                pricing.getPrice(),
                pricing.getDisplayOrder()
        );
    }
}

package com.example.waxing.pricing.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pricing_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PricingGender gender;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10, columnDefinition = "varchar(10) default 'BODY'")
    private PricingCategory category;

    @Column(precision = 12, scale = 0)
    private BigDecimal price;

    @Column(nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private boolean active = true;

    public static Pricing create(
            PricingGender gender,
            PricingCategory category,
            String name,
            BigDecimal price,
            int displayOrder
    ) {
        Pricing pricing = new Pricing();
        pricing.gender = gender;
        pricing.category = category;
        pricing.name = name;
        pricing.price = price;
        pricing.displayOrder = displayOrder;
        return pricing;
    }

    public void changeCategory(PricingCategory category) {
        this.category = category;
    }

    public void update(PricingGender gender, PricingCategory category, String name,
                       BigDecimal price, int displayOrder) {
        this.gender = gender;
        this.category = category;
        this.name = name;
        this.price = price;
        this.displayOrder = displayOrder;
    }

    public void deactivate() {
        this.active = false;
    }

    public void changeDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}

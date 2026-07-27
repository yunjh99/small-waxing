package com.example.waxing.pricing.repository;

import com.example.waxing.pricing.domain.Pricing;
import com.example.waxing.pricing.domain.PricingGender;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PricingRepository extends JpaRepository<Pricing, Long> {
    List<Pricing> findAllByGenderAndActiveTrueOrderByDisplayOrderAscIdAsc(PricingGender gender);
}

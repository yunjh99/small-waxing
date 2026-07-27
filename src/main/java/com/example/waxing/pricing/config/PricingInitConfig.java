package com.example.waxing.pricing.config;

import com.example.waxing.pricing.domain.Pricing;
import com.example.waxing.pricing.domain.PricingCategory;
import com.example.waxing.pricing.domain.PricingGender;
import com.example.waxing.pricing.repository.PricingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class PricingInitConfig {

    private static final List<String> COMMON_MENUS = List.of(
            "브라질리언 왁싱",
            "페이스 전체 왁싱",
            "인중 왁싱",
            "팔 전체 왁싱",
            "팔 하프 왁싱",
            "다리 전체 왁싱",
            "다리 하프 왁싱"
    );

    @Bean
    public CommandLineRunner createInitialPricingMenus(PricingRepository pricingRepository) {
        return args -> {
            if (pricingRepository.count() > 0) {
                List<Pricing> facePricings = pricingRepository.findAll().stream()
                        .filter(pricing -> pricing.getName().equals("페이스 전체 왁싱")
                                || pricing.getName().equals("인중 왁싱"))
                        .peek(pricing -> pricing.changeCategory(PricingCategory.FACE))
                        .toList();
                pricingRepository.saveAll(facePricings);
                return;
            }

            List<Pricing> pricings = new ArrayList<>();
            int femaleOrder = 1;
            pricings.add(Pricing.create(PricingGender.FEMALE, PricingCategory.BODY, "브라질리언 왁싱", null, femaleOrder++));
            pricings.add(Pricing.create(PricingGender.FEMALE, PricingCategory.BODY, "임산부 왁싱", null, femaleOrder++));
            pricings.add(Pricing.create(PricingGender.FEMALE, PricingCategory.FACE, "페이스 전체 왁싱", null, femaleOrder++));
            pricings.add(Pricing.create(PricingGender.FEMALE, PricingCategory.FACE, "인중 왁싱", null, femaleOrder++));
            pricings.add(Pricing.create(PricingGender.FEMALE, PricingCategory.BODY, "비키니 왁싱", null, femaleOrder++));
            pricings.add(Pricing.create(PricingGender.FEMALE, PricingCategory.BODY, "팔 전체 왁싱", null, femaleOrder++));
            pricings.add(Pricing.create(PricingGender.FEMALE, PricingCategory.BODY, "팔 하프 왁싱", null, femaleOrder++));
            pricings.add(Pricing.create(PricingGender.FEMALE, PricingCategory.BODY, "다리 전체 왁싱", null, femaleOrder++));
            pricings.add(Pricing.create(PricingGender.FEMALE, PricingCategory.BODY, "다리 하프 왁싱", null, femaleOrder));

            int maleOrder = 1;
            for (String menu : COMMON_MENUS) {
                PricingCategory category = menu.equals("페이스 전체 왁싱") || menu.equals("인중 왁싱")
                        ? PricingCategory.FACE
                        : PricingCategory.BODY;
                pricings.add(Pricing.create(PricingGender.MALE, category, menu, null, maleOrder++));
            }

            pricingRepository.saveAll(pricings);
        };
    }
}

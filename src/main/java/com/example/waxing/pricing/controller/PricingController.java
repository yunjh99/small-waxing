package com.example.waxing.pricing.controller;

import com.example.waxing.pricing.domain.PricingGender;
import com.example.waxing.pricing.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pricing")
public class PricingController {

    private final PricingService pricingService;

    @GetMapping
    public String pricing(Model model) {
        model.addAttribute("femalePricings", pricingService.getActivePricings(PricingGender.FEMALE));
        model.addAttribute("malePricings", pricingService.getActivePricings(PricingGender.MALE));
        model.addAttribute("femaleGender", PricingGender.FEMALE);
        model.addAttribute("maleGender", PricingGender.MALE);
        return "pages/pricing/pricing";
    }
}

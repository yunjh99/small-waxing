package com.example.waxing.pricing.controller;

import com.example.waxing.pricing.domain.PricingCategory;
import com.example.waxing.pricing.domain.PricingGender;
import com.example.waxing.pricing.dto.PricingCreateRequest;
import com.example.waxing.pricing.service.PricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/pricing")
public class AdminPricingController {

    private final PricingService pricingService;

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute(
                "pricing",
                new PricingCreateRequest(null, null, null, null, 0)
        );
        model.addAttribute("genders", PricingGender.values());
        model.addAttribute("categories", PricingCategory.values());
        return "pages/pricing/form";
    }

    @GetMapping("/{id}/form")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("pricing", pricingService.getForEdit(id));
        model.addAttribute("pricingId", id);
        model.addAttribute("genders", PricingGender.values());
        model.addAttribute("categories", PricingCategory.values());
        return "pages/pricing/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("pricing") PricingCreateRequest request,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("genders", PricingGender.values());
            model.addAttribute("categories", PricingCategory.values());
            return "pages/pricing/form";
        }

        pricingService.createPricing(request);
        return "redirect:/pricing?gender=" + request.gender().name();
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("pricing") PricingCreateRequest request,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pricingId", id);
            model.addAttribute("genders", PricingGender.values());
            model.addAttribute("categories", PricingCategory.values());
            return "pages/pricing/form";
        }

        pricingService.updatePricing(id, request);
        return "redirect:/pricing?gender=" + request.gender().name();
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @RequestParam(defaultValue = "FEMALE") PricingGender gender) {
        pricingService.deletePricing(id);
        return "redirect:/pricing?gender=" + gender.name();
    }

    @PostMapping("/order")
    public String reorder(@RequestParam PricingGender gender,
                          @RequestParam PricingCategory category,
                          @RequestParam(name = "pricingIds", required = false) List<Long> pricingIds) {
        if (pricingIds != null && !pricingIds.isEmpty()) {
            pricingService.reorder(gender, category, pricingIds);
        }
        return "redirect:/pricing?gender=" + gender.name();
    }
}

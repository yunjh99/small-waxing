package com.example.waxing.faq.controller;

import com.example.waxing.faq.dto.FaqListDto;
import com.example.waxing.faq.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/community/faqs")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "all") String type,
                       Model model,
                       @PageableDefault(size = 8) Pageable pageable) {

        Page<FaqListDto> faqs = faqService.getActiveFaqs(type, pageable);

        model.addAttribute("faqs", faqs);
        model.addAttribute("selectedType", type);
        model.addAttribute("viewType", "active");

        return "pages/community/faq/list";
    }
}
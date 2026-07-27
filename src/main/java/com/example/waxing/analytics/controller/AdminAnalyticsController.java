package com.example.waxing.analytics.controller;

import com.example.waxing.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/analytics")
public class AdminAnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("analytics", analyticsService.getDashboard());
        return "pages/admin/analytics";
    }
}

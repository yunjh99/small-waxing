package com.example.waxing.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage() {
        return "pages/auth/login";
    }

}

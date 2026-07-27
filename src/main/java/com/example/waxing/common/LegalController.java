package com.example.waxing.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LegalController {

    @GetMapping("/terms")
    public String terms() {
        return "pages/legal/terms";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "pages/legal/privacy";
    }
}

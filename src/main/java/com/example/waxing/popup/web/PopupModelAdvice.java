package com.example.waxing.popup.web;

import com.example.waxing.popup.dto.PopupDto;
import com.example.waxing.popup.service.PopupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class PopupModelAdvice {

    private final PopupService popupService;

    @ModelAttribute("activePopups")
    public List<PopupDto> activePopups() {
        return popupService.getActivePopups();
    }
}

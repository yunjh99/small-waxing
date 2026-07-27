package com.example.waxing.popup.controller;

import com.example.waxing.popup.service.PopupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/popups")
public class AdminPopupController {

    private final PopupService popupService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("popups", popupService.getAll());
        return "pages/admin/popups";
    }

    @GetMapping("/form")
    public String createForm() {
        return "pages/admin/popup-form";
    }

    @GetMapping("/{id}/form")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("popup", popupService.get(id));
        return "pages/admin/popup-form";
    }

    @PostMapping
    public String create(@RequestParam String title,
                         @RequestParam LocalDate startDate,
                         @RequestParam LocalDate endDate,
                         @RequestParam MultipartFile image,
                         RedirectAttributes redirectAttributes) {
        try {
            popupService.create(title, startDate, endDate, image);
            redirectAttributes.addFlashAttribute("message", "팝업이 등록되었습니다.");
            return "redirect:/admin/popups";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/popups/form";
        }
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String title,
                         @RequestParam LocalDate startDate,
                         @RequestParam LocalDate endDate,
                         @RequestParam(required = false) MultipartFile image,
                         RedirectAttributes redirectAttributes) {
        try {
            popupService.update(id, title, startDate, endDate, image);
            redirectAttributes.addFlashAttribute("message", "팝업이 수정되었습니다.");
            return "redirect:/admin/popups";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/popups/" + id + "/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        popupService.delete(id);
        redirectAttributes.addFlashAttribute("message", "팝업이 삭제되었습니다.");
        return "redirect:/admin/popups";
    }
}

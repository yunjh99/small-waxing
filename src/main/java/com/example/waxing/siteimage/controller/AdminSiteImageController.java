package com.example.waxing.siteimage.controller;

import com.example.waxing.siteimage.domain.SiteImageSlot;
import com.example.waxing.siteimage.domain.SiteTextSlot;
import com.example.waxing.siteimage.service.SiteImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/site-images")
public class AdminSiteImageController {

    private final SiteImageService siteImageService;

    @GetMapping
    public String form(Model model) {
        model.addAttribute("images", siteImageService.getAdminImages());
        model.addAttribute("galleryImages", siteImageService.getGalleryImages());
        return "pages/admin/site-images";
    }

    @PostMapping("/gallery")
    public String addGalleryImage(@RequestParam MultipartFile image,
                                  RedirectAttributes redirectAttributes) {
        try {
            siteImageService.addGalleryImage(image);
            redirectAttributes.addFlashAttribute("message", "둘러보기 이미지가 추가되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/site-images";
    }

    @PostMapping("/gallery/{id}/delete")
    public String deleteGalleryImage(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes) {
        try {
            siteImageService.deleteGalleryImage(id);
            redirectAttributes.addFlashAttribute("message", "둘러보기 이미지가 삭제되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/site-images";
    }

    @PostMapping("/gallery/{id}/replace")
    public String replaceGalleryImage(@PathVariable Long id,
                                      @RequestParam MultipartFile image,
                                      @RequestParam(defaultValue = "") String returnTo,
                                      RedirectAttributes redirectAttributes) {
        try {
            siteImageService.replaceGalleryImage(id, image);
            redirectAttributes.addFlashAttribute("message", "둘러보기 이미지가 변경되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return redirectTo(returnTo);
    }

    @PostMapping("/{slot}")
    public String replace(@PathVariable SiteImageSlot slot,
                          @RequestParam MultipartFile image,
                          @RequestParam(defaultValue = "") String returnTo,
                          RedirectAttributes redirectAttributes) {
        try {
            siteImageService.replace(slot, image);
            redirectAttributes.addFlashAttribute("message", "이미지가 변경되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return redirectTo(returnTo);
    }

    @PostMapping("/{slot}/default")
    public String restoreDefault(@PathVariable SiteImageSlot slot,
                                 RedirectAttributes redirectAttributes) {
        siteImageService.restoreDefault(slot);
        redirectAttributes.addFlashAttribute("message", "기본 이미지로 복원했습니다.");
        return "redirect:/admin/site-images";
    }

    @PostMapping("/text/{slot}")
    public String updateText(@PathVariable SiteTextSlot slot,
                             @RequestParam String title,
                             @RequestParam String subtitle,
                             @RequestParam String description,
                             @RequestParam(defaultValue = "") String returnTo,
                             RedirectAttributes redirectAttributes) {
        try {
            siteImageService.updateText(slot, title, subtitle, description);
            redirectAttributes.addFlashAttribute("message", "문구가 변경되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return redirectTo(returnTo);
    }

    @PostMapping("/text/{slot}/default")
    public String restoreDefaultText(@PathVariable SiteTextSlot slot,
                                     RedirectAttributes redirectAttributes) {
        siteImageService.restoreDefaultText(slot);
        redirectAttributes.addFlashAttribute("message", "기본 문구로 복원했습니다.");
        return "redirect:/admin/site-images";
    }

    private String redirectTo(String returnTo) {
        if (returnTo != null && returnTo.matches("^(/|/smallwaxing|/smallwaxing/gallery|/services/(brazilian|body|face|pregnant))$")) {
            return "redirect:" + returnTo;
        }
        return "redirect:/admin/site-images";
    }
}

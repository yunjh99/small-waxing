package com.example.waxing.common;

import com.example.waxing.siteimage.domain.SiteImageSlot;
import com.example.waxing.siteimage.domain.SiteTextSlot;
import com.example.waxing.siteimage.service.SiteImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final SiteImageService siteImageService;

    @Value("${app.reservation.naver-url}")
    private String naverReservationUrl;

    @Value("${app.reservation.kakao-url}")
    private String kakaoChannelUrl;

    @GetMapping("/")
    public String main(Model model) {
        model.addAttribute("mainHeroImage", siteImageService.getUrl(SiteImageSlot.MAIN_HERO));
        model.addAttribute("mainServiceImage1", siteImageService.getUrl(SiteImageSlot.MAIN_SERVICE_1));
        model.addAttribute("mainServiceImage2", siteImageService.getUrl(SiteImageSlot.MAIN_SERVICE_2));
        model.addAttribute("mainServiceImage3", siteImageService.getUrl(SiteImageSlot.MAIN_SERVICE_3));
        model.addAttribute("mainServiceImage4", siteImageService.getUrl(SiteImageSlot.MAIN_SERVICE_4));
        model.addAttribute("mainHeroText", siteImageService.getText(SiteTextSlot.MAIN_HERO));
        model.addAttribute("mainServiceText1", siteImageService.getText(SiteTextSlot.MAIN_SERVICE_1));
        model.addAttribute("mainServiceText2", siteImageService.getText(SiteTextSlot.MAIN_SERVICE_2));
        model.addAttribute("mainServiceText3", siteImageService.getText(SiteTextSlot.MAIN_SERVICE_3));
        model.addAttribute("mainServiceText4", siteImageService.getText(SiteTextSlot.MAIN_SERVICE_4));
        return "pages/main/index";
    }

    @GetMapping("/smallwaxing")
    public String smallWaxing(Model model) {
        model.addAttribute("heroImageUrl", siteImageService.getUrl(SiteImageSlot.SPECIAL_HERO));
        model.addAttribute("specialImage1", siteImageService.getUrl(SiteImageSlot.SPECIAL_CONTENT_1));
        model.addAttribute("specialImage2", siteImageService.getUrl(SiteImageSlot.SPECIAL_CONTENT_2));
        model.addAttribute("specialImage3", siteImageService.getUrl(SiteImageSlot.SPECIAL_CONTENT_3));
        model.addAttribute("heroText", siteImageService.getText(SiteTextSlot.SPECIAL_HERO));
        model.addAttribute("specialText1", siteImageService.getText(SiteTextSlot.SPECIAL_CONTENT_1));
        model.addAttribute("specialText2", siteImageService.getText(SiteTextSlot.SPECIAL_CONTENT_2));
        model.addAttribute("specialText3", siteImageService.getText(SiteTextSlot.SPECIAL_CONTENT_3));
        return "pages/smallwaxing/special";
    }

    @GetMapping("/smallwaxing/gallery")
    public String gallery(Model model) {
        model.addAttribute("heroImageUrl", siteImageService.getUrl(SiteImageSlot.GALLERY_HERO));
        model.addAttribute("galleryImages", siteImageService.getGalleryImages());
        model.addAttribute("heroText", siteImageService.getText(SiteTextSlot.GALLERY_HERO));
        return "pages/smallwaxing/gallery";
    }

    @GetMapping("/reservation")
    public String reservation(Model model) {
        model.addAttribute("naverReservationUrl", naverReservationUrl);
        model.addAttribute("kakaoChannelUrl", kakaoChannelUrl);
        return "pages/reservation/index";
    }
}

package com.example.waxing.common;

import com.example.waxing.siteimage.domain.SiteImageSlot;
import com.example.waxing.siteimage.domain.SiteTextSlot;
import com.example.waxing.siteimage.service.SiteImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ServiceController {

    private static final Map<String, ServicePage> PAGES = Map.of(
            "brazilian", new ServicePage(
                    SiteImageSlot.MAIN_SERVICE_1,
                    SiteTextSlot.MAIN_SERVICE_1,
                    SiteTextSlot.SERVICE_CONTENT_BRAZILIAN,
                    List.of(
                            new Benefit("bi-droplet", "산뜻한 청결감", "불필요한 털과 각질을 함께 정돈해 쾌적함을 높여요."),
                            new Benefit("bi-stars", "매끄러운 피부", "면도 후 까끌거림 없이 부드러운 피부결을 느낄 수 있어요."),
                            new Benefit("bi-calendar2-check", "오래가는 깔끔함", "모근부터 정리해 매끈한 상태가 비교적 오래 유지돼요."),
                            new Benefit("bi-shield-check", "1:1 위생 관리", "일회용 도구와 철저한 소독으로 안심할 수 있어요.")
                    )
            ),
            "body", new ServicePage(
                    SiteImageSlot.MAIN_SERVICE_3,
                    SiteTextSlot.MAIN_SERVICE_3,
                    SiteTextSlot.SERVICE_CONTENT_BODY,
                    List.of(
                            new Benefit("bi-person-arms-up", "부위별 맞춤 관리", "원하는 부위와 피부 상태에 맞춰 세심하게 진행해요."),
                            new Benefit("bi-stars", "부드러운 피부결", "잔털과 묵은 각질을 정돈해 한층 매끄러워 보여요."),
                            new Benefit("bi-sun", "자신 있는 노출", "민소매와 수영복도 깔끔하고 자신 있게 즐겨요."),
                            new Benefit("bi-heart-pulse", "자극 최소화", "숙련된 테크닉으로 피부 부담을 세심하게 줄여요.")
                    )
            ),
            "face", new ServicePage(
                    SiteImageSlot.MAIN_SERVICE_2,
                    SiteTextSlot.MAIN_SERVICE_2,
                    SiteTextSlot.SERVICE_CONTENT_FACE,
                    List.of(
                            new Benefit("bi-brush", "메이크업 밀착", "잔털을 정돈해 베이스 메이크업이 얇고 고르게 표현돼요."),
                            new Benefit("bi-brightness-high", "맑아 보이는 피부", "묵은 각질과 잔털을 정리해 피부가 화사해 보여요."),
                            new Benefit("bi-bezier2", "또렷한 얼굴선", "눈썹과 헤어라인을 다듬어 인상을 깔끔하게 정돈해요."),
                            new Benefit("bi-feather", "섬세한 시술", "민감한 얼굴 피부를 고려해 부위별로 세심하게 관리해요.")
                    )
            ),
            "pregnant", new ServicePage(
                    SiteImageSlot.MAIN_SERVICE_4,
                    SiteTextSlot.MAIN_SERVICE_4,
                    SiteTextSlot.SERVICE_CONTENT_PREGNANT,
                    List.of(
                            new Benefit("bi-person-heart", "임산부 맞춤 케어", "주수와 컨디션을 확인하고 무리 없이 진행해요."),
                            new Benefit("bi-flower1", "편안한 시술 자세", "몸에 부담이 적도록 자세와 진행 속도를 조절해요."),
                            new Benefit("bi-droplet-half", "출산 전 청결 관리", "산전·산후 관리가 한결 편하도록 깔끔하게 정돈해요."),
                            new Benefit("bi-chat-heart", "세심한 상담", "처음 받는 분도 안심하도록 과정과 주의사항을 안내해요.")
                    )
            )
    );

    private final SiteImageService siteImageService;

    @GetMapping("/services/{service}")
    public String service(@PathVariable String service, Model model) {
        ServicePage page = PAGES.get(service);
        if (page == null) {
            return "redirect:/services/brazilian";
        }

        model.addAttribute("activeService", service);
        model.addAttribute("heroImageUrl", siteImageService.getUrl(page.imageSlot()));
        model.addAttribute("heroText", siteImageService.getText(page.textSlot()));
        model.addAttribute("contentText", siteImageService.getText(page.contentTextSlot()));
        model.addAttribute("serviceContent", page);
        return "pages/service/detail";
    }

    public record ServicePage(
            SiteImageSlot imageSlot,
            SiteTextSlot textSlot,
            SiteTextSlot contentTextSlot,
            List<Benefit> benefits
    ) {
    }

    public record Benefit(String icon, String title, String description) {
    }
}

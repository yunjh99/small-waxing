package com.example.waxing.event.controller;

import com.example.waxing.event.service.EventService;
import com.example.waxing.global.util.SecurityUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/community/events")
public class EventController {

    private static final String EVENT_LIST_VIEW = "pages/community/event/list";
    private static final String EVENT_DETAIL_VIEW = "pages/community/event/detail";
    private static final int PAGE_SIZE = 8;

    private final EventService eventService;

    @GetMapping
    public String list(
            @PageableDefault(size = PAGE_SIZE) Pageable pageable,
            Model model
    ) {
        addEventListAttributes(model, pageable);
        return EVENT_LIST_VIEW;
    }

    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            @PageableDefault(size = PAGE_SIZE) Pageable pageable,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {

        // 관리자가 아니고, 24시간 이내에 같은 이벤트를 조회한 기록이 없을 때만 증가한다.
        if (!SecurityUtils.isAdmin() && !hasViewCookie(request, id)) {
            eventService.incrementViews(id);
            addViewCookie(response, id);
        }

        model.addAttribute("event", eventService.getActiveEvent(id));
        model.addAttribute("currentId", id);
        addEventListAttributes(model, pageable);

        return EVENT_DETAIL_VIEW;
    }

    private void addEventListAttributes(Model model, Pageable pageable) {
        model.addAttribute("events", eventService.getActiveEvent(pageable));
        model.addAttribute("adminView", false);
    }
    /** 해당 이벤트를 이미 조회했는지 쿠키로 확인한다. */
    private boolean hasViewCookie(HttpServletRequest request, Long id) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }

        String cookieName = "view_event_" + id;
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return true;
            }
        }

        return false;
    }

    /** 같은 브라우저의 중복 조회를 24시간 동안 막는 쿠키를 추가한다. */
    private void addViewCookie(HttpServletResponse response, Long id) {
        Cookie cookie = new Cookie("view_event_" + id, "true");
        cookie.setMaxAge(60 * 60 * 24);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}

package com.example.waxing.notice.controller;

import com.example.waxing.global.util.SecurityUtils;
import com.example.waxing.notice.service.NoticeService;
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
@RequestMapping("/community/notices")
public class NoticeController {

    private static final String NOTICE_LIST_VIEW = "pages/community/notice/list";
    private static final String NOTICE_DETAIL_VIEW = "pages/community/notice/detail";
    private static final String VIEW_COOKIE_PREFIX = "view_notice_";
    private static final int VIEW_COOKIE_MAX_AGE = 60 * 60 * 24;
    private static final int PAGE_SIZE = 8;

    private final NoticeService noticeService;

    @GetMapping
    public String list(
            @PageableDefault(size = PAGE_SIZE) Pageable pageable,
            Model model
    ) {
        addNoticeListAttributes(model, pageable);
        return NOTICE_LIST_VIEW;
    }

    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            @PageableDefault(size = PAGE_SIZE) Pageable pageable,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        // 관리자가 아니고 24시간 이내에 조회한 기록이 없을 때만 조회수를 증가시킨다.
        if (!SecurityUtils.isAdmin() && !hasViewCookie(request, id)) {
            noticeService.incrementViews(id);
            addViewCookie(response, id);
        }

        model.addAttribute("notice", noticeService.getActiveNotice(id));
        model.addAttribute("currentId", id);
        addNoticeListAttributes(model, pageable);

        return NOTICE_DETAIL_VIEW;
    }

    private void addNoticeListAttributes(Model model, Pageable pageable) {
        model.addAttribute("notices", noticeService.getActiveNotices(pageable));
        model.addAttribute("adminView", false);
    }

    private boolean hasViewCookie(HttpServletRequest request, Long id) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }

        String cookieName = VIEW_COOKIE_PREFIX + id;
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return true;
            }
        }

        return false;
    }

    private void addViewCookie(HttpServletResponse response, Long id) {
        Cookie cookie = new Cookie(VIEW_COOKIE_PREFIX + id, "true");
        cookie.setMaxAge(VIEW_COOKIE_MAX_AGE);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}

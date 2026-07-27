package com.example.waxing.analytics.web;

import com.example.waxing.analytics.service.AnalyticsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URI;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AnalyticsInterceptor implements HandlerInterceptor {

    private static final String VISITOR_COOKIE = "waxing_visitor";
    private static final String VISITOR_ATTRIBUTE = "analyticsVisitorId";

    private final AnalyticsService analyticsService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod) || !isTrackable(request)) {
            return true;
        }

        String visitorId = readVisitorId(request);
        if (visitorId == null) {
            visitorId = UUID.randomUUID().toString();
            Cookie cookie = new Cookie(VISITOR_COOKIE, visitorId);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 365);
            cookie.setHttpOnly(true);
            cookie.setSecure(request.isSecure());
            response.addCookie(cookie);
        }
        request.setAttribute(VISITOR_ATTRIBUTE, visitorId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        Object visitorId = request.getAttribute(VISITOR_ATTRIBUTE);
        if (visitorId == null || ex != null || response.getStatus() >= 400) return;

        try {
            analyticsService.record(
                    visitorId.toString(),
                    request.getRequestURI(),
                    referrerSource(request.getHeader("Referer"), request.getServerName()),
                    deviceType(request.getHeader("User-Agent"))
            );
        } catch (RuntimeException ignored) {
            // 통계 저장 실패가 사용자 페이지 응답에 영향을 주지 않게 한다.
        }
    }

    private boolean isTrackable(HttpServletRequest request) {
        String path = request.getRequestURI();
        return "GET".equalsIgnoreCase(request.getMethod())
                && !path.startsWith("/admin")
                && !path.startsWith("/files")
                && !path.startsWith("/login")
                && !path.startsWith("/error");
    }

    private String readVisitorId(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> VISITOR_COOKIE.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> value.matches("[0-9a-fA-F-]{36}"))
                .findFirst()
                .orElse(null);
    }

    private String referrerSource(String referrer, String currentHost) {
        if (referrer == null || referrer.isBlank()) return "직접 방문";
        try {
            String host = URI.create(referrer).getHost();
            if (host == null) return "기타";
            host = host.toLowerCase(Locale.ROOT);
            if (currentHost != null && host.equalsIgnoreCase(currentHost)) return "사이트 내부";
            if (host.contains("naver")) return "네이버";
            if (host.contains("google")) return "구글";
            if (host.contains("daum")) return "다음";
            if (host.contains("instagram")) return "인스타그램";
            if (host.contains("kakao")) return "카카오";
            return "기타";
        } catch (IllegalArgumentException e) {
            return "기타";
        }
    }

    private String deviceType(String userAgent) {
        if (userAgent == null) return "PC";
        String value = userAgent.toLowerCase(Locale.ROOT);
        return value.matches(".*(mobile|android|iphone|ipad|ipod).*") ? "모바일" : "PC";
    }
}

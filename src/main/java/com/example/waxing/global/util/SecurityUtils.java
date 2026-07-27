package com.example.waxing.global.util;

import com.example.waxing.global.security.CustomUserDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static boolean isAdmin() {

        // 현재 로그인한 사용자 인증 정보 가져오기
        // Spring Security는 로그인 정보를 SecurityContextHolder에 저장함
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 로그인하지 않은 사용자거나 인증되지 않은 사용자이면 관리자 아님
        // AnonymousAuthenticationToken → 비로그인 사용자
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        // 인증 객체 안에 있는 실제 사용자 정보 가져오기
        // 보통 CustomUserDetails가 들어 있음
        Object principal = authentication.getPrincipal();

        // principal이 우리가 만든 CustomUserDetails가 아니면 관리자 아님
        // (예: 다른 인증 방식 등)
        if (!(principal instanceof CustomUserDetails customUserDetails)) {
            return false;
        }

        // 사용자의 권한 목록 중 ROLE_ADMIN이 있는지 확인
        // 권한 목록은 Collection<GrantedAuthority> 형태
        // ROLE_ADMIN이 하나라도 있으면 관리자
        return customUserDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    public static CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails;
        }

        return null;
    }
}

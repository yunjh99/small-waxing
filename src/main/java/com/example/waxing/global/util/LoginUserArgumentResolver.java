package com.example.waxing.global.util;

import com.example.waxing.global.error.exception.InvalidLoginUserException;
import com.example.waxing.global.security.CustomUserDetails;
import com.example.waxing.global.security.Login;
import com.example.waxing.user.domain.Role;
import com.example.waxing.user.dto.LoginUser;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Login.class)
                && parameter.getParameterType().equals(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new InvalidLoginUserException();
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails customUserDetails)) {
            throw new InvalidLoginUserException();
        }

        Role role = customUserDetails.getAuthorities().stream()
                .map(authority -> Role.ofValue(authority.getAuthority()))
                .findFirst()
                .orElseThrow(InvalidLoginUserException::new);

        return LoginUser.builder()
                .userId(customUserDetails.getUser().getId())
                .userName(customUserDetails.getUsername())
                .role(role)
                .build();
    }
}

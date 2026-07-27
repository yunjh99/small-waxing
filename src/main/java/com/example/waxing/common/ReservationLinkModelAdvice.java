package com.example.waxing.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ReservationLinkModelAdvice {

    @Value("${app.reservation.naver-url}")
    private String naverReservationUrl;

    @Value("${app.reservation.kakao-url}")
    private String kakaoChannelUrl;

    @Value("${app.reservation.instagram-url}")
    private String instagramUrl;

    @ModelAttribute("naverReservationUrl")
    public String naverReservationUrl() {
        return naverReservationUrl;
    }

    @ModelAttribute("kakaoChannelUrl")
    public String kakaoChannelUrl() {
        return kakaoChannelUrl;
    }

    @ModelAttribute("instagramUrl")
    public String instagramUrl() {
        return instagramUrl;
    }
}

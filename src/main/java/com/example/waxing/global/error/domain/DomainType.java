package com.example.waxing.global.error.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DomainType {

    NOTICE("공지사항"),
    FAQ("FAQ"),
    EVENT("이벤트");

    private final String label;

    // 🔥 추가
    public String alreadyDeletedMessage() {
        return "이미 삭제된 " + label + "입니다.";
    }

    public String notFoundException(){
        return label + " 를 찾을 수 없습니다.";
    }
}
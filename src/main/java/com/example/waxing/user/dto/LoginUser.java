package com.example.waxing.user.dto;

import com.example.waxing.user.domain.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginUser {

    private Long userId;
    private String userName;
    private Role role;

    @Builder
    public LoginUser(Long userId, String userName, Role role) {
        this.userId = userId;
        this.userName = userName;
        this.role = role;
    }
}
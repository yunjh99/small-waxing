package com.example.waxing.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; //아이디

    @Column(nullable = false)
    private String password; //비번

    @Column(nullable = false)
    private String name;     //성함

    @Column(nullable = false, unique = true)
    private String email;    //이메일

    @Column(nullable = false, unique = true)
    private String phone;    //폰번호

    @Column(nullable = false)
    private String address;  //주소

    @Enumerated(EnumType.STRING)
    private Role role;

    @PrePersist
    public void setDefaultRole() {
        if (this.role == null) {
            this.role = Role.USER; //
        }
    }

    public static User createAdmin(String username, String encodedPassword) {
        User user = new User();
        user.username = username;
        user.password = encodedPassword;
        user.name = "관리자";
        user.email = "admin@waxing.local";
        user.phone = "000-0000-0000";
        user.address = "관리자";
        user.role = Role.ADMIN;
        return user;
    }

    public void changeAdminCredentials(String username, String encodedPassword) {
        this.username = username;
        this.password = encodedPassword;
    }

}

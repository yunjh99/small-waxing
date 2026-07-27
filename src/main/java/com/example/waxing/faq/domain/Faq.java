package com.example.waxing.faq.domain;

import com.example.waxing.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Faq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faq_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY: 이벤트 조회 시 작성자 정보는 필요할 때만 조회
    @JoinColumn(name = "user_id", nullable = true)
    private User user; // 작성자

    @Column(nullable = false)
    private String title; // 질문

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 답변

    @Enumerated(EnumType.STRING)
    private FaqType type;

    @Column(nullable = false)
    private int viewCount = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private User deletedBy;

    //생성
    public static Faq create(User user, String title, String content, FaqType type) {
        Faq faq = new Faq();
        faq.user = user;
        faq.title = title;
        faq.content = content;
        faq.type = type;
        return faq;
    }

    //삭제
    public void delete(User user) {
        if (this.deletedAt != null) return; // 또는 예외
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = user;
    }

    //수정
    public void update(String title, String content, FaqType type) {
        this.title = title;
        this.content = content;
        this.type = type;
    }

    // 복구
    public void restore() {
        this.deletedAt = null;
        this.deletedBy = null;
    }
}
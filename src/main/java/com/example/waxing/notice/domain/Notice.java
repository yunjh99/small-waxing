package com.example.waxing.notice.domain;

import com.example.waxing.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {

    // 기본 정보

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private int viewCount = 0;

    // 생성 및 변경 이력

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

    // 첨부 이미지

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeImage> images = new ArrayList<>();

    // 생성 및 수정

    public static Notice create(User user, String title, String content) {
        Notice notice = new Notice();
        notice.user = user;
        notice.title = title;
        notice.content = content;
        return notice;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 삭제 및 복구

    /** 실제 데이터를 제거하지 않고 삭제 시각과 삭제한 사용자를 기록한다. */
    public void delete(User user) {
        if (this.deletedAt != null) {
            return;
        }

        this.deletedAt = LocalDateTime.now();
        this.deletedBy = user;
    }

    public void restore() {
        this.deletedAt = null;
        this.deletedBy = null;
    }

    // 이미지 관리

    public void addImage(NoticeImage image) {
        this.images.add(image);
    }

    public NoticeImage getImage() {
        return images.stream()
                .findFirst()
                .orElse(null);
    }

    public void removeImage(NoticeImage image) {
        this.images.remove(image);
    }
}

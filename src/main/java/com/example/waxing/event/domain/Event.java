package com.example.waxing.event.domain;

import com.example.waxing.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity // JPA 관리 대상 엔티티
@Getter // 엔티티 조회용 Getter 제공
@NoArgsConstructor(access = AccessLevel.PROTECTED)// JPA 프록시 생성을 위한 기본 생성자 (외부 직접 생성 제한)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id; //기본키


    @ManyToOne(fetch = FetchType.LAZY) // LAZY: 이벤트 조회 시 작성자 정보는 필요할 때만 조회
    @JoinColumn(name = "user_id", nullable = true)
    private User user; // 작성자

    @NotBlank // 사용자 입력 검증: null, 빈 문자열, 공백만 입력 모두 허용하지 않음
    @Column(nullable = false)
    private String title;  //제목

    @Column(columnDefinition = "TEXT")
    private String content; //내용

    @Column(nullable = false)
    private LocalDate startDate;  // 이벤트 시작일 (00:00부터 노출로 간주)

    @Column(nullable = false)
    private LocalDate endDate;    // 이벤트 종료일 (23:59:59까지 노출로 간주)

    @Column(nullable = false)
    private int viewCount = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일시 (수정 불가)

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt; // 수정일시

    @Column
    private LocalDateTime deletedAt; // 삭제일시

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private User deletedBy; // 삭제한 유저

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventImage> images = new ArrayList<>();


    public static Event create(User user, String title, String content,
                               LocalDate startDate, LocalDate endDate) {
        Event event = new Event();
        event.user = user;
        event.title = title;
        event.content = content;
        event.startDate = startDate;
        event.endDate = endDate;
        return event;
    }


    // 삭제
    public void delete(User user) {
        if (this.deletedAt != null) return; // 또는 예외
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = user;
    }

    // 복구
    public void restore() {
        this.deletedAt = null;
        this.deletedBy = null;
    }


    public void addImage(EventImage image) {
        this.images.add(image);
        // EventImage가 event를 갖도록 보장(혹시 나중에 create 로직이 바뀌어도 안전)
        // 단, EventImage에 setEvent가 없으니 create 패턴으로 통일하는 게 더 좋긴 함.
    }


    public EventImage getImage(EventImageType type) {
        return images.stream()
                .filter(i -> i.getType() == type)
                .findFirst()
                .orElse(null);
    }

    public void update(String title, String content, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void removeImage(EventImage image) {
        images.remove(image);
    }

}


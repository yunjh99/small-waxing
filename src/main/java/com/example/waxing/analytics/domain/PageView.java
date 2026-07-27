package com.example.waxing.analytics.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "page_view", indexes = {
        @Index(name = "idx_page_view_viewed_at", columnList = "viewed_at"),
        @Index(name = "idx_page_view_visitor", columnList = "visitor_id"),
        @Index(name = "idx_page_view_path", columnList = "path")
})
public class PageView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visitor_id", nullable = false, length = 36)
    private String visitorId;

    @Column(nullable = false, length = 300)
    private String path;

    @Column(name = "referrer_source", nullable = false, length = 30)
    private String referrerSource;

    @Column(name = "device_type", nullable = false, length = 10)
    private String deviceType;

    @CreationTimestamp
    @Column(name = "viewed_at", nullable = false, updatable = false)
    private LocalDateTime viewedAt;

    public static PageView create(String visitorId, String path, String referrerSource, String deviceType) {
        PageView pageView = new PageView();
        pageView.visitorId = visitorId;
        pageView.path = path;
        pageView.referrerSource = referrerSource;
        pageView.deviceType = deviceType;
        return pageView;
    }
}

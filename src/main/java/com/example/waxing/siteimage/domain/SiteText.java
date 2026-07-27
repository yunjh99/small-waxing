package com.example.waxing.siteimage.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "site_text", uniqueConstraints = @UniqueConstraint(columnNames = "slot"))
public class SiteText {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SiteTextSlot slot;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, length = 500)
    private String subtitle;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, length = 10)
    private String horizontalPosition;

    @Column(nullable = false, length = 10)
    private String verticalPosition;

    public static SiteText create(SiteTextSlot slot, String title, String subtitle,
                                  String description, String horizontal, String vertical) {
        SiteText text = new SiteText();
        text.slot = slot;
        text.update(title, subtitle, description, horizontal, vertical);
        return text;
    }

    public void update(String title, String subtitle, String description,
                       String horizontal, String vertical) {
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.horizontalPosition = horizontal;
        this.verticalPosition = vertical;
    }
}

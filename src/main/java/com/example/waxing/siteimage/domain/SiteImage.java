package com.example.waxing.siteimage.domain;

import com.example.waxing.file.domain.UploadFile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "site_image", uniqueConstraints = @UniqueConstraint(columnNames = "slot"))
public class SiteImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SiteImageSlot slot;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "upload_file_id", nullable = false)
    private UploadFile uploadFile;

    public static SiteImage create(SiteImageSlot slot, UploadFile uploadFile) {
        SiteImage image = new SiteImage();
        image.slot = slot;
        image.uploadFile = uploadFile;
        return image;
    }

    public UploadFile replace(UploadFile newFile) {
        UploadFile oldFile = this.uploadFile;
        this.uploadFile = newFile;
        return oldFile;
    }
}

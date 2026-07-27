package com.example.waxing.siteimage.domain;

import com.example.waxing.file.domain.UploadFile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "gallery_image")
public class GalleryImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upload_file_id")
    private UploadFile uploadFile;

    @Column(length = 500)
    private String defaultUrl;

    @Column(nullable = false)
    private int displayOrder;

    public static GalleryImage uploaded(UploadFile uploadFile, int displayOrder) {
        GalleryImage image = new GalleryImage();
        image.uploadFile = uploadFile;
        image.displayOrder = displayOrder;
        return image;
    }

    public static GalleryImage defaultImage(String defaultUrl, int displayOrder) {
        GalleryImage image = new GalleryImage();
        image.defaultUrl = defaultUrl;
        image.displayOrder = displayOrder;
        return image;
    }

    public UploadFile replace(UploadFile newFile) {
        UploadFile oldFile = this.uploadFile;
        this.uploadFile = newFile;
        return oldFile;
    }
}

package com.example.waxing.popup.domain;

import com.example.waxing.file.domain.UploadFile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Popup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "upload_file_id", nullable = false)
    private UploadFile image;

    public static Popup create(String title, LocalDate startDate, LocalDate endDate, UploadFile image) {
        Popup popup = new Popup();
        popup.update(title, startDate, endDate);
        popup.image = image;
        return popup;
    }

    public void update(String title, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public UploadFile replaceImage(UploadFile newImage) {
        UploadFile oldImage = image;
        image = newImage;
        return oldImage;
    }
}

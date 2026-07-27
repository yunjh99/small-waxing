package com.example.waxing.notice.domain;

import com.example.waxing.file.domain.UploadFile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "upload_file_id", nullable = false)
    private UploadFile uploadFile;

    /**
     * NoticeImage 생성 팩토리 메서드
     *
     * - Notice ↔ NoticeImage 양방향 연관관계를 한 번에 완결
     * - 공지사항 본문 이미지 전용
     */
    public static NoticeImage create(Notice notice, UploadFile uploadFile) {
        NoticeImage noticeImage = new NoticeImage();
        noticeImage.notice = notice;
        noticeImage.uploadFile = uploadFile;

        // 양방향 연관관계 완결
        notice.addImage(noticeImage);

        return noticeImage;
    }

    public UploadFile changeFile(UploadFile newFile) {
        UploadFile old = this.uploadFile;
        this.uploadFile = newFile;
        return old;
    }
}
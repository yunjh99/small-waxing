package com.example.waxing.notice.service;

import com.example.waxing.file.domain.UploadFile;
import com.example.waxing.file.repository.UploadFileRepository;
import com.example.waxing.file.service.FileStorageService;
import com.example.waxing.file.validation.ImageValidationException;
import com.example.waxing.file.validation.ImageValidator;
import com.example.waxing.global.error.domain.DomainType;
import com.example.waxing.global.error.exception.AlreadyDeletedException;
import com.example.waxing.global.error.exception.InvalidNoticeImageException;
import com.example.waxing.global.error.exception.NoticeNotDeletedException;
import com.example.waxing.global.error.exception.NoticeNotFoundException;
import com.example.waxing.global.error.exception.UserNotFoundException;
import com.example.waxing.notice.domain.Notice;
import com.example.waxing.notice.domain.NoticeImage;
import com.example.waxing.notice.domain.NoticeStatus;
import com.example.waxing.notice.dto.NoticeCreateRequest;
import com.example.waxing.notice.dto.NoticeDetailDto;
import com.example.waxing.notice.dto.NoticeListDto;
import com.example.waxing.notice.repository.NoticeRepository;
import com.example.waxing.user.domain.User;
import com.example.waxing.user.dto.LoginUser;
import com.example.waxing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private static final String NOTICE_FILE_DIRECTORY = "notice";

    private final NoticeRepository noticeRepository;
    private final FileStorageService fileStorageService;
    private final ImageValidator imageValidator;
    private final UploadFileRepository uploadFileRepository;
    private final UserRepository userRepository;

    // 조회

    /** 삭제 되지 않은 공지사항 목록 조회 */
    public Page<NoticeListDto> getActiveNotices(Pageable pageable) {
        return noticeRepository.findActiveNotices(pageable);
    }

    /** 삭제 되지 않은 공지사항 상세 조회 */
    public NoticeDetailDto getActiveNotice(Long id) {
        return noticeRepository.findActiveDetailById(id)
                .orElseThrow(NoticeNotFoundException::new);
    }

    /** 삭제 여부와 관계없이 공지사항 목록 조회 */
    @PreAuthorize("hasRole('ADMIN')")
    public Page<NoticeListDto> getAdminNotices(NoticeStatus status, Pageable pageable) {
        return noticeRepository.findAdminNotices(status, pageable);
    }

    /** 삭제 여부와 관계없이 공지사항 상세 조회 */
    @PreAuthorize("hasRole('ADMIN')")
    public NoticeDetailDto getAdminNotice(Long id) {
        return noticeRepository.findDetailById(id)
                .orElseThrow(NoticeNotFoundException::new);
    }

    @Transactional
    public void incrementViews(Long id) {
        noticeRepository.incrementViews(id);
    }

    // 생성 및 수정

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Long createNotice(LoginUser loginUser, NoticeCreateRequest request) {
        // 존재하지 않는 사용자라면 UserNotFoundException이 발생한다.
        User user = findUser(loginUser.getUserId());

        // 요청받은 기본 정보로 공지사항을 생성한다.
        Notice notice = Notice.create(
                user,
                request.title(),
                request.content()
        );

        // 첨부된 이미지를 연결한 후 공지사항과 함께 저장한다.
        attachImage(notice, request.bodyImage());
        return noticeRepository.save(notice).getId();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void updateNotice(
            Long id,
            NoticeCreateRequest request,
            boolean deleteBodyImage
    ) {
        // 존재하지 않는 공지사항이라면 NoticeNotFoundException이 발생한다.
        Notice notice = findNotice(id);

        // 공지사항의 기본 정보를 수정한다.
        notice.update(request.title(), request.content());

        // 새 이미지와 삭제 요청 여부에 따라 기존 이미지를 변경한다.
        applyImageChange(notice, request.bodyImage(), deleteBodyImage);
    }

    // 삭제 및 복구

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteNotice(Long id, LoginUser loginUser) {
        // 존재하지 않는 공지사항이라면 NoticeNotFoundException이 발생한다.
        Notice notice = findNotice(id);

        // 이미 삭제된 공지사항의 중복 삭제를 방지한다.
        if (notice.getDeletedAt() != null) {
            throw new AlreadyDeletedException(DomainType.NOTICE);
        }

        // 삭제를 요청한 관리자를 조회한다.
        User deletedBy = findUser(loginUser.getUserId());

        // 실제 데이터를 제거하지 않고 삭제 시각과 삭제한 관리자를 기록한다.
        notice.delete(deletedBy);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void restoreNotice(Long id) {
        // 존재하지 않는 공지사항이라면 NoticeNotFoundException이 발생한다.
        Notice notice = findNotice(id);

        // 삭제되지 않은 공지사항의 복구를 방지한다.
        if (notice.getDeletedAt() == null) {
            throw new NoticeNotDeletedException();
        }

        // 삭제 시각과 삭제한 관리자 정보를 초기화한다.
        notice.restore();
    }

    // 이미지 검증 및 저장

    private void attachImage(Notice notice, MultipartFile file) {
        if (!hasFile(file)) {
            return;
        }

        // 유효한 이미지만 저장소에 보관하고 공지사항에 연결한다.
        validateImage(file);
        NoticeImage.create(notice, storeImage(file));
    }

    private void validateImage(MultipartFile file) {
        try {
            imageValidator.validate(file);
        } catch (ImageValidationException exception) {
            throw new InvalidNoticeImageException(exception.getMessage());
        }
    }

    private UploadFile storeImage(MultipartFile file) {
        return fileStorageService.store(file, NOTICE_FILE_DIRECTORY);
    }

    private boolean hasFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    // 이미지 변경 및 삭제

    private void applyImageChange(
            Notice notice,
            MultipartFile newFile,
            boolean deleteRequested
    ) {
        // 새 파일이 있으면 삭제 요청보다 이미지 교체를 우선한다.
        if (hasFile(newFile)) {
            replaceImage(notice, newFile);
        } else if (deleteRequested) {
            deleteImage(notice);
        }
    }

    private void replaceImage(Notice notice, MultipartFile newFile) {
        validateImage(newFile);

        UploadFile newStoredFile = storeImage(newFile);
        NoticeImage existingImage = notice.getImage();

        // 기존 이미지가 없으면 새 이미지를 추가한다.
        if (existingImage == null) {
            NoticeImage.create(notice, newStoredFile);
            return;
        }

        // 기존 이미지가 있으면 연결 파일을 바꾸고 이전 파일을 제거한다.
        UploadFile oldStoredFile = existingImage.changeFile(newStoredFile);
        deleteReplacedFile(oldStoredFile);
    }

    private void deleteImage(Notice notice) {
        NoticeImage existingImage = notice.getImage();
        if (existingImage == null) {
            return;
        }

        notice.removeImage(existingImage);
        deletePhysicalFile(existingImage.getUploadFile());
    }

    private void deleteReplacedFile(UploadFile file) {
        if (file == null) {
            return;
        }

        uploadFileRepository.delete(file);
        deletePhysicalFile(file);
    }

    private void deletePhysicalFile(UploadFile file) {
        if (file != null) {
            fileStorageService.delete(file);
        }
    }

    // 공통 조회

    private Notice findNotice(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(NoticeNotFoundException::new);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }
}

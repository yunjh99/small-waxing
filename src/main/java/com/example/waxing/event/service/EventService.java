package com.example.waxing.event.service;

import com.example.waxing.event.domain.Event;
import com.example.waxing.event.domain.EventImage;
import com.example.waxing.event.domain.EventImageType;
import com.example.waxing.event.domain.EventStatus;
import com.example.waxing.event.dto.EventCreateRequest;
import com.example.waxing.event.dto.EventDetailDto;
import com.example.waxing.event.dto.EventListDto;
import com.example.waxing.event.repository.EventRepository;
import com.example.waxing.file.domain.UploadFile;
import com.example.waxing.file.repository.UploadFileRepository;
import com.example.waxing.file.service.FileStorageService;
import com.example.waxing.file.validation.ImageValidationException;
import com.example.waxing.file.validation.ImageValidator;
import com.example.waxing.global.error.domain.DomainType;
import com.example.waxing.global.error.exception.AlreadyDeletedException;
import com.example.waxing.global.error.exception.EventNotDeletedException;
import com.example.waxing.global.error.exception.EventNotFoundException;
import com.example.waxing.global.error.exception.InvalidEventImageException;
import com.example.waxing.global.error.exception.UserNotFoundException;
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
public class EventService {

    private static final String EVENT_FILE_DIRECTORY = "event";

    private final EventRepository eventRepository;
    private final FileStorageService fileStorageService;
    private final ImageValidator imageValidator;
    private final UserRepository userRepository;
    private final UploadFileRepository uploadFileRepository;

    // 조회

    /** 일반 사용자용 이벤트 목록 조회 */
    public Page<EventListDto> getActiveEvent(Pageable pageable) {
        return eventRepository.findActiveEvents(pageable);
    }

    /** 일반 사용자용 이벤트 상세 조회 */
    public EventDetailDto getActiveEvent(Long id) {
        return eventRepository.findActiveDetailById(id)
                .orElseThrow(EventNotFoundException::new);
    }

    /** 관리자용 이벤트 목록 조회 */
    @PreAuthorize("hasRole('ADMIN')")
    public Page<EventListDto> getAdminEvents(EventStatus status, Pageable pageable) {
        return eventRepository.findAdminEvents(status, pageable);
    }

    /** 관리자용 이벤트 상세 조회 */
    @PreAuthorize("hasRole('ADMIN')")
    public EventDetailDto getAdminEvent(Long id) {
        return eventRepository.findDetailById(id)
                .orElseThrow(EventNotFoundException::new);
    }

    @Transactional
    public void incrementViews(Long id) {
        eventRepository.incrementViews(id);
    }

    // 생성 및 수정

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Long createEvent(LoginUser loginUser, EventCreateRequest request) {
        // 존재하지 않는 사용자라면 UserNotFoundException이 발생한다.
        User user = findUser(loginUser.getUserId());

        // 파일을 저장하기 전에 요청에 포함된 이미지를 모두 검증한다.
        validateImages(request);

        // 요청받은 기본 정보로 이벤트를 생성한다.
        Event event = Event.create(
                user,
                request.title(),
                request.content(),
                request.startDate(),
                request.endDate()
        );

        // 첨부된 이미지만 이벤트에 연결한다.
        attachImage(event, request.thumbnail(), EventImageType.THUMBNAIL);
        attachImage(event, request.bodyImage(), EventImageType.BODY);

        // 이벤트와 연결된 이미지 정보를 함께 저장한다.
        return eventRepository.save(event).getId();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void updateEvent(
            Long id,
            EventCreateRequest request,
            MultipartFile thumbnail,
            MultipartFile bodyImage,
            boolean deleteThumbnail,
            boolean deleteBodyImage
    ) {
        // 존재하지 않는 이벤트라면 EventNotFoundException이 발생한다.
        Event event = findEvent(id);

        // 이벤트의 기본 정보를 수정한다.
        event.update(
                request.title(),
                request.content(),
                request.startDate(),
                request.endDate()
        );

        // 새 이미지와 삭제 요청 여부에 따라 기존 이미지를 변경한다.
        applyImageChange(event, EventImageType.THUMBNAIL, thumbnail, deleteThumbnail);
        applyImageChange(event, EventImageType.BODY, bodyImage, deleteBodyImage);
    }

    // 삭제 및 복구

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteEvent(Long id, LoginUser loginUser) {
        // 존재하지 않는 이벤트라면 EventNotFoundException이 발생한다.
        Event event = findEvent(id);

        // 이미 삭제된 이벤트의 중복 삭제를 방지한다.
        if (event.getDeletedAt() != null) {
            throw new AlreadyDeletedException(DomainType.EVENT);
        }

        // 삭제를 요청한 관리자를 조회한다.
        User deletedBy = findUser(loginUser.getUserId());

        // 실제 데이터를 제거하지 않고 삭제 시각과 삭제한 관리자를 기록한다.
        event.delete(deletedBy);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void restoreEvent(Long id) {
        // 존재하지 않는 이벤트라면 EventNotFoundException이 발생한다.
        Event event = findEvent(id);

        // 삭제되지 않은 이벤트의 복구를 방지한다.
        if (event.getDeletedAt() == null) {
            throw new EventNotDeletedException();
        }

        // 삭제 시각과 삭제한 관리자 정보를 초기화한다.
        event.restore();
    }

    // 이미지 검증 및 저장

    private void validateImages(EventCreateRequest request) {
        validateImageIfPresent(request.thumbnail());
        validateImageIfPresent(request.bodyImage());
    }

    private void validateImageIfPresent(MultipartFile file) {
        if (hasFile(file)) {
            validateImage(file);
        }
    }

    private void validateImage(MultipartFile file) {
        try {
            imageValidator.validate(file);
        } catch (ImageValidationException exception) {
            throw new InvalidEventImageException(exception.getMessage());
        }
    }

    private void attachImage(Event event, MultipartFile file, EventImageType type) {
        if (!hasFile(file)) {
            return;
        }

        EventImage.create(event, type, storeImage(file));
    }

    private UploadFile storeImage(MultipartFile file) {
        return fileStorageService.store(file, EVENT_FILE_DIRECTORY);
    }

    private boolean hasFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    // 이미지 변경 및 삭제

    private void applyImageChange(
            Event event,
            EventImageType type,
            MultipartFile newFile,
            boolean deleteRequested
    ) {
        if (hasFile(newFile)) {
            replaceImage(event, type, newFile);
        } else if (deleteRequested) {
            deleteImage(event, type);
        }
    }

    private void replaceImage(Event event, EventImageType type, MultipartFile newFile) {
        validateImage(newFile);

        UploadFile newStoredFile = storeImage(newFile);
        EventImage existingImage = event.getImage(type);

        if (existingImage == null) {
            EventImage.create(event, type, newStoredFile);
            return;
        }

        UploadFile oldStoredFile = existingImage.changeFile(newStoredFile);
        deleteReplacedFile(oldStoredFile);
    }

    private void deleteImage(Event event, EventImageType type) {
        EventImage existingImage = event.getImage(type);
        if (existingImage == null) {
            return;
        }

        event.removeImage(existingImage);
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

    private Event findEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(EventNotFoundException::new);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}

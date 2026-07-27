package com.example.waxing.popup.service;

import com.example.waxing.file.domain.UploadFile;
import com.example.waxing.file.repository.UploadFileRepository;
import com.example.waxing.file.service.FileStorageService;
import com.example.waxing.file.validation.ImageValidator;
import com.example.waxing.popup.domain.Popup;
import com.example.waxing.popup.dto.PopupDto;
import com.example.waxing.popup.repository.PopupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopupService {

    private final PopupRepository popupRepository;
    private final UploadFileRepository uploadFileRepository;
    private final FileStorageService fileStorageService;
    private final ImageValidator imageValidator;

    public List<PopupDto> getActivePopups() {
        LocalDate today = LocalDate.now();
        return popupRepository
                .findAllByStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByIdDesc(today, today)
                .stream().map(PopupDto::from).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<PopupDto> getAll() {
        return popupRepository.findAll().stream()
                .sorted(Comparator.comparing(Popup::getId).reversed())
                .map(PopupDto::from)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PopupDto get(Long id) {
        return PopupDto.from(getPopup(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void create(String title, LocalDate startDate, LocalDate endDate, MultipartFile image) {
        validate(title, startDate, endDate);
        requireImage(image);
        UploadFile stored = store(image);
        popupRepository.save(Popup.create(title.strip(), startDate, endDate, stored));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void update(Long id, String title, LocalDate startDate, LocalDate endDate, MultipartFile image) {
        validate(title, startDate, endDate);
        Popup popup = getPopup(id);
        popup.update(title.strip(), startDate, endDate);

        if (image != null && !image.isEmpty()) {
            UploadFile newImage = store(image);
            UploadFile oldImage = popup.replaceImage(newImage);
            popupRepository.saveAndFlush(popup);
            uploadFileRepository.delete(oldImage);
            fileStorageService.delete(oldImage);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void delete(Long id) {
        Popup popup = getPopup(id);
        UploadFile image = popup.getImage();
        popupRepository.delete(popup);
        popupRepository.flush();
        uploadFileRepository.delete(image);
        fileStorageService.delete(image);
    }

    private UploadFile store(MultipartFile image) {
        imageValidator.validate(image);
        UploadFile stored = fileStorageService.store(image, "popup");
        return uploadFileRepository.save(stored);
    }

    private Popup getPopup(Long id) {
        return popupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("팝업을 찾을 수 없습니다."));
    }

    private void validate(String title, LocalDate startDate, LocalDate endDate) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("관리용 제목을 입력해 주세요.");
        if (startDate == null || endDate == null) throw new IllegalArgumentException("노출 기간을 선택해 주세요.");
        if (endDate.isBefore(startDate)) throw new IllegalArgumentException("종료일은 시작일보다 빠를 수 없습니다.");
    }

    private void requireImage(MultipartFile image) {
        if (image == null || image.isEmpty()) throw new IllegalArgumentException("팝업 이미지를 선택해 주세요.");
    }
}

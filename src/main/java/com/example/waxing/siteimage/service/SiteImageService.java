package com.example.waxing.siteimage.service;

import com.example.waxing.file.domain.UploadFile;
import com.example.waxing.file.repository.UploadFileRepository;
import com.example.waxing.file.service.FileStorageService;
import com.example.waxing.file.validation.ImageValidator;
import com.example.waxing.siteimage.domain.*;
import com.example.waxing.siteimage.dto.GalleryImageDto;
import com.example.waxing.siteimage.dto.SiteImageAdminDto;
import com.example.waxing.siteimage.dto.SiteTextDto;
import com.example.waxing.siteimage.repository.GalleryImageRepository;
import com.example.waxing.siteimage.repository.SiteImageRepository;
import com.example.waxing.siteimage.repository.SiteTextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SiteImageService {

    private final SiteImageRepository siteImageRepository;
    private final UploadFileRepository uploadFileRepository;
    private final FileStorageService fileStorageService;
    private final ImageValidator imageValidator;
    private final SiteTextRepository siteTextRepository;
    private final GalleryImageRepository galleryImageRepository;

    public String getUrl(SiteImageSlot slot) {
        return siteImageRepository.findBySlot(slot)
                .map(siteImage -> "/files/" + siteImage.getUploadFile().getId())
                .orElse(slot.getDefaultUrl());
    }

    public Map<SiteImageSlot, String> getUrls(SiteImageSlot... slots) {
        Map<SiteImageSlot, String> urls = new EnumMap<>(SiteImageSlot.class);
        Arrays.stream(slots).forEach(slot -> urls.put(slot, getUrl(slot)));
        return urls;
    }

    public SiteTextDto getText(SiteTextSlot slot) {
        return siteTextRepository.findBySlot(slot)
                .map(text -> toTextDto(slot, text))
                .orElseGet(() -> defaultText(slot));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<SiteImageAdminDto> getAdminImages() {
        Map<SiteImageSlot, SiteImage> saved = siteImageRepository.findAllByOrderBySlotAsc()
                .stream()
                .collect(Collectors.toMap(SiteImage::getSlot, Function.identity()));

        return Arrays.stream(SiteImageSlot.values())
                .filter(slot -> slot != SiteImageSlot.LOCATION_HERO)
                .filter(slot -> !isGallerySlide(slot))
                .map(slot -> {
                    SiteImage image = saved.get(slot);
                    SiteTextSlot textSlot = SiteTextSlot.fromImageSlot(slot);
                    return new SiteImageAdminDto(
                            slot,
                            slot.getLabel(),
                            image == null ? slot.getDefaultUrl() : "/files/" + image.getUploadFile().getId(),
                            image == null ? "기본 이미지" : image.getUploadFile().getOriginalName(),
                            slot.getRecommendedSize(),
                            slot.getMenuKey(),
                            textSlot == null ? null : getText(textSlot),
                            image != null
                    );
                })
                .toList();
    }

    @Transactional
    public List<GalleryImageDto> getGalleryImages() {
        initializeGalleryImages();
        return galleryImageRepository.findAllByOrderByDisplayOrderAscIdAsc()
                .stream()
                .map(this::toGalleryImageDto)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void addGalleryImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("추가할 이미지를 선택해 주세요.");
        }

        initializeGalleryImages();
        imageValidator.validate(file);
        UploadFile uploadFile = fileStorageService.store(file, "site-image/gallery");
        uploadFileRepository.save(uploadFile);

        GalleryImage lastImage = galleryImageRepository.findTopByOrderByDisplayOrderDesc();
        int nextOrder = lastImage == null ? 1 : lastImage.getDisplayOrder() + 1;
        galleryImageRepository.save(GalleryImage.uploaded(uploadFile, nextOrder));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteGalleryImage(Long id) {
        initializeGalleryImages();
        if (galleryImageRepository.count() <= 1) {
            throw new IllegalStateException("둘러보기 이미지는 최소 1장이 필요합니다.");
        }

        GalleryImage image = galleryImageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 이미지를 찾을 수 없습니다."));
        UploadFile uploadFile = image.getUploadFile();
        galleryImageRepository.delete(image);
        galleryImageRepository.flush();

        if (uploadFile != null) {
            uploadFileRepository.delete(uploadFile);
            fileStorageService.delete(uploadFile);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void replaceGalleryImage(Long id, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("교체할 이미지를 선택해 주세요.");
        }

        initializeGalleryImages();
        imageValidator.validate(file);
        GalleryImage image = galleryImageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("교체할 이미지를 찾을 수 없습니다."));
        UploadFile newFile = fileStorageService.store(file, "site-image/gallery");
        uploadFileRepository.save(newFile);

        UploadFile oldFile = image.replace(newFile);
        galleryImageRepository.saveAndFlush(image);
        if (oldFile != null) {
            uploadFileRepository.delete(oldFile);
            fileStorageService.delete(oldFile);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void replace(SiteImageSlot slot, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("교체할 이미지를 선택해 주세요.");
        }

        imageValidator.validate(file);
        UploadFile newFile = fileStorageService.store(file, "site-image");
        uploadFileRepository.save(newFile);

        SiteImage image = siteImageRepository.findBySlot(slot).orElse(null);
        if (image == null) {
            siteImageRepository.save(SiteImage.create(slot, newFile));
            return;
        }

        UploadFile oldFile = image.replace(newFile);
        siteImageRepository.saveAndFlush(image);
        uploadFileRepository.delete(oldFile);
        fileStorageService.delete(oldFile);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void restoreDefault(SiteImageSlot slot) {
        siteImageRepository.findBySlot(slot).ifPresent(image -> {
            UploadFile oldFile = image.getUploadFile();
            siteImageRepository.delete(image);
            siteImageRepository.flush();
            uploadFileRepository.delete(oldFile);
            fileStorageService.delete(oldFile);
        });
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void updateText(SiteTextSlot slot, String title, String subtitle, String description) {
        SiteText text = siteTextRepository.findBySlot(slot)
                .orElseGet(() -> SiteText.create(slot, safe(title), safe(subtitle), safe(description),
                        slot.getDefaultHorizontal(), slot.getDefaultVertical()));
        text.update(safe(title), safe(subtitle), safe(description),
                slot.getDefaultHorizontal(), slot.getDefaultVertical());
        siteTextRepository.save(text);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void restoreDefaultText(SiteTextSlot slot) {
        siteTextRepository.findBySlot(slot).ifPresent(siteTextRepository::delete);
    }

    private SiteTextDto toTextDto(SiteTextSlot slot, SiteText text) {
        return new SiteTextDto(slot, text.getTitle(), text.getSubtitle(), text.getDescription(),
                slot.getDefaultHorizontal(), slot.getDefaultVertical(), true);
    }

    private SiteTextDto defaultText(SiteTextSlot slot) {
        return new SiteTextDto(slot, slot.getDefaultTitle(), slot.getDefaultSubtitle(),
                slot.getDefaultDescription(), slot.getDefaultHorizontal(), slot.getDefaultVertical(), false);
    }

    private void initializeGalleryImages() {
        if (galleryImageRepository.count() > 0) {
            return;
        }

        List<SiteImageSlot> slots = List.of(
                SiteImageSlot.GALLERY_SLIDE_1,
                SiteImageSlot.GALLERY_SLIDE_2,
                SiteImageSlot.GALLERY_SLIDE_3,
                SiteImageSlot.GALLERY_SLIDE_4
        );

        for (int index = 0; index < slots.size(); index++) {
            SiteImageSlot slot = slots.get(index);
            SiteImage savedImage = siteImageRepository.findBySlot(slot).orElse(null);
            if (savedImage == null) {
                galleryImageRepository.save(GalleryImage.defaultImage(slot.getDefaultUrl(), index + 1));
            } else {
                galleryImageRepository.save(GalleryImage.uploaded(savedImage.getUploadFile(), index + 1));
                siteImageRepository.delete(savedImage);
            }
        }
    }

    private GalleryImageDto toGalleryImageDto(GalleryImage image) {
        UploadFile uploadFile = image.getUploadFile();
        return new GalleryImageDto(
                image.getId(),
                uploadFile == null ? image.getDefaultUrl() : "/files/" + uploadFile.getId(),
                uploadFile == null ? "기본 이미지" : uploadFile.getOriginalName(),
                uploadFile != null
        );
    }

    private boolean isGallerySlide(SiteImageSlot slot) {
        return slot == SiteImageSlot.GALLERY_SLIDE_1
                || slot == SiteImageSlot.GALLERY_SLIDE_2
                || slot == SiteImageSlot.GALLERY_SLIDE_3
                || slot == SiteImageSlot.GALLERY_SLIDE_4;
    }

    private String safe(String value) {
        return value == null ? "" : value.strip();
    }
}

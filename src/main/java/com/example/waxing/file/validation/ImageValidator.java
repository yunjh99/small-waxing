package com.example.waxing.file.validation;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Set;

/** 애플리케이션에서 업로드하는 이미지의 공통 형식과 크기를 검증한다. */
@Component
public class ImageValidator {

    private static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024;
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "webp"
    );

    public void validate(MultipartFile file) {
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new ImageValidationException("이미지 파일은 10MB 이하만 업로드할 수 있습니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null
                || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new ImageValidationException("JPEG, PNG, GIF, WebP 이미지만 업로드할 수 있습니다.");
        }

        String extension = getExtension(file.getOriginalFilename());
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            throw new ImageValidationException("허용되지 않는 이미지 확장자입니다.");
        }

        try (InputStream input = file.getInputStream()) {
            byte[] header = input.readNBytes(12);
            if (!hasValidImageSignature(header, contentType.toLowerCase(Locale.ROOT))) {
                throw new ImageValidationException("올바른 이미지 파일이 아닙니다.");
            }
        } catch (IOException e) {
            throw new ImageValidationException("이미지 파일을 확인할 수 없습니다.");
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) return "";
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private boolean hasValidImageSignature(byte[] header, String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> header.length >= 3
                    && unsigned(header[0]) == 0xFF
                    && unsigned(header[1]) == 0xD8
                    && unsigned(header[2]) == 0xFF;
            case "image/png" -> header.length >= 8
                    && unsigned(header[0]) == 0x89
                    && header[1] == 'P' && header[2] == 'N' && header[3] == 'G'
                    && unsigned(header[4]) == 0x0D && unsigned(header[5]) == 0x0A
                    && unsigned(header[6]) == 0x1A && unsigned(header[7]) == 0x0A;
            case "image/gif" -> header.length >= 6
                    && header[0] == 'G' && header[1] == 'I' && header[2] == 'F'
                    && header[3] == '8' && (header[4] == '7' || header[4] == '9')
                    && header[5] == 'a';
            case "image/webp" -> header.length >= 12
                    && header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
                    && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P';
            default -> false;
        };
    }

    private int unsigned(byte value) {
        return value & 0xFF;
    }
}

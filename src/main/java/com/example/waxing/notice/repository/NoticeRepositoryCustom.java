package com.example.waxing.notice.repository;

import com.example.waxing.notice.domain.NoticeStatus;
import com.example.waxing.notice.dto.NoticeDetailDto;
import com.example.waxing.notice.dto.NoticeListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface NoticeRepositoryCustom {

    // 일반 사용자용 목록 조회 - 삭제 되지 않은 공지사항 반환
    Page<NoticeListDto> findActiveNotices(Pageable pageable);
    // 관리자용 목록 조회 - 조건에 맞는 공지사항 반환
    Page<NoticeListDto> findAdminNotices(NoticeStatus status, Pageable pageable);

    // 일반 사용자용 상세 조회 - 삭제 되지 않은 공지사항 반환
    Optional<NoticeDetailDto> findActiveDetailById(Long id);
    // 관리자용 상세 조회 - 모든 공지사항 반환
    Optional<NoticeDetailDto> findDetailById(Long id);

}

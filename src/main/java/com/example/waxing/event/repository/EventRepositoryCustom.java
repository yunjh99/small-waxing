package com.example.waxing.event.repository;

import com.example.waxing.event.domain.EventStatus;
import com.example.waxing.event.dto.EventDetailDto;
import com.example.waxing.event.dto.EventListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EventRepositoryCustom {

    // 일반 사용자용 목록 조회 - 현재 진행 중인 이벤트만 반환
    Page<EventListDto> findActiveEvents(Pageable pageable);
    // 관리자용 목록 조회 - 조건에 맞는 이벤트만 반환
    Page<EventListDto> findAdminEvents(EventStatus status, Pageable pageable);

    // 일반 사용자용 상세 조회 - 현재 진행 중인 이벤트만 반환
    Optional<EventDetailDto> findActiveDetailById(Long id);
    // 관리자용 상세 조회 - 모든 이벤트 반환
    Optional<EventDetailDto> findDetailById(Long id);

}

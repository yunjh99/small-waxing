package com.example.waxing.analytics.service;

import com.example.waxing.analytics.domain.PageView;
import com.example.waxing.analytics.dto.AnalyticsDashboardDto;
import com.example.waxing.analytics.dto.TrafficCountDto;
import com.example.waxing.analytics.repository.PageViewRepository;
import com.example.waxing.analytics.repository.TrafficCountProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private static final Map<String, String> PAGE_NAMES = Map.ofEntries(
            Map.entry("/", "메인 페이지"),
            Map.entry("/smallwaxing", "예제왁싱 특별함"),
            Map.entry("/smallwaxing/gallery", "둘러보기"),
            Map.entry("/services/brazilian", "브라질리언왁싱"),
            Map.entry("/services/body", "바디왁싱"),
            Map.entry("/services/face", "페이스왁싱"),
            Map.entry("/services/pregnant", "임산부왁싱"),
            Map.entry("/pricing", "시술 가격"),
            Map.entry("/community/events", "이벤트"),
            Map.entry("/community/notices", "공지사항"),
            Map.entry("/community/faqs", "자주 묻는 질문")
    );

    private final PageViewRepository pageViewRepository;

    @Transactional
    public void record(String visitorId, String path, String referrer, String deviceType) {
        pageViewRepository.save(PageView.create(visitorId, path, referrer, deviceType));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AnalyticsDashboardDto getDashboard() {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();

        return new AnalyticsDashboardDto(
                pageViewRepository.countAllVisitors(),
                pageViewRepository.countVisitorsSince(monthStart),
                pageViewRepository.countVisitorsSince(todayStart),
                pageViewRepository.countViewsSince(todayStart),
                pageViewRepository.findPopularPosts(monthStart, PageRequest.of(0, 10)),
                pageViewRepository.findPopularPages(monthStart, PageRequest.of(0, 10))
                        .stream()
                        .map(this::toNamedPage)
                        .toList(),
                pageViewRepository.findReferrerStats(monthStart),
                pageViewRepository.findDeviceStats(monthStart)
        );
    }

    private TrafficCountDto toNamedPage(TrafficCountProjection page) {
        return new TrafficCountDto(
                PAGE_NAMES.getOrDefault(page.getLabel(), page.getLabel()),
                page.getCount()
        );
    }
}

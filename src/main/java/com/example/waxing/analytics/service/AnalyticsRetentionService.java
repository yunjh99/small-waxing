package com.example.waxing.analytics.service;

import com.example.waxing.analytics.repository.PageViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnalyticsRetentionService {

    private final PageViewRepository pageViewRepository;

    @Value("${app.analytics.retention-days:365}")
    private long retentionDays;

    @Scheduled(cron = "${app.analytics.cleanup-cron:0 20 3 * * *}")
    @Transactional
    public void deleteExpiredPageViews() {
        pageViewRepository.deleteOlderThan(LocalDateTime.now().minusDays(retentionDays));
    }
}

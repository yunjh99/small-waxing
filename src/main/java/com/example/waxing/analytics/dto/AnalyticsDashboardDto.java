package com.example.waxing.analytics.dto;

import com.example.waxing.analytics.repository.TrafficCountProjection;

import java.util.List;

public record AnalyticsDashboardDto(
        long cumulativeVisitors,
        long monthlyVisitors,
        long todayVisitors,
        long todayPageViews,
        List<TrafficCountProjection> popularPosts,
        List<TrafficCountDto> popularPages,
        List<TrafficCountProjection> referrers,
        List<TrafficCountProjection> devices
) {
}

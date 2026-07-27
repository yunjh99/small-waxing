package com.example.waxing.analytics.repository;

import com.example.waxing.analytics.domain.PageView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PageViewRepository extends JpaRepository<PageView, Long> {

    @Modifying(clearAutomatically = true)
    @Query("delete from PageView p where p.viewedAt < :cutoff")
    int deleteOlderThan(@Param("cutoff") LocalDateTime cutoff);

    @Query("select count(distinct p.visitorId) from PageView p")
    long countAllVisitors();

    @Query("select count(distinct p.visitorId) from PageView p where p.viewedAt >= :from")
    long countVisitorsSince(@Param("from") LocalDateTime from);

    @Query("select count(p) from PageView p where p.viewedAt >= :from")
    long countViewsSince(@Param("from") LocalDateTime from);

    @Query("""
            select p.path as label, count(p) as count
            from PageView p
            where p.viewedAt >= :from
            group by p.path
            order by count(p) desc
            """)
    List<TrafficCountProjection> findPopularPages(@Param("from") LocalDateTime from, Pageable pageable);

    @Query("""
            select p.path as label, count(p) as count
            from PageView p
            where p.viewedAt >= :from
              and (p.path like '/community/events/%' or p.path like '/community/notices/%')
            group by p.path
            order by count(p) desc
            """)
    List<TrafficCountProjection> findPopularPosts(@Param("from") LocalDateTime from, Pageable pageable);

    @Query("""
            select p.referrerSource as label, count(p) as count
            from PageView p
            where p.viewedAt >= :from
              and p.referrerSource <> '사이트 내부'
            group by p.referrerSource
            order by count(p) desc
            """)
    List<TrafficCountProjection> findReferrerStats(@Param("from") LocalDateTime from);

    @Query("""
            select p.deviceType as label, count(p) as count
            from PageView p
            where p.viewedAt >= :from
            group by p.deviceType
            order by count(p) desc
            """)
    List<TrafficCountProjection> findDeviceStats(@Param("from") LocalDateTime from);
}

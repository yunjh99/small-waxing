package com.example.waxing.notice.repository;

import com.example.waxing.notice.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeRepositoryCustom {
    @Modifying
    @Query("update Notice n set n.viewCount = n.viewCount + 1 where n.id = :id and n.deletedAt is null")
    int incrementViews(@Param("id") Long id);
}


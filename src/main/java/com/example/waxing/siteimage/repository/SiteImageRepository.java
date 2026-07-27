package com.example.waxing.siteimage.repository;

import com.example.waxing.siteimage.domain.SiteImage;
import com.example.waxing.siteimage.domain.SiteImageSlot;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SiteImageRepository extends JpaRepository<SiteImage, Long> {

    @EntityGraph(attributePaths = "uploadFile")
    Optional<SiteImage> findBySlot(SiteImageSlot slot);

    @EntityGraph(attributePaths = "uploadFile")
    List<SiteImage> findAllByOrderBySlotAsc();
}

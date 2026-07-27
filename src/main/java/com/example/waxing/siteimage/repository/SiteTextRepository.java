package com.example.waxing.siteimage.repository;

import com.example.waxing.siteimage.domain.SiteText;
import com.example.waxing.siteimage.domain.SiteTextSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SiteTextRepository extends JpaRepository<SiteText, Long> {
    Optional<SiteText> findBySlot(SiteTextSlot slot);
}

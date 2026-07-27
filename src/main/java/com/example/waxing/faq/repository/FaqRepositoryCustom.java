package com.example.waxing.faq.repository;

import com.example.waxing.faq.domain.FaqType;
import com.example.waxing.faq.dto.FaqListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface FaqRepositoryCustom {

    Page<FaqListDto> findActiveFaqs(Pageable pageable);
    Page<FaqListDto> findActiveFaqsByType(FaqType type, Pageable pageable);
    Page<FaqListDto> findDeletedFaqs(Pageable pageable);

    Optional<FaqListDto> findFaqById(Long id);
}
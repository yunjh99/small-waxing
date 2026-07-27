package com.example.waxing.faq.repository;

import com.example.waxing.faq.domain.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository  extends JpaRepository<Faq, Long>, FaqRepositoryCustom {
}

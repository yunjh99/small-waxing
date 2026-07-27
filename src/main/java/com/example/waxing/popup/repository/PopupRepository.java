package com.example.waxing.popup.repository;

import com.example.waxing.popup.domain.Popup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PopupRepository extends JpaRepository<Popup, Long> {

    @EntityGraph(attributePaths = "image")
    List<Popup> findAllByStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByIdDesc(
            LocalDate startDate, LocalDate endDate);

    @Override
    @EntityGraph(attributePaths = "image")
    List<Popup> findAll();

    @Override
    @EntityGraph(attributePaths = "image")
    Optional<Popup> findById(Long id);
}

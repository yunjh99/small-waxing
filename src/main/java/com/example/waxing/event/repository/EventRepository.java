package com.example.waxing.event.repository;

import com.example.waxing.event.domain.Event;
import com.example.waxing.event.dto.EventListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {

    @Modifying
    @Query("""
    update Event e
    set e.viewCount = e.viewCount + 1
    where e.id = :id
""")
    int incrementViews(@Param("id") Long id);

}

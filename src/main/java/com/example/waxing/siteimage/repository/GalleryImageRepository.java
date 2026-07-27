package com.example.waxing.siteimage.repository;

import com.example.waxing.siteimage.domain.GalleryImage;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GalleryImageRepository extends JpaRepository<GalleryImage, Long> {

    @EntityGraph(attributePaths = "uploadFile")
    List<GalleryImage> findAllByOrderByDisplayOrderAscIdAsc();

    GalleryImage findTopByOrderByDisplayOrderDesc();
}

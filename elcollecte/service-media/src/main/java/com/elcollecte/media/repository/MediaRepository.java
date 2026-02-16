package com.elcollecte.media.repository;

import com.elcollecte.media.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findByCollecteId(Long collecteId);
    List<Media> findByUploadedBy(Long uploadedBy);
    long countByCollecteId(Long collecteId);
}
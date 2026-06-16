package com.elcollecte.media.repository;

import com.elcollecte.media.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    Optional<Media> findByUuid(UUID uuid);

    List<Media> findByCollecteId(Long collecteId);

    List<Media> findByProjetId(Long projetId);

    List<Media> findByUploadePar(Long uploadePar);
}

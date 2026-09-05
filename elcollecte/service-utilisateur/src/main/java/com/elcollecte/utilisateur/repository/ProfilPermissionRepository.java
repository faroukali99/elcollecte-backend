package com.elcollecte.utilisateur.repository;

import com.elcollecte.utilisateur.entity.ProfilPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfilPermissionRepository extends JpaRepository<ProfilPermission, Long> {

    List<ProfilPermission> findAllByProfilId(Long profilId);

    Optional<ProfilPermission> findByProfilIdAndPermissionId(Long profilId, Long permissionId);

    void deleteByProfilIdAndPermissionId(Long profilId, Long permissionId);

    boolean existsByProfilIdAndPermissionId(Long profilId, Long permissionId);
}

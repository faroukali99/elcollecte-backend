package com.elcollecte.utilisateur.repository;

import com.elcollecte.utilisateur.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndActiveTrue(String email);

    boolean existsByEmail(String email);

    Page<User> findAllByOrganisationId(Long organisationId, Pageable pageable);

    Page<User> findAllByOrganisationIdAndRole(Long organisationId, User.Role role, Pageable pageable);

    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :now WHERE u.id = :id")
    void updateLastLogin(@Param("id") Long id, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE User u SET u.refreshToken = :token WHERE u.id = :id")
    void updateRefreshToken(@Param("id") Long id, @Param("token") String token);

    @Modifying
    @Query("UPDATE User u SET u.active = :active WHERE u.id = :id")
    void updateActiveStatus(@Param("id") Long id, @Param("active") boolean active);
}

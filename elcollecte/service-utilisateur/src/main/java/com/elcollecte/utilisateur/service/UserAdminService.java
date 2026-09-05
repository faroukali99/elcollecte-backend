package com.elcollecte.utilisateur.service;

import com.elcollecte.common.exception.BusinessException;
import com.elcollecte.utilisateur.dto.CreateUserRequest;
import com.elcollecte.utilisateur.dto.UserAdminDto;
import com.elcollecte.utilisateur.entity.Organisation;
import com.elcollecte.utilisateur.entity.Profil;
import com.elcollecte.utilisateur.entity.User;
import com.elcollecte.utilisateur.repository.OrganisationRepository;
import com.elcollecte.utilisateur.repository.ProfilRepository;
import com.elcollecte.utilisateur.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * Gestion administrative des utilisateurs (CRUD complet).
 *
 * Ce service comble une lacune existante : jusqu'ici, seul AuthService
 * gérait le cycle de vie utilisateur, et uniquement via l'auto-inscription
 * (register). Il n'existait aucun moyen pour un ADMIN de créer/gérer des
 * comptes pour d'autres utilisateurs.
 *
 * Chaque utilisateur créé ici est directement rattaché à un Profil
 * dynamique. Le champ `role` legacy est déduit du code du profil quand
 * c'est possible (compatibilité avec ProjetService/CollecteService), sinon
 * il retombe sur ENQUETEUR par défaut.
 */
@Service
public class UserAdminService {

    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;
    private final ProfilRepository profilRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAdminService(UserRepository userRepository,
                            OrganisationRepository organisationRepository,
                            ProfilRepository profilRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.organisationRepository = organisationRepository;
        this.profilRepository = profilRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<UserAdminDto> findAll(Long organisationId, Pageable pageable) {
        return userRepository.findAllByOrganisationId(organisationId, pageable)
            .map(UserAdminDto::from);
    }

    @Transactional(readOnly = true)
    public UserAdminDto findById(Long id, Long organisationId) {
        return userRepository.findByIdAndOrganisationId(id, organisationId)
            .map(UserAdminDto::from)
            .orElseThrow(() -> new NoSuchElementException("Utilisateur introuvable: " + id));
    }

    @Transactional
    public UserAdminDto create(CreateUserRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw BusinessException.conflict("Email déjà utilisé: " + req.email());
        }

        Organisation organisation = organisationRepository.findById(req.organisationId())
            .orElseThrow(() -> new NoSuchElementException(
                "Organisation introuvable: " + req.organisationId()));

        Profil profil = profilRepository.findById(req.profilId())
            .orElseThrow(() -> new NoSuchElementException(
                "Profil introuvable: " + req.profilId()));

        if (!profil.isActive()) {
            throw BusinessException.badRequest("Impossible d'affecter un profil désactivé");
        }

        // Compat : on dérive le rôle legacy depuis le code du profil quand
        // il correspond à un des 4 rôles historiques ; sinon ENQUETEUR par
        // défaut, le comportement métier réel restant piloté par les
        // permissions du profil.
        User.Role legacyRole = resolveLegacyRole(profil.getCode());

        User user = new User(req.nom(), req.prenom(), req.email(),
            passwordEncoder.encode(req.password()), legacyRole, organisation);
        user.setProfil(profil);

        return UserAdminDto.from(userRepository.save(user));
    }

    @Transactional
    public UserAdminDto assignProfil(Long userId, Long organisationId, Long profilId) {
        User user = userRepository.findByIdAndOrganisationId(userId, organisationId)
            .orElseThrow(() -> new NoSuchElementException("Utilisateur introuvable: " + userId));

        Profil profil = profilRepository.findById(profilId)
            .orElseThrow(() -> new NoSuchElementException("Profil introuvable: " + profilId));

        if (!profil.isActive()) {
            throw BusinessException.badRequest("Impossible d'affecter un profil désactivé");
        }

        user.setProfil(profil);
        // On garde le rôle legacy synchronisé pour ne pas casser ProjetService/CollecteService
        user.setRole(resolveLegacyRole(profil.getCode()));

        return UserAdminDto.from(userRepository.save(user));
    }

    @Transactional
    public void desactiver(Long userId, Long organisationId) {
        User user = userRepository.findByIdAndOrganisationId(userId, organisationId)
            .orElseThrow(() -> new NoSuchElementException("Utilisateur introuvable: " + userId));
        userRepository.updateActiveStatus(user.getId(), false);
    }

    @Transactional
    public void reactiver(Long userId, Long organisationId) {
        User user = userRepository.findByIdAndOrganisationId(userId, organisationId)
            .orElseThrow(() -> new NoSuchElementException("Utilisateur introuvable: " + userId));
        userRepository.updateActiveStatus(user.getId(), true);
    }

    /**
     * Déduit un rôle legacy à partir du code de profil, pour compatibilité
     * avec le code existant (ProjetService.findAll, CollecteService.findAll)
     * qui compare encore la String de rôle brute. Si le profil ne
     * correspond à aucun des 4 rôles historiques (nouveau profil créé par
     * l'ADMIN), on retombe sur ENQUETEUR — le comportement réel restant de
     * toute façon gouverné par les permissions du profil sur les nouveaux
     * endpoints.
     */
    private User.Role resolveLegacyRole(String profilCode) {
        try {
            return User.Role.valueOf(profilCode.toUpperCase());
        } catch (IllegalArgumentException e) {
            return User.Role.ENQUETEUR;
        }
    }
}

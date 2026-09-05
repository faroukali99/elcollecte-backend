package com.elcollecte.utilisateur.service;

import com.elcollecte.common.exception.BusinessException;
import com.elcollecte.utilisateur.dto.AssignPermissionsRequest;
import com.elcollecte.utilisateur.dto.CreateProfilRequest;
import com.elcollecte.utilisateur.dto.ProfilDto;
import com.elcollecte.utilisateur.dto.UpdateProfilRequest;
import com.elcollecte.utilisateur.entity.Organisation;
import com.elcollecte.utilisateur.entity.Permission;
import com.elcollecte.utilisateur.entity.Profil;
import com.elcollecte.utilisateur.entity.ProfilPermission;
import com.elcollecte.utilisateur.repository.OrganisationRepository;
import com.elcollecte.utilisateur.repository.PermissionRepository;
import com.elcollecte.utilisateur.repository.ProfilPermissionRepository;
import com.elcollecte.utilisateur.repository.ProfilRepository;
import com.elcollecte.utilisateur.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class ProfilService {

    private final ProfilRepository profilRepository;
    private final PermissionRepository permissionRepository;
    private final ProfilPermissionRepository profilPermissionRepository;
    private final OrganisationRepository organisationRepository;
    private final UserRepository userRepository;

    public ProfilService(ProfilRepository profilRepository,
                         PermissionRepository permissionRepository,
                         ProfilPermissionRepository profilPermissionRepository,
                         OrganisationRepository organisationRepository,
                         UserRepository userRepository) {
        this.profilRepository = profilRepository;
        this.permissionRepository = permissionRepository;
        this.profilPermissionRepository = profilPermissionRepository;
        this.organisationRepository = organisationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProfilDto> findAll(Long organisationId, Pageable pageable) {
        Page<Profil> page = (organisationId != null)
            ? profilRepository.findAllByOrganisationIdOrOrganisationIsNull(organisationId, pageable)
            : profilRepository.findAllByOrganisationIsNull(pageable);
        return page.map(ProfilDto::from);
    }

    @Transactional(readOnly = true)
    public ProfilDto findById(Long id) {
        return profilRepository.findById(id)
            .map(ProfilDto::from)
            .orElseThrow(() -> new NoSuchElementException("Profil introuvable: " + id));
    }

    @Transactional
    public ProfilDto create(CreateProfilRequest req) {
        if (profilRepository.existsByCode(req.code())) {
            throw BusinessException.conflict("Un profil avec le code '" + req.code() + "' existe déjà");
        }

        Organisation organisation = null;
        if (req.organisationId() != null) {
            organisation = organisationRepository.findById(req.organisationId())
                .orElseThrow(() -> new NoSuchElementException(
                    "Organisation introuvable: " + req.organisationId()));
        }

        // Les profils créés par l'ADMIN via l'API ne sont jamais "système" :
        // seuls les 4 profils historiques migrés (ADMIN, CHEF_PROJET,
        // ENQUETEUR, ANALYSTE) le sont, via la migration Flyway V4.
        Profil profil = new Profil(req.code().toUpperCase(), req.libelle(),
            req.description(), organisation, false);

        return ProfilDto.from(profilRepository.save(profil));
    }

    @Transactional
    public ProfilDto update(Long id, UpdateProfilRequest req) {
        Profil profil = getProfilOrThrow(id);
        profil.setLibelle(req.libelle());
        profil.setDescription(req.description());
        return ProfilDto.from(profilRepository.save(profil));
    }

    @Transactional
    public void desactiver(Long id) {
        Profil profil = getProfilOrThrow(id);
        if (profil.isSysteme()) {
            throw BusinessException.forbidden("Un profil système ne peut pas être désactivé");
        }
        profil.setActive(false);
        profilRepository.save(profil);
    }

    @Transactional
    public void reactiver(Long id) {
        Profil profil = getProfilOrThrow(id);
        profil.setActive(true);
        profilRepository.save(profil);
    }

    @Transactional
    public void delete(Long id) {
        Profil profil = getProfilOrThrow(id);
        if (profil.isSysteme()) {
            throw BusinessException.forbidden(
                "Un profil système ne peut pas être supprimé (compatibilité avec les rôles historiques)");
        }
        long usersCount = userRepository.countByProfilId(id);
        if (usersCount > 0) {
            throw BusinessException.conflict(
                "Impossible de supprimer ce profil : " + usersCount + " utilisateur(s) y sont rattachés");
        }
        profilRepository.delete(profil);
    }

    @Transactional
    public ProfilDto assignPermissions(Long profilId, AssignPermissionsRequest req) {
        Profil profil = getProfilOrThrow(profilId);

        for (Long permissionId : req.permissionIds()) {
            if (profilPermissionRepository.existsByProfilIdAndPermissionId(profilId, permissionId)) {
                continue; // déjà associée, idempotent
            }
            Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new NoSuchElementException(
                    "Permission introuvable: " + permissionId));
            profilPermissionRepository.save(new ProfilPermission(profil, permission));
        }

        return findById(profilId);
    }

    @Transactional
    public void removePermission(Long profilId, Long permissionId) {
        getProfilOrThrow(profilId); // s'assure que le profil existe
        profilPermissionRepository.deleteByProfilIdAndPermissionId(profilId, permissionId);
    }

    private Profil getProfilOrThrow(Long id) {
        return profilRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Profil introuvable: " + id));
    }
}

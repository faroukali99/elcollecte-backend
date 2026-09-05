package com.elcollecte.utilisateur.service;

import com.elcollecte.common.exception.BusinessException;
import com.elcollecte.utilisateur.dto.CreatePermissionRequest;
import com.elcollecte.utilisateur.dto.PermissionDto;
import com.elcollecte.utilisateur.entity.Permission;
import com.elcollecte.utilisateur.repository.PermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Transactional(readOnly = true)
    public List<PermissionDto> findAll() {
        return permissionRepository.findAll().stream()
            .map(PermissionDto::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<PermissionDto> findByCategorie(String categorie) {
        return permissionRepository.findAllByCategorie(categorie).stream()
            .map(PermissionDto::from)
            .toList();
    }

    @Transactional
    public PermissionDto create(CreatePermissionRequest req) {
        String code = req.code().toUpperCase();
        if (permissionRepository.existsByCode(code)) {
            throw BusinessException.conflict("Une permission avec le code '" + code + "' existe déjà");
        }
        Permission permission = new Permission(code, req.libelle(), req.categorie(), req.description());
        return PermissionDto.from(permissionRepository.save(permission));
    }
}

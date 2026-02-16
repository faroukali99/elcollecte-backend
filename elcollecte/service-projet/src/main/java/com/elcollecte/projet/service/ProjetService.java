package com.elcollecte.projet.service;

import com.elcollecte.projet.dto.CreateProjetRequest;
import com.elcollecte.projet.dto.ProjetDto;
import com.elcollecte.projet.dto.UpdateProjetRequest;
import com.elcollecte.projet.entity.Projet;
import com.elcollecte.projet.entity.ProjetEnqueteur;
import com.elcollecte.projet.repository.ProjetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class ProjetService {

    private final ProjetRepository          projetRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProjetService(ProjetRepository projetRepository,
                         KafkaTemplate<String, Object> kafkaTemplate) {
        this.projetRepository = projetRepository;
        this.kafkaTemplate    = kafkaTemplate;
    }

    @Transactional(readOnly = true)
    public Page<ProjetDto> findAll(Long userId, String role, Long orgId, Pageable pageable) {
        Page<Projet> page = switch (role) {
            case "ADMIN"       -> projetRepository.findAllByOrganisationId(orgId, pageable);
            case "CHEF_PROJET" -> projetRepository.findAccessibleByUser(orgId, userId, pageable);
            case "ENQUETEUR"   -> projetRepository.findAccessibleByUser(orgId, userId, pageable);
            default            -> projetRepository.findAccessibleByUser(orgId, userId, pageable);
        };
        List<ProjetDto> dtos = page.getContent().stream().map(ProjetDto::from).toList();
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ProjetDto findById(Long id, Long orgId) {
        return projetRepository.findByIdAndOrganisationId(id, orgId)
            .map(ProjetDto::from)
            .orElseThrow(() -> new NoSuchElementException("Projet introuvable: " + id));
    }

    @Transactional
    public ProjetDto create(CreateProjetRequest req, Long chefProjetId, Long orgId) {
        Projet projet = new Projet();
        projet.setTitre(req.titre());
        projet.setDescription(req.description());
        projet.setOrganisationId(orgId);
        projet.setChefProjetId(chefProjetId);
        projet.setDateDebut(req.dateDebut());
        projet.setDateFin(req.dateFin());
        projet.setZoneGeo(req.zoneGeo());
        projet.setStatut(Projet.Statut.BROUILLON);

        Projet saved = projetRepository.save(projet);

        Map<String, Object> event = new HashMap<>();
        event.put("type",      "PROJET_CREE");
        event.put("projetId",  saved.getId());
        event.put("userId",    chefProjetId);
        kafkaTemplate.send("projet.cree", event);

        return ProjetDto.from(saved);
    }

    @Transactional
    public ProjetDto update(Long id, UpdateProjetRequest req, Long orgId) {
        Projet projet = projetRepository.findByIdAndOrganisationId(id, orgId)
            .orElseThrow(() -> new NoSuchElementException("Projet introuvable: " + id));

        if (req.titre()       != null) projet.setTitre(req.titre());
        if (req.description() != null) projet.setDescription(req.description());
        if (req.statut()      != null) projet.setStatut(Projet.Statut.valueOf(req.statut()));
        if (req.dateFin()     != null) projet.setDateFin(req.dateFin());
        if (req.zoneGeo()     != null) projet.setZoneGeo(req.zoneGeo());

        return ProjetDto.from(projetRepository.save(projet));
    }

    @Transactional
    public void addEnqueteur(Long projetId, Long enqueteurId, Long orgId) {
        Projet projet = projetRepository.findByIdAndOrganisationId(projetId, orgId)
            .orElseThrow(() -> new NoSuchElementException("Projet introuvable"));

        boolean alreadyAssigned = projet.getEnqueteurs().stream()
            .anyMatch(e -> e.getUserId().equals(enqueteurId) && e.isActive());

        if (!alreadyAssigned) {
            projet.getEnqueteurs().add(new ProjetEnqueteur(projet, enqueteurId));
            projetRepository.save(projet);
        }
    }

    @Transactional
    public void removeEnqueteur(Long projetId, Long enqueteurId, Long orgId) {
        Projet projet = projetRepository.findByIdAndOrganisationId(projetId, orgId)
            .orElseThrow(() -> new NoSuchElementException("Projet introuvable"));

        projet.getEnqueteurs().stream()
            .filter(e -> e.getUserId().equals(enqueteurId))
            .forEach(e -> e.setActive(false));

        projetRepository.save(projet);
    }
}

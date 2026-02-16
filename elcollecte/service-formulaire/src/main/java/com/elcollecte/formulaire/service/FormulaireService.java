package com.elcollecte.formulaire.service;

import com.elcollecte.formulaire.dto.*;
import com.elcollecte.formulaire.entity.Formulaire;
import com.elcollecte.formulaire.repository.FormulaireRepository;
import org.springframework.data.domain.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class FormulaireService {

    private final FormulaireRepository     formulaireRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public FormulaireService(FormulaireRepository formulaireRepo,
                             KafkaTemplate<String, Object> kafkaTemplate) {
        this.formulaireRepo = formulaireRepo;
        this.kafkaTemplate  = kafkaTemplate;
    }

    @Transactional(readOnly = true)
    public Page<FormulaireDto> findByProjet(Long projetId, boolean activeOnly,
                                            Pageable pageable) {
        Page<Formulaire> page = activeOnly
                ? formulaireRepo.findByProjetIdAndActiveTrue(projetId, pageable)
                : formulaireRepo.findByProjetId(projetId, pageable);
        return page.map(FormulaireDto::from);
    }

    @Transactional(readOnly = true)
    public FormulaireDto findById(Long id) {
        return formulaireRepo.findById(id)
                .map(FormulaireDto::from)
                .orElseThrow(() -> new NoSuchElementException("Formulaire introuvable: " + id));
    }

    @Transactional
    public FormulaireDto create(CreateFormulaireRequest req, Long createurId) {
        Formulaire f = new Formulaire();
        f.setProjetId(req.projetId());
        f.setCreateurId(createurId);
        f.setTitre(req.titre());
        f.setDescription(req.description());
        f.setSchemaQuestions(req.schemaQuestions() != null
                ? req.schemaQuestions() : List.of());

        Formulaire saved = formulaireRepo.save(f);
        return FormulaireDto.from(saved);
    }

    @Transactional
    public FormulaireDto update(Long id, UpdateFormulaireRequest req) {
        Formulaire f = formulaireRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Formulaire introuvable: " + id));

        if (req.titre()           != null) f.setTitre(req.titre());
        if (req.description()     != null) f.setDescription(req.description());
        if (req.schemaQuestions() != null) {
            f.setSchemaQuestions(req.schemaQuestions());
            f.setVersion(f.getVersion() + 1); // bump de version à chaque modification
        }

        return FormulaireDto.from(formulaireRepo.save(f));
    }

    @Transactional
    public void publier(Long id, Long userId) {
        Formulaire f = formulaireRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Formulaire introuvable: " + id));
        f.setActive(true);
        formulaireRepo.save(f);

        Map<String, Object> event = new HashMap<>();
        event.put("type",         "FORMULAIRE_PUBLIE");
        event.put("entityType",   "FORMULAIRE");
        event.put("entityId",     id);
        event.put("userId",       userId);
        event.put("projetId",     f.getProjetId());
        kafkaTemplate.send("formulaire.publie", String.valueOf(id), event);
    }

    @Transactional
    public void archiver(Long id) {
        Formulaire f = formulaireRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Formulaire introuvable: " + id));
        f.setActive(false);
        formulaireRepo.save(f);
    }
}
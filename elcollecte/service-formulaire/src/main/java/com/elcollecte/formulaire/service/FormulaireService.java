package com.elcollecte.formulaire.service;

import com.elcollecte.formulaire.dto.CreateFormulaireRequest;
import com.elcollecte.formulaire.dto.FormulaireDto;
import com.elcollecte.formulaire.dto.FormulaireVersionDto;
import com.elcollecte.formulaire.dto.QuestionDto;
import com.elcollecte.formulaire.dto.SectionDto;
import com.elcollecte.formulaire.entity.Formulaire;
import com.elcollecte.formulaire.entity.FormulaireVersion;
import com.elcollecte.formulaire.entity.Organisation;
import com.elcollecte.formulaire.entity.Question;
import com.elcollecte.formulaire.entity.Section;
import com.elcollecte.formulaire.repository.FormulaireRepository;
import com.elcollecte.formulaire.repository.FormulaireVersionRepository;
import com.elcollecte.formulaire.repository.QuestionRepository;
import com.elcollecte.formulaire.repository.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class FormulaireService {

    private final FormulaireRepository formulaireRepository;
    private final FormulaireVersionRepository versionRepository;
    private final SectionRepository sectionRepository;
    private final QuestionRepository questionRepository;

    public FormulaireService(FormulaireRepository formulaireRepository,
                             FormulaireVersionRepository versionRepository,
                             SectionRepository sectionRepository,
                             QuestionRepository questionRepository) {
        this.formulaireRepository = formulaireRepository;
        this.versionRepository = versionRepository;
        this.sectionRepository = sectionRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public FormulaireDto create(CreateFormulaireRequest req, Long orgId) {
        Formulaire formulaire = new Formulaire();
        formulaire.setNom(req.nom());
        formulaire.setDescription(req.description());
        formulaire.setCode(req.code());
        
        if (req.organisationId() != null) {
            Organisation org = new Organisation();
            org.setId(req.organisationId());
            formulaire.setOrganisation(org);
        }
        
        formulaire.setActif(true);
        
        Formulaire saved = formulaireRepository.save(formulaire);
        
        // Créer automatiquement la version 1
        FormulaireVersion v1 = new FormulaireVersion();
        v1.setFormulaire(saved);
        v1.setNumeroVersion(1);
        v1.setDescription("Version initiale");
        v1.setPublie(false);
        v1.setActive(true);
        versionRepository.save(v1);
        
        return FormulaireDto.from(saved);
    }

    @Transactional(readOnly = true)
    public List<FormulaireDto> findAll(Long orgId) {
        return formulaireRepository.findActifsByOrganisation(orgId).stream()
            .map(FormulaireDto::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public FormulaireDto findById(Long id) {
        return formulaireRepository.findById(id)
            .map(FormulaireDto::from)
            .orElseThrow(() -> new NoSuchElementException("Formulaire introuvable: " + id));
    }

    @Transactional(readOnly = true)
    public FormulaireVersionDto getLastPublishedVersion(Long formulaireId) {
        return versionRepository.findLastPublishedVersion(formulaireId)
            .map(FormulaireVersionDto::from)
            .orElseThrow(() -> new NoSuchElementException("Aucune version publiée"));
    }

    @Transactional(readOnly = true)
    public FormulaireVersionDto getLastActiveVersion(Long formulaireId) {
        return versionRepository.findLastActiveVersion(formulaireId)
            .map(FormulaireVersionDto::from)
            .orElseThrow(() -> new NoSuchElementException("Aucune version active"));
    }

    @Transactional(readOnly = true)
    public List<SectionDto> getSectionsByVersion(Long versionId) {
        return sectionRepository.findByVersionIdOrderByOrdre(versionId).stream()
            .map(SectionDto::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<QuestionDto> getQuestionsBySection(Long sectionId) {
        return questionRepository.findBySectionIdOrderByOrdre(sectionId).stream()
            .map(QuestionDto::from)
            .toList();
    }

    @Transactional
    public FormulaireVersionDto publishVersion(Long versionId) {
        FormulaireVersion version = versionRepository.findById(versionId)
            .orElseThrow(() -> new NoSuchElementException("Version introuvable"));
        
        version.setPublie(true);
        version.setPublishedAt(java.time.LocalDateTime.now());
        
        // Désactiver les autres versions publiées
        versionRepository.findByFormulaireIdAndNumeroVersion(
            version.getFormulaire().getId(), 
            version.getNumeroVersion()
        ).ifPresent(v -> {
            if (!v.getId().equals(versionId)) {
                v.setPublie(false);
                versionRepository.save(v);
            }
        });
        
        return FormulaireVersionDto.from(versionRepository.save(version));
    }
}

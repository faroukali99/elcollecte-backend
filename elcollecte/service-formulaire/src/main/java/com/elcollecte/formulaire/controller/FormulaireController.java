package com.elcollecte.formulaire.controller;

import com.elcollecte.formulaire.dto.CreateFormulaireRequest;
import com.elcollecte.formulaire.dto.FormulaireDto;
import com.elcollecte.formulaire.dto.FormulaireVersionDto;
import com.elcollecte.formulaire.dto.QuestionDto;
import com.elcollecte.formulaire.dto.SectionDto;
import com.elcollecte.formulaire.service.FormulaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formulaires")
@Tag(name = "Formulaires Dynamiques", description = "Gestion des formulaires dynamiques avec versionnement")
public class FormulaireController {

    private final FormulaireService formulaireService;

    public FormulaireController(FormulaireService formulaireService) {
        this.formulaireService = formulaireService;
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau formulaire")
    public ResponseEntity<FormulaireDto> create(
            @Valid @RequestBody CreateFormulaireRequest request,
            @RequestHeader(value = "X-Org-Id", required = false) Long orgId) {
        
        Long effectiveOrgId = (orgId != null) ? orgId : 1L;
        return ResponseEntity.ok(formulaireService.create(request, effectiveOrgId));
    }

    @GetMapping
    @Operation(summary = "Lister les formulaires actifs")
    public ResponseEntity<List<FormulaireDto>> findAll(
            @RequestHeader(value = "X-Org-Id", required = false) Long orgId) {
        
        Long effectiveOrgId = (orgId != null) ? orgId : 1L;
        return ResponseEntity.ok(formulaireService.findAll(effectiveOrgId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détails d'un formulaire")
    public ResponseEntity<FormulaireDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(formulaireService.findById(id));
    }

    @GetMapping("/{id}/versions/published")
    @Operation(summary = "Dernière version publiée")
    public ResponseEntity<FormulaireVersionDto> getLastPublishedVersion(@PathVariable Long id) {
        return ResponseEntity.ok(formulaireService.getLastPublishedVersion(id));
    }

    @GetMapping("/{id}/versions/active")
    @Operation(summary = "Dernière version active")
    public ResponseEntity<FormulaireVersionDto> getLastActiveVersion(@PathVariable Long id) {
        return ResponseEntity.ok(formulaireService.getLastActiveVersion(id));
    }

    @GetMapping("/versions/{versionId}/sections")
    @Operation(summary = "Sections d'une version")
    public ResponseEntity<List<SectionDto>> getSectionsByVersion(@PathVariable Long versionId) {
        return ResponseEntity.ok(formulaireService.getSectionsByVersion(versionId));
    }

    @GetMapping("/sections/{sectionId}/questions")
    @Operation(summary = "Questions d'une section")
    public ResponseEntity<List<QuestionDto>> getQuestionsBySection(@PathVariable Long sectionId) {
        return ResponseEntity.ok(formulaireService.getQuestionsBySection(sectionId));
    }

    @PostMapping("/versions/{versionId}/publish")
    @Operation(summary = "Publier une version")
    public ResponseEntity<FormulaireVersionDto> publishVersion(@PathVariable Long versionId) {
        return ResponseEntity.ok(formulaireService.publishVersion(versionId));
    }
}

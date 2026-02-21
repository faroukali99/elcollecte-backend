package com.elcollecte.formulaire.service;

import com.elcollecte.formulaire.dto.FormulaireEiesDto;
import com.elcollecte.formulaire.dto.SaveFormulaireRequest;
import com.elcollecte.formulaire.entity.FormulaireEies;
import com.elcollecte.formulaire.repository.FormulaireEiesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Service de gestion des formulaires EIES.
 */
@Service
public class FormulaireEiesService {

    private static final Logger log = LoggerFactory.getLogger(FormulaireEiesService.class);

    private final FormulaireEiesRepository  repo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public FormulaireEiesService(FormulaireEiesRepository repo,
                                  KafkaTemplate<String, Object> kafkaTemplate) {
        this.repo          = repo;
        this.kafkaTemplate = kafkaTemplate;
    }

    // ── CRUD ────────────────────────────────────────────────────────────────

    @Transactional
    public FormulaireEiesDto creerBrouillon(Long userId) {
        FormulaireEies f = new FormulaireEies();
        f.setUserId(userId);
        f.setStatut(FormulaireEies.Statut.BROUILLON);
        return FormulaireEiesDto.from(repo.save(f));
    }

    @Transactional
    public FormulaireEiesDto sauvegarder(Long id, Long userId, SaveFormulaireRequest req) {
        FormulaireEies f = repo.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new NoSuchElementException("Formulaire introuvable: " + id));

        appliquerChamps(f, req);
        f.setScoreCompletude(estimerScore(f));

        return FormulaireEiesDto.from(repo.save(f));
    }

    @Transactional
    public FormulaireEiesDto soumettre(Long id, Long userId) {
        FormulaireEies f = repo.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new NoSuchElementException("Formulaire introuvable: " + id));

        if (f.getStatut() == FormulaireEies.Statut.SOUMIS) {
            throw new IllegalStateException("Ce formulaire est déjà soumis.");
        }

        f.setStatut(FormulaireEies.Statut.SOUMIS);
        f.setSubmittedAt(LocalDateTime.now());
        FormulaireEies saved = repo.save(f);

        // Notifie les autres services via Kafka
        publishEvent("formulaire.eies.soumis", saved, userId);

        log.info("[EIES] Formulaire #{} soumis par user #{}", id, userId);
        return FormulaireEiesDto.from(saved);
    }

    @Transactional(readOnly = true)
    public FormulaireEiesDto getById(Long id, Long userId) {
        return repo.findByIdAndUserId(id, userId)
            .map(FormulaireEiesDto::from)
            .orElseThrow(() -> new NoSuchElementException("Formulaire introuvable: " + id));
    }

    @Transactional(readOnly = true)
    public Page<FormulaireEiesDto> listParUser(Long userId, Pageable pageable) {
        return repo.findByUserId(userId, pageable).map(FormulaireEiesDto::from);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void appliquerChamps(FormulaireEies f, SaveFormulaireRequest req) {
        if (req.projetId()                   != null) f.setProjetId(req.projetId());
        if (req.nomProjet()                  != null) f.setNomProjet(req.nomProjet());
        if (req.region()                     != null) f.setRegion(req.region());
        if (req.departement()                != null) f.setDepartement(req.departement());
        if (req.commune()                    != null) f.setCommune(req.commune());
        if (req.promoteur()                  != null) f.setPromoteur(req.promoteur());
        if (req.emailPromoteur()             != null) f.setEmailPromoteur(req.emailPromoteur());
        if (req.dateDebut()                  != null) f.setDateDebut(req.dateDebut());
        if (req.dureeEstimee()               != null) f.setDureeEstimee(req.dureeEstimee());
        if (req.typeProjet()                 != null) f.setTypeProjet(req.typeProjet());
        if (req.superficie()                 != null) f.setSuperficie(req.superficie());
        if (req.descriptionDetaillee()       != null) f.setDescriptionDetaillee(req.descriptionDetaillee());
        if (req.activitesPrincipales()       != null) f.setActivitesPrincipales(req.activitesPrincipales());
        if (req.besoinsRessources()          != null) f.setBesoinsRessources(req.besoinsRessources());
        if (req.impactFaune()                != null) f.setImpactFaune(req.impactFaune());
        if (req.impactFlore()                != null) f.setImpactFlore(req.impactFlore());
        if (req.impactEau()                  != null) f.setImpactEau(req.impactEau());
        if (req.impactAir()                  != null) f.setImpactAir(req.impactAir());
        if (req.impactSonore()               != null) f.setImpactSonore(req.impactSonore());
        if (req.gestionDechets()             != null) f.setGestionDechets(req.gestionDechets());
        if (req.mesuresAttenuation()         != null) f.setMesuresAttenuation(req.mesuresAttenuation());
        if (req.populationAffectee()         != null) f.setPopulationAffectee(req.populationAffectee());
        if (req.deploiementPopulations()     != null) f.setDeploiementPopulations(req.deploiementPopulations());
        if (req.creationEmplois()            != null) f.setCreationEmplois(req.creationEmplois());
        if (req.impactEconomiqueLocal()      != null) f.setImpactEconomiqueLocal(req.impactEconomiqueLocal());
        if (req.consultationCommunautes()    != null) f.setConsultationCommunautes(req.consultationCommunautes());
        if (req.mesuresCompensationSociale() != null) f.setMesuresCompensationSociale(req.mesuresCompensationSociale());
        if (req.documentsAnnexes()           != null) f.setDocumentsAnnexes(req.documentsAnnexes());
    }

    private int estimerScore(FormulaireEies f) {
        int total = 20;
        int remplis = 0;
        if (notBlank(f.getNomProjet()))               remplis++;
        if (notBlank(f.getRegion()))                  remplis++;
        if (notBlank(f.getDepartement()))             remplis++;
        if (notBlank(f.getCommune()))                 remplis++;
        if (notBlank(f.getPromoteur()))               remplis++;
        if (notBlank(f.getDateDebut()))               remplis++;
        if (notBlank(f.getTypeProjet()))              remplis++;
        if (f.getSuperficie() != null)                remplis++;
        if (notBlank(f.getDescriptionDetaillee()))    remplis++;
        if (notBlank(f.getActivitesPrincipales()))    remplis++;
        if (notBlank(f.getImpactFaune()))             remplis++;
        if (notBlank(f.getImpactFlore()))             remplis++;
        if (notBlank(f.getImpactEau()))               remplis++;
        if (notBlank(f.getImpactAir()))               remplis++;
        if (notBlank(f.getImpactSonore()))            remplis++;
        if (notBlank(f.getGestionDechets()))          remplis++;
        if (notBlank(f.getMesuresAttenuation()))      remplis++;
        if (f.getPopulationAffectee() != null)        remplis++;
        if (notBlank(f.getConsultationCommunautes())) remplis++;
        if (notBlank(f.getMesuresCompensationSociale())) remplis++;
        return remplis * 100 / total;
    }

    private boolean notBlank(String v) {
        return v != null && !v.isBlank();
    }

    private void publishEvent(String topic, FormulaireEies f, Long userId) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("formulaireId", f.getId());
            event.put("projetId",     f.getProjetId());
            event.put("userId",       userId);
            event.put("nomProjet",    f.getNomProjet());
            event.put("timestamp",    LocalDateTime.now().toString());
            kafkaTemplate.send(topic, String.valueOf(f.getId()), event);
        } catch (Exception e) {
            log.warn("[EIES] Impossible d'envoyer l'événement Kafka: {}", e.getMessage());
        }
    }
}

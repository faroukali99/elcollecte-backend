// RapportService.java
package com.elcollecte.rapport.service;

import com.elcollecte.rapport.entity.Rapport;
import com.elcollecte.rapport.repository.RapportRepository;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class RapportService {

    private final RapportRepository rapportRepo;

    public RapportService(RapportRepository rapportRepo) {
        this.rapportRepo = rapportRepo;
    }

    @Transactional
    public Rapport demanderRapport(Long projetId, Long demandeurId,
                                   String titre, String format,
                                   Map<String, Object> parametres) {
        Rapport r = new Rapport();
        r.setProjetId(projetId);
        r.setDemandeurId(demandeurId);
        r.setTitre(titre);
        r.setFormat(Rapport.Format.valueOf(format.toUpperCase()));
        r.setParametres(parametres);
        Rapport saved = rapportRepo.save(r);

        // Lancement asynchrone de la génération
        genererAsync(saved.getId());
        return saved;
    }

    @Async
    @Transactional
    public void genererAsync(Long rapportId) {
        Rapport r = rapportRepo.findById(rapportId).orElse(null);
        if (r == null) return;
        try {
            r.setStatut(Rapport.Statut.EN_COURS);
            rapportRepo.save(r);

            // TODO: intégrer iText pour PDF / Apache POI pour XLSX
            // Simulation de génération
            Thread.sleep(100);

            r.setStatut(Rapport.Statut.TERMINE);
            r.setGeneratedAt(LocalDateTime.now());
            r.setUrlFichier("/rapports/" + rapportId + "." +
                    r.getFormat().name().toLowerCase());
        } catch (Exception e) {
            r.setStatut(Rapport.Statut.ERREUR);
            r.setMessageErreur(e.getMessage());
        }
        rapportRepo.save(r);
    }

    @Transactional(readOnly = true)
    public Page<Rapport> findByProjet(Long projetId, Pageable pageable) {
        return rapportRepo.findByProjetId(projetId, pageable);
    }

    @Transactional(readOnly = true)
    public Rapport findById(Long id) {
        return rapportRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rapport introuvable: " + id));
    }
}
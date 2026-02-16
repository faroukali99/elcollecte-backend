package com.elcollecte.collecte.service;

import com.elcollecte.collecte.dto.*;
import com.elcollecte.collecte.entity.CollecteData;
import com.elcollecte.collecte.repository.CollecteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CollecteService {

    private final CollecteRepository            collecteRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CollecteService(CollecteRepository collecteRepo,
                           KafkaTemplate<String, Object> kafkaTemplate) {
        this.collecteRepo  = collecteRepo;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public CollecteDto submit(SubmitCollecteRequest req, Long enqueteurId) {
        CollecteData c = new CollecteData();
        c.setFormulaireId(req.formulaireId());
        c.setEnqueteurId(enqueteurId);
        c.setProjetId(req.projetId());
        c.setDonnees(req.donnees());
        c.setLatitude(req.latitude());
        c.setLongitude(req.longitude());
        c.setStatut(CollecteData.Statut.SOUMIS);
        c.setOffline(false);

        CollecteData saved = collecteRepo.save(c);
        publishCollecteEvent("collecte.soumise", saved, enqueteurId);
        return CollecteDto.from(saved);
    }

    @Transactional
    public SyncResponse syncBatch(List<OfflineSyncRequest> requests, Long enqueteurId) {
        List<SyncResult> results = new ArrayList<>();

        for (OfflineSyncRequest req : requests) {
            try {
                CollecteData c = new CollecteData();
                c.setFormulaireId(req.formulaireId());
                c.setEnqueteurId(enqueteurId);
                c.setProjetId(req.projetId());
                c.setDonnees(req.donnees());
                c.setLatitude(req.latitude());
                c.setLongitude(req.longitude());
                c.setStatut(CollecteData.Statut.SOUMIS);
                c.setOffline(true);
                c.setCollectedAt(req.collectedAt() != null
                    ? req.collectedAt() : LocalDateTime.now());
                c.setSyncedAt(LocalDateTime.now());

                CollecteData saved = collecteRepo.save(c);
                publishCollecteEvent("collecte.soumise", saved, enqueteurId);
                results.add(new SyncResult(req.localId(), saved.getId(), true, null));

            } catch (Exception e) {
                results.add(new SyncResult(req.localId(), null, false, e.getMessage()));
            }
        }

        long synced = results.stream().filter(SyncResult::success).count();
        long failed = results.size() - synced;
        return new SyncResponse(requests.size(), synced, failed, results);
    }

    @Transactional(readOnly = true)
    public Page<CollecteDto> findAll(Long projetId, String statut,
                                     Long userId, String role, Pageable pageable) {
        Page<CollecteData> page;

        if ("ENQUETEUR".equals(role)) {
            page = projetId != null
                ? collecteRepo.findByProjetIdAndEnqueteurId(projetId, userId, pageable)
                : collecteRepo.findByEnqueteurId(userId, pageable);
        } else {
            page = projetId != null
                ? collecteRepo.findByProjetId(projetId, pageable)
                : collecteRepo.findAll(pageable);
        }

        List<CollecteDto> dtos = page.getContent().stream()
            .map(CollecteDto::from)
            .toList();
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Transactional
    public void valider(Long id, Long validateurId) {
        CollecteData c = collecteRepo.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Collecte introuvable: " + id));

        c.setStatut(CollecteData.Statut.VALIDE);
        c.setValidateurId(validateurId);
        c.setValidatedAt(LocalDateTime.now());
        collecteRepo.save(c);

        publishCollecteEvent("collecte.validee", c, validateurId);
    }

    @Transactional
    public void rejeter(Long id, String motif, Long validateurId) {
        CollecteData c = collecteRepo.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Collecte introuvable: " + id));

        c.setStatut(CollecteData.Statut.REJETE);
        c.setValidateurId(validateurId);
        c.setMotifRejet(motif);
        c.setValidatedAt(LocalDateTime.now());
        collecteRepo.save(c);

        publishCollecteEvent("collecte.rejetee", c, validateurId);
    }

    // ── Private ───────────────────────────────────────────────────────────────
    private void publishCollecteEvent(String topic, CollecteData c, Long userId) {
        Map<String, Object> event = new HashMap<>();
        event.put("collecteId",   c.getId());
        event.put("projetId",     c.getProjetId());
        event.put("formulaireId", c.getFormulaireId());
        event.put("enqueteurId",  c.getEnqueteurId());
        event.put("statut",       c.getStatut().name());
        event.put("userId",       userId);
        event.put("timestamp",    LocalDateTime.now().toString());
        kafkaTemplate.send(topic, String.valueOf(c.getId()), event);
    }
}

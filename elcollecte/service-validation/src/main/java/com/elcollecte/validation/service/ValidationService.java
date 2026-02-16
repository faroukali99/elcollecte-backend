package com.elcollecte.validation.service;

import com.elcollecte.validation.entity.Validation;
import com.elcollecte.validation.repository.ValidationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class ValidationService {

    private final ValidationRepository      validationRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ValidationService(ValidationRepository validationRepo,
                             KafkaTemplate<String, Object> kafkaTemplate) {
        this.validationRepo = validationRepo;
        this.kafkaTemplate  = kafkaTemplate;
    }

    /** Consomme collecte.soumise → crée une entrée EN_ATTENTE */
    @KafkaListener(topics = "collecte.soumise", groupId = "validation-service")
    @Transactional
    public void onCollecteSoumise(Map<String, Object> event) {
        Long collecteId = toLong(event.get("collecteId"));
        Long projetId   = toLong(event.get("projetId"));
        if (collecteId == null || validationRepo.findByCollecteId(collecteId).isPresent()) return;

        Validation v = new Validation();
        v.setCollecteId(collecteId);
        v.setProjetId(projetId);
        validationRepo.save(v);
    }

    @Transactional(readOnly = true)
    public Page<Validation> findByProjet(Long projetId, String statut, Pageable pageable) {
        if (statut != null) {
            return validationRepo.findByProjetIdAndStatut(
                    projetId, Validation.Statut.valueOf(statut), pageable);
        }
        return validationRepo.findByProjetId(projetId, pageable);
    }

    @Transactional
    public Validation valider(Long id, Long validateurId, String commentaire) {
        Validation v = validationRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Validation introuvable: " + id));
        v.setStatut(Validation.Statut.VALIDE);
        v.setValidateurId(validateurId);
        v.setCommentaire(commentaire);
        Validation saved = validationRepo.save(v);

        publishEvent("collecte.validee", saved, validateurId);
        return saved;
    }

    @Transactional
    public Validation rejeter(Long id, Long validateurId,
                              String motif, String commentaire) {
        Validation v = validationRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Validation introuvable: " + id));
        v.setStatut(Validation.Statut.REJETE);
        v.setValidateurId(validateurId);
        v.setMotifRejet(motif);
        v.setCommentaire(commentaire);
        Validation saved = validationRepo.save(v);

        publishEvent("collecte.rejetee", saved, validateurId);
        return saved;
    }

    @Transactional
    public Validation demanderRevision(Long id, Long validateurId, String commentaire) {
        Validation v = validationRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Validation introuvable: " + id));
        v.setStatut(Validation.Statut.REVISION);
        v.setValidateurId(validateurId);
        v.setCommentaire(commentaire);
        return validationRepo.save(v);
    }

    // ── Private ───────────────────────────────────────────────────────────────
    private void publishEvent(String topic, Validation v, Long userId) {
        Map<String, Object> event = new HashMap<>();
        event.put("collecteId", v.getCollecteId());
        event.put("projetId",   v.getProjetId());
        event.put("userId",     userId);
        event.put("statut",     v.getStatut().name());
        kafkaTemplate.send(topic, String.valueOf(v.getCollecteId()), event);
    }

    private Long toLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.longValue();
        try { return Long.valueOf(o.toString()); } catch (Exception e) { return null; }
    }
}
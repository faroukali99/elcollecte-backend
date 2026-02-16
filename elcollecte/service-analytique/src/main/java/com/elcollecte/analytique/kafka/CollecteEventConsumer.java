package com.elcollecte.analytique.kafka;

import com.elcollecte.analytique.service.StatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CollecteEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(CollecteEventConsumer.class);

    private final StatsService statsService;

    public CollecteEventConsumer(StatsService statsService) {
        this.statsService = statsService;
    }

    @KafkaListener(topics = "collecte.soumise", groupId = "analytique-service")
    public void onCollecteSoumise(Map<String, Object> event) {
        try {
            Long projetId = toLong(event.get("projetId"));
            log.info("[ANALYTICS] collecte.soumise - projetId={}", projetId);
            statsService.onNouvelleCollecte(projetId);
        } catch (Exception e) {
            log.error("[ANALYTICS] Erreur traitement collecte.soumise", e);
        }
    }

    @KafkaListener(topics = "collecte.validee", groupId = "analytique-service")
    public void onCollecteValidee(Map<String, Object> event) {
        try {
            Long projetId = toLong(event.get("projetId"));
            log.info("[ANALYTICS] collecte.validee - projetId={}", projetId);
            statsService.onCollecteValidee(projetId);
        } catch (Exception e) {
            log.error("[ANALYTICS] Erreur traitement collecte.validee", e);
        }
    }

    @KafkaListener(topics = "collecte.rejetee", groupId = "analytique-service")
    public void onCollecteRejetee(Map<String, Object> event) {
        try {
            Long projetId = toLong(event.get("projetId"));
            statsService.onCollecteRejetee(projetId);
        } catch (Exception e) {
            log.error("[ANALYTICS] Erreur traitement collecte.rejetee", e);
        }
    }

    @KafkaListener(topics = "projet.cree", groupId = "analytique-service")
    public void onProjetCree(Map<String, Object> event) {
        try {
            Long projetId = toLong(event.get("projetId"));
            statsService.initProjet(projetId);
        } catch (Exception e) {
            log.error("[ANALYTICS] Erreur traitement projet.cree", e);
        }
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number n) return n.longValue();
        return Long.valueOf(value.toString());
    }
}

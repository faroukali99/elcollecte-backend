package com.elcollecte.audit.kafka;

import com.elcollecte.audit.entity.AuditLog;
import com.elcollecte.audit.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class AuditEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(AuditEventConsumer.class);

    private final AuditLogRepository auditRepo;

    public AuditEventConsumer(AuditLogRepository auditRepo) {
        this.auditRepo = auditRepo;
    }

    @KafkaListener(topics = "audit.events", groupId = "audit-service")
    @Transactional
    public void onAuditEvent(Map<String, Object> event) {
        try {
            AuditLog log_ = new AuditLog();
            log_.setUserId(toLong(event.get("userId")));
            log_.setAction(str(event.get("action")));
            log_.setRessource(str(event.get("ressource")));
            log_.setRessourceId(toLong(event.get("ressourceId")));
            log_.setDetails(str(event.get("details")));
            log_.setIpAddress(str(event.get("ipAddress")));
            auditRepo.save(log_);
        } catch (Exception e) {
            log.error("[AUDIT] Erreur enregistrement audit.events", e);
        }
    }

    @KafkaListener(
        topics = {"collecte.soumise", "collecte.validee", "collecte.rejetee",
                  "projet.cree", "formulaire.publie"},
        groupId = "audit-service-metier"
    )
    @Transactional
    public void onBusinessEvent(Map<String, Object> event) {
        try {
            AuditLog log_ = new AuditLog();
            log_.setAction(str(event.get("type")));
            log_.setRessource(str(event.get("entityType")));
            log_.setRessourceId(toLong(event.get("entityId")));
            log_.setUserId(toLong(event.get("userId")));
            auditRepo.save(log_);
        } catch (Exception e) {
            log.error("[AUDIT] Erreur enregistrement business event", e);
        }
    }

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        try { return Long.valueOf(v.toString()); } catch (Exception e) { return null; }
    }

    private String str(Object v) {
        return v != null ? v.toString() : null;
    }
}

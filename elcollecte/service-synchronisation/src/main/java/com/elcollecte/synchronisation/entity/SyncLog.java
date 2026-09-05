package com.elcollecte.synchronisation.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "sync_logs")
public class SyncLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "device_id", length = 100)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Direction direction;

    @Column(nullable = false)
    private Statut statut;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> details;

    @Column(name = "items_count")
    private Integer itemsCount;

    @Column(name = "errors_count")
    private Integer errorsCount;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() { this.startedAt = LocalDateTime.now(); }

    public enum Direction {
        UPLOAD, DOWNLOAD, BIDIRECTIONAL
    }

    public enum Statut {
        EN_COURS, SUCCES, PARTIEL, ECHEC
    }

    public SyncLog() {}

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getDeviceId() { return deviceId; }
    public Direction getDirection() { return direction; }
    public Statut getStatut() { return statut; }
    public Map<String, Object> getDetails() { return details; }
    public Integer getItemsCount() { return itemsCount; }
    public Integer getErrorsCount() { return errorsCount; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public void setDirection(Direction direction) { this.direction = direction; }
    public void setStatut(Statut statut) { this.statut = statut; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
    public void setItemsCount(Integer itemsCount) { this.itemsCount = itemsCount; }
    public void setErrorsCount(Integer errorsCount) { this.errorsCount = errorsCount; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}

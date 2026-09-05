package com.elcollecte.synchronisation.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "sync_conflicts")
public class SyncConflict {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "entity_type", length = 50, nullable = false)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "local_version")
    private Long localVersion;

    @Column(name = "remote_version")
    private Long remoteVersion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> localData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> remoteData;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Resolution resolution;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolved_by")
    private Long resolvedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public enum Resolution {
        PENDING, LOCAL_WINS, REMOTE_WINS, MANUAL_MERGE
    }

    public SyncConflict() {}

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getEntityType() { return entityType; }
    public Long getEntityId() { return entityId; }
    public Long getLocalVersion() { return localVersion; }
    public Long getRemoteVersion() { return remoteVersion; }
    public Map<String, Object> getLocalData() { return localData; }
    public Map<String, Object> getRemoteData() { return remoteData; }
    public Resolution getResolution() { return resolution; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public Long getResolvedBy() { return resolvedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
    public void setLocalVersion(Long localVersion) { this.localVersion = localVersion; }
    public void setRemoteVersion(Long remoteVersion) { this.remoteVersion = remoteVersion; }
    public void setLocalData(Map<String, Object> localData) { this.localData = localData; }
    public void setRemoteData(Map<String, Object> remoteData) { this.remoteData = remoteData; }
    public void setResolution(Resolution resolution) { this.resolution = resolution; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public void setResolvedBy(Long resolvedBy) { this.resolvedBy = resolvedBy; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

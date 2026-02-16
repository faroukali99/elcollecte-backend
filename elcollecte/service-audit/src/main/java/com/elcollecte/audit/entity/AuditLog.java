package com.elcollecte.audit.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.net.InetAddress;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(length = 100)
    private String ressource;

    @Column(name = "ressource_id")
    private Long ressourceId;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "ip_address")
    @JdbcTypeCode(SqlTypes.INET)
    private InetAddress ipAddress;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public AuditLog() {}

    public Long          getId()          { return id; }
    public Long          getUserId()       { return userId; }
    public String        getAction()       { return action; }
    public String        getRessource()    { return ressource; }
    public Long          getRessourceId()  { return ressourceId; }
    public String        getDetails()      { return details; }
    public String        getIpAddress()    { return ipAddress != null ? ipAddress.getHostAddress() : null; }
    public LocalDateTime getCreatedAt()    { return createdAt; }

    public void setUserId(Long v)       { this.userId = v; }
    public void setAction(String v)     { this.action = v; }
    public void setRessource(String v)  { this.ressource = v; }
    public void setRessourceId(Long v)  { this.ressourceId = v; }
    public void setDetails(String v)    { this.details = v; }
    public void setIpAddress(String v)  {
        if (v == null || v.isBlank()) {
            this.ipAddress = null;
            return;
        }
        try {
            this.ipAddress = InetAddress.getByName(v);
        } catch (Exception e) {
            this.ipAddress = null;
        }
    }
}

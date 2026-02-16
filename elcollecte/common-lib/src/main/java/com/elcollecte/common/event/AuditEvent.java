// AuditEvent.java
package com.elcollecte.common.event;
import java.time.LocalDateTime;

public class AuditEvent {
    private Long userId;
    private String action, ressource, details, ipAddress;
    private Long ressourceId;
    private LocalDateTime timestamp = LocalDateTime.now();

    public AuditEvent() {}

    // getters/setters standards
    public Long getUserId()          { return userId; }
    public String getAction()        { return action; }
    public String getRessource()     { return ressource; }
    public Long getRessourceId()     { return ressourceId; }
    public String getDetails()       { return details; }
    public String getIpAddress()     { return ipAddress; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setUserId(Long v)    { this.userId = v; }
    public void setAction(String v)  { this.action = v; }
    public void setRessource(String v) { this.ressource = v; }
    public void setRessourceId(Long v) { this.ressourceId = v; }
    public void setDetails(String v)   { this.details = v; }
    public void setIpAddress(String v) { this.ipAddress = v; }
    public void setTimestamp(LocalDateTime v) { this.timestamp = v; }
}
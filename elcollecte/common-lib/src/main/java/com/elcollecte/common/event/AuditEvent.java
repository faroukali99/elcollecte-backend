package com.elcollecte.common.event;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuditEvent {
    private Long userId;
    private String action;
    private String ressource;
    private Long ressourceId;
    private String details;
    private String ipAddress;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}

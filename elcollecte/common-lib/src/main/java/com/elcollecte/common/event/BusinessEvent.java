package com.elcollecte.common.event;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BusinessEvent {
    private String type;
    private String entityType;
    private Long entityId;
    private Long userId;
    private Object payload;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}

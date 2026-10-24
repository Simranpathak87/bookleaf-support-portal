package bookleaf.supportportal.dto;

import java.time.LocalDateTime;

public record MessageResponse(Long id, String senderRole, String senderName,
        String body, LocalDateTime createdAt) {}
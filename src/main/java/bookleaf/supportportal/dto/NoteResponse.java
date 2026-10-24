package bookleaf.supportportal.dto;

import java.time.LocalDateTime;

public record NoteResponse(Long id, String adminName, String body, LocalDateTime createdAt) {}
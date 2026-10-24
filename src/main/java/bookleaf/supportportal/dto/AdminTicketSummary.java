package bookleaf.supportportal.dto;

import java.time.LocalDateTime;

public record AdminTicketSummary(Long id, String authorId, String authorName, String bookTitle,
        String subject, String category, String priority, String status,
        String assignedTo, String aiStatus, LocalDateTime createdAt, long ageHours) {}
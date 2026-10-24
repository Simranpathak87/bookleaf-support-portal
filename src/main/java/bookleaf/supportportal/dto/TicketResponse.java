package bookleaf.supportportal.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TicketResponse(Long id, String bookId, String bookTitle, String subject,
        String description, String attachmentName, String status,
        LocalDateTime createdAt, LocalDateTime updatedAt,
        List<MessageResponse> messages) {}
package bookleaf.supportportal.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AdminTicketDetail(Long id, String authorId, String authorName, String authorEmail,
        String bookId, String bookTitle, String subject, String description, String attachmentName,
        String category, boolean categoryOverridden, String priority, boolean priorityOverridden,
        String status, String assignedTo, String aiStatus,
        LocalDateTime createdAt, LocalDateTime updatedAt,
        List<MessageResponse> messages, List<NoteResponse> notes) {}
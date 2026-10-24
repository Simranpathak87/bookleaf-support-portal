package bookleaf.supportportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TicketCreateRequest(
        String bookId, // null/blank = General / Account Level
        @NotBlank @Size(max = 200) String subject,
        @NotBlank @Size(min = 10, max = 5000) String description,
        @Size(max = 255) String attachmentName) {}
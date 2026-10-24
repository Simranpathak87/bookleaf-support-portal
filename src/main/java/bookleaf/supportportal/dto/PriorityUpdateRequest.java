package bookleaf.supportportal.dto;

import bookleaf.supportportal.entity.TicketPriority;
import jakarta.validation.constraints.NotNull;

public record PriorityUpdateRequest(@NotNull TicketPriority priority) {}
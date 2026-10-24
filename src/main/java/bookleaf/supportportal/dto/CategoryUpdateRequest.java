package bookleaf.supportportal.dto;

import bookleaf.supportportal.entity.TicketCategory;
import jakarta.validation.constraints.NotNull;

public record CategoryUpdateRequest(@NotNull TicketCategory category) {}
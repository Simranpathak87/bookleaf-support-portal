package bookleaf.supportportal.dto;

import bookleaf.supportportal.entity.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(@NotNull TicketStatus status) {

	public TicketStatus status() {
		// TODO Auto-generated method stub
		return null;
	}}
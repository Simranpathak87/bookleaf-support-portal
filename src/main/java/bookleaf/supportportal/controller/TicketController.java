package bookleaf.supportportal.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import bookleaf.supportportal.dto.TicketCreateRequest;
import bookleaf.supportportal.dto.TicketResponse;
import bookleaf.supportportal.service.TicketService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> create(Authentication auth,
            @Valid @RequestBody TicketCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketService.create(auth.getName(), req));
    }

    @GetMapping
    public List<TicketResponse> myTickets(Authentication auth) {
        return ticketService.listForAuthor(auth.getName());
    }

    @GetMapping("/{id}")
    public TicketResponse getOne(Authentication auth, @PathVariable Long id) {
        return ticketService.getForAuthor(auth.getName(), id);
    }
}
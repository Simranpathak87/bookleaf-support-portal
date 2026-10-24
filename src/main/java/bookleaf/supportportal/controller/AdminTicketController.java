package bookleaf.supportportal.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import bookleaf.supportportal.dto.*;
import bookleaf.supportportal.entity.TicketCategory;
import bookleaf.supportportal.entity.TicketPriority;
import bookleaf.supportportal.entity.TicketStatus;
import bookleaf.supportportal.service.AdminTicketService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/tickets")
public class AdminTicketController {

    private final AdminTicketService service;

    public AdminTicketController(AdminTicketService service) {
        this.service = service;
    }

    @GetMapping
    public List<AdminTicketSummary> list(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketCategory category,
            @RequestParam(required = false) TicketPriority priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.list(status, category, priority, from, to);
    }

    @GetMapping("/{id}")
    public AdminTicketDetail get(@PathVariable Long id) {
        return service.get(id);
    }

    @PatchMapping("/{id}/status")
    public AdminTicketDetail status(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest r) {
        return service.updateStatus(id, r.status());
    }

    @PatchMapping("/{id}/category")
    public AdminTicketDetail category(@PathVariable Long id, @Valid @RequestBody CategoryUpdateRequest r) {
        return service.overrideCategory(id, r.category());
    }

    @PatchMapping("/{id}/priority")
    public AdminTicketDetail priority(@PathVariable Long id, @Valid @RequestBody PriorityUpdateRequest r) {
        return service.overridePriority(id, r.priority());
    }

    @PostMapping("/{id}/assign-to-me")
    public AdminTicketDetail assign(@PathVariable Long id, Authentication auth) {
        return service.assignToMe(id, auth.getName());
    }

    @PostMapping("/{id}/notes")
    public ResponseEntity<AdminTicketDetail> note(@PathVariable Long id, Authentication auth,
            @Valid @RequestBody BodyRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addNote(id, auth.getName(), r.body()));
    }

    @PostMapping("/{id}/replies")
    public ResponseEntity<AdminTicketDetail> reply(@PathVariable Long id, Authentication auth,
            @Valid @RequestBody BodyRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.reply(id, auth.getName(), r.body()));
    }
}
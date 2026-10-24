package bookleaf.supportportal.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import bookleaf.supportportal.dto.*;
import bookleaf.supportportal.entity.*;
import bookleaf.supportportal.exception.BadRequestException;
import bookleaf.supportportal.exception.ResourceNotFoundException;
import bookleaf.supportportal.repository.*;

@Service
public class AdminTicketService {

    private final TicketRepository ticketRepo;
    private final TicketMessageRepository messageRepo;
    private final InternalNoteRepository noteRepo;
    private final UserRepository userRepo;

    public AdminTicketService(TicketRepository ticketRepo, TicketMessageRepository messageRepo,
                              InternalNoteRepository noteRepo, UserRepository userRepo) {
        this.ticketRepo = ticketRepo;
        this.messageRepo = messageRepo;
        this.noteRepo = noteRepo;
        this.userRepo = userRepo;
    }

    /** Queue: unresolved first, then most urgent, then oldest first. */
    @Transactional(readOnly = true)
    public List<AdminTicketSummary> list(TicketStatus status, TicketCategory category,
                                         TicketPriority priority, LocalDate from, LocalDate to) {
        Comparator<Ticket> order = Comparator
                .comparing((Ticket t) -> !isActive(t))
                .thenComparing(Ticket::getPriority)   // enum order: CRITICAL first
                .thenComparing(Ticket::getCreatedAt); // oldest first
        return ticketRepo.findAll().stream()
                .filter(t -> status == null || t.getStatus() == status)
                .filter(t -> category == null || t.getCategory() == category)
                .filter(t -> priority == null || t.getPriority() == priority)
                .filter(t -> from == null || !t.getCreatedAt().toLocalDate().isBefore(from))
                .filter(t -> to == null || !t.getCreatedAt().toLocalDate().isAfter(to))
                .sorted(order)
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminTicketDetail get(Long id) {
        return toDetail(find(id));
    }

    @Transactional
    public AdminTicketDetail updateStatus(Long id, TicketStatus status) {
        Ticket t = find(id);
        t.setStatus(status);
        ticketRepo.save(t);
        return toDetail(t);
    }

    @Transactional
    public AdminTicketDetail overrideCategory(Long id, TicketCategory category) {
        Ticket t = find(id);
        t.setCategory(category);
        t.setCategoryOverridden(true); // AI will not overwrite this later
        ticketRepo.save(t);
        return toDetail(t);
    }

    @Transactional
    public AdminTicketDetail overridePriority(Long id, TicketPriority priority) {
        Ticket t = find(id);
        t.setPriority(priority);
        t.setPriorityOverridden(true);
        ticketRepo.save(t);
        return toDetail(t);
    }

    @Transactional
    public AdminTicketDetail assignToMe(Long id, String adminId) {
        Ticket t = find(id);
        t.setAssignedAdmin(findUser(adminId));
        if (t.getStatus() == TicketStatus.OPEN) t.setStatus(TicketStatus.IN_PROGRESS);
        ticketRepo.save(t);
        return toDetail(t);
    }

    @Transactional
    public AdminTicketDetail addNote(Long id, String adminId, String body) {
        Ticket t = find(id);
        InternalNote n = new InternalNote();
        n.setTicket(t);
        n.setAdmin(findUser(adminId));
        n.setBody(body.trim());
        noteRepo.save(n);
        return toDetail(t);
    }

    @Transactional
    public AdminTicketDetail reply(Long id, String adminId, String body) {
        Ticket t = find(id);
        if (t.getStatus() == TicketStatus.CLOSED) {
            throw new BadRequestException("Ticket is closed. Reopen it before replying.");
        }
        User admin = findUser(adminId);
        TicketMessage m = new TicketMessage();
        m.setTicket(t);
        m.setSenderRole("ADMIN");
        m.setSenderName(admin.getName());
        m.setBody(body.trim());
        messageRepo.save(m);

        if (t.getStatus() == TicketStatus.OPEN) t.setStatus(TicketStatus.IN_PROGRESS);
        if (t.getAssignedAdmin() == null) t.setAssignedAdmin(admin);
        t.setUpdatedAt(LocalDateTime.now()); // lets the author's page notice the change
        ticketRepo.save(t);
        return toDetail(t);
    }

    // ---------- helpers ----------

    private boolean isActive(Ticket t) {
        return t.getStatus() == TicketStatus.OPEN || t.getStatus() == TicketStatus.IN_PROGRESS;
    }

    private Ticket find(Long id) {
        return ticketRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
    }

    private User findUser(String id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private AdminTicketSummary toSummary(Ticket t) {
        Book b = t.getBook();
        return new AdminTicketSummary(t.getId(), t.getAuthor().getId(), t.getAuthor().getName(),
                b == null ? "General / Account Level" : b.getTitle(),
                t.getSubject(), t.getCategory().name(), t.getPriority().name(), t.getStatus().name(),
                t.getAssignedAdmin() == null ? null : t.getAssignedAdmin().getName(),
                t.getAiStatus(), t.getCreatedAt(),
                Duration.between(t.getCreatedAt(), LocalDateTime.now()).toHours());
    }

    private AdminTicketDetail toDetail(Ticket t) {
        User a = t.getAuthor();
        Book b = t.getBook();
        List<MessageResponse> msgs = messageRepo.findByTicketIdOrderByCreatedAtAsc(t.getId()).stream()
                .map(m -> new MessageResponse(m.getId(), m.getSenderRole(), m.getSenderName(),
                        m.getBody(), m.getCreatedAt()))
                .toList();
        List<NoteResponse> notes = noteRepo.findByTicketIdOrderByCreatedAtAsc(t.getId()).stream()
                .map(n -> new NoteResponse(n.getId(), n.getAdmin().getName(), n.getBody(), n.getCreatedAt()))
                .toList();
        return new AdminTicketDetail(t.getId(), a.getId(), a.getName(), a.getEmail(),
                b == null ? null : b.getId(),
                b == null ? "General / Account Level" : b.getTitle(),
                t.getSubject(), t.getDescription(), t.getAttachmentName(),
                t.getCategory().name(), t.isCategoryOverridden(),
                t.getPriority().name(), t.isPriorityOverridden(),
                t.getStatus().name(),
                t.getAssignedAdmin() == null ? null : t.getAssignedAdmin().getName(),
                t.getAiStatus(), t.getCreatedAt(), t.getUpdatedAt(), msgs, notes);
    }
}
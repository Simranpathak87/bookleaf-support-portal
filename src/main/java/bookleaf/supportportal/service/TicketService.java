package bookleaf.supportportal.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import bookleaf.supportportal.dto.*;
import bookleaf.supportportal.entity.*;
import bookleaf.supportportal.exception.BadRequestException;
import bookleaf.supportportal.exception.ResourceNotFoundException;
import bookleaf.supportportal.repository.*;
import org.springframework.context.ApplicationEventPublisher; 
import bookleaf.supportportal.ai.TicketCreatedEvent;

@Service
public class TicketService {

    private final TicketRepository ticketRepo;
    private final TicketMessageRepository messageRepo;
    private final UserRepository userRepo;
    private final BookRepository bookRepo;
	private ApplicationEventPublisher events;

    public TicketService(TicketRepository ticketRepo, TicketMessageRepository messageRepo,
                         UserRepository userRepo, BookRepository bookRepo,ApplicationEventPublisher events) {
        this.ticketRepo = ticketRepo;
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
        this.bookRepo = bookRepo;
        this.events = events;
    }

    @Transactional
    public TicketResponse create(String authorId, TicketCreateRequest req) {
        User author = userRepo.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Ticket t = new Ticket();
        t.setAuthor(author);
        if (req.bookId() != null && !req.bookId().isBlank()) {
            // book must belong to this author
            Book book = bookRepo.findByIdAndAuthorId(req.bookId(), authorId)
                    .orElseThrow(() -> new BadRequestException("Selected book does not belong to you"));
            t.setBook(book);
        }
        t.setSubject(req.subject().trim());
        t.setDescription(req.description().trim());
        t.setAttachmentName(req.attachmentName());
        // category/priority stay UNCLASSIFIED/MEDIUM here; AI fills them in Step 5
        ticketRepo.save(t);
        events.publishEvent(new TicketCreatedEvent(t.getId()));
        return toDto(t);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> listForAuthor(String authorId) {
        return ticketRepo.findByAuthorIdOrderByCreatedAtDesc(authorId)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public TicketResponse getForAuthor(String authorId, Long ticketId) {
        // returns 404 (not 403) for other authors' tickets, so IDs cannot be probed
        Ticket t = ticketRepo.findByIdAndAuthorId(ticketId, authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        return toDto(t);
    }

    private TicketResponse toDto(Ticket t) {
        List<MessageResponse> msgs = messageRepo.findByTicketIdOrderByCreatedAtAsc(t.getId())
                .stream()
                .map(m -> new MessageResponse(m.getId(), m.getSenderRole(),
                        m.getSenderName(), m.getBody(), m.getCreatedAt()))
                .toList();
        Book b = t.getBook();
        return new TicketResponse(t.getId(),
                b == null ? null : b.getId(),
                b == null ? "General / Account Level" : b.getTitle(),
                t.getSubject(), t.getDescription(), t.getAttachmentName(),
                t.getStatus().name(), t.getCreatedAt(), t.getUpdatedAt(), msgs);
    }
}
package bookleaf.supportportal.ai;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import bookleaf.supportportal.entity.Ticket;
import bookleaf.supportportal.repository.TicketRepository;

@Component
public class TicketAiListener {

    private final TicketRepository ticketRepo;
    private final TicketClassifier classifier;

    public TicketAiListener(TicketRepository ticketRepo, TicketClassifier classifier) {
        this.ticketRepo = ticketRepo;
        this.classifier = classifier;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(TicketCreatedEvent e) {
        Ticket t = ticketRepo.findById(e.ticketId()).orElse(null);
        if (t == null) return;
        TicketClassifier.Classification c = classifier.classify(t.getSubject(), t.getDescription());
        if (!t.isCategoryOverridden()) t.setCategory(c.category());
        if (!t.isPriorityOverridden()) t.setPriority(c.priority());
        t.setAiStatus(c.fromAi() ? "DONE" : "FALLBACK");
        ticketRepo.save(t);
    }
}
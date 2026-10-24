package bookleaf.supportportal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import bookleaf.supportportal.entity.TicketMessage;

public interface TicketMessageRepository extends JpaRepository<TicketMessage, Long> {
    List<TicketMessage> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
}
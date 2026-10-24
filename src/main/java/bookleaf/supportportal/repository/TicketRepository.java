package bookleaf.supportportal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import bookleaf.supportportal.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByAuthorIdOrderByCreatedAtDesc(String authorId);
    Optional<Ticket> findByIdAndAuthorId(Long id, String authorId);
}
package bookleaf.supportportal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import bookleaf.supportportal.entity.InternalNote;

public interface InternalNoteRepository extends JpaRepository<InternalNote, Long> {
    List<InternalNote> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
}
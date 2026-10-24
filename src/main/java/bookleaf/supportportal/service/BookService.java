package bookleaf.supportportal.service;

import java.util.List;
import org.springframework.stereotype.Service;
import bookleaf.supportportal.dto.BookResponse;
import bookleaf.supportportal.entity.Book;
import bookleaf.supportportal.repository.BookRepository;

@Service
public class BookService {

    private final BookRepository bookRepo;

    public BookService(BookRepository bookRepo) {
        this.bookRepo = bookRepo;
    }

    public List<BookResponse> getBooksForAuthor(String authorId) {
        return bookRepo.findByAuthorId(authorId).stream().map(this::toDto).toList();
    }

    private BookResponse toDto(Book b) {
        return new BookResponse(b.getId(), b.getTitle(), b.getIsbn(), b.getGenre(),
                b.getPublicationDate(), b.getStatus(), b.getMrp(), b.getRoyaltyPerCopy(),
                b.getCopiesSold(), b.getRoyaltyEarned(), b.getRoyaltyPaid(),
                b.getRoyaltyPending(), b.getLastPayoutDate(), b.getPrintPartner(),
                b.getPlatforms());
    }
}
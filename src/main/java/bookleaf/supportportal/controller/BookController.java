package bookleaf.supportportal.controller;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import bookleaf.supportportal.dto.BookResponse;
import bookleaf.supportportal.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookResponse> myBooks(Authentication auth) {
        return bookService.getBooksForAuthor(auth.getName());
    }
}
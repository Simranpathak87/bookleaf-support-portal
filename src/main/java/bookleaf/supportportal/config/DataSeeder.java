
package bookleaf.supportportal.config;



import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import bookleaf.supportportal.entity.*;
import bookleaf.supportportal.repository.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final BookRepository bookRepo;
    private final PasswordEncoder encoder;

    public DataSeeder(UserRepository userRepo, BookRepository bookRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.bookRepo = bookRepo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepo.count() > 0) return;

        User admin = new User();
        admin.setId("ADMIN001");
        admin.setName("BookLeaf Admin");
        admin.setEmail("admin@bookleaf.com");
        admin.setPasswordHash(encoder.encode("Admin@123"));
        admin.setRole(Role.ADMIN);
        userRepo.save(admin);

        JsonNode root = new ObjectMapper()
                .readTree(new ClassPathResource("bookleaf_sample_data.json").getInputStream());

        for (JsonNode a : root.get("authors")) {
            User author = new User();
            author.setId(a.get("author_id").asText());
            author.setName(a.get("name").asText());
            author.setEmail(a.get("email").asText());
            author.setPasswordHash(encoder.encode("Author@123"));
            author.setRole(Role.AUTHOR);
            author.setPhone(a.get("phone").asText());
            author.setCity(a.get("city").asText());
            author.setJoinedDate(LocalDate.parse(a.get("joined_date").asText()));
            userRepo.save(author);

            for (JsonNode b : a.get("books")) {
                Book book = new Book();
                book.setId(b.get("book_id").asText());
                book.setAuthor(author);
                book.setTitle(b.get("title").asText());
                book.setIsbn(b.get("isbn").asText());
                book.setGenre(b.get("genre").asText());
                book.setPublicationDate(date(b, "publication_date"));
                book.setStatus(b.get("status").asText());
                book.setMrp(integer(b, "mrp"));
                book.setRoyaltyPerCopy(integer(b, "author_royalty_per_copy"));
                book.setCopiesSold(b.get("total_copies_sold").asInt());
                book.setRoyaltyEarned(b.get("total_royalty_earned").asInt());
                book.setRoyaltyPaid(b.get("royalty_paid").asInt());
                book.setRoyaltyPending(b.get("royalty_pending").asInt());
                book.setLastPayoutDate(date(b, "last_royalty_payout_date"));
                book.setPrintPartner(b.get("print_partner").isNull() ? null : b.get("print_partner").asText());
                b.get("available_on").forEach(p -> book.getPlatforms().add(p.asText()));
                bookRepo.save(book);
            }
        }
    }

    private Integer integer(JsonNode n, String f) {
        return n.get(f).isNull() ? null : n.get(f).asInt();
    }

    private LocalDate date(JsonNode n, String f) {
        return n.get(f).isNull() ? null : LocalDate.parse(n.get(f).asText());
    }
}
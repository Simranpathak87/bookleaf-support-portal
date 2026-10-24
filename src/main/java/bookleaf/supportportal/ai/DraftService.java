package bookleaf.supportportal.ai;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import bookleaf.supportportal.entity.Book;
import bookleaf.supportportal.entity.Ticket;
import bookleaf.supportportal.entity.TicketCategory;
import bookleaf.supportportal.exception.ResourceNotFoundException;
import bookleaf.supportportal.repository.TicketRepository;

@Service
public class DraftService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("d MMM yyyy");

    private static final String SYSTEM = """
            You are a support representative at BookLeaf Publishing replying to an author.
            Rules:
            - Use ONLY the facts in the context. Never invent numbers, dates or statuses; if something is missing, say the team will check.
            - Plain text only, no markdown. 120 to 200 words.
            - Start with "Hi <first name>," and end with a clear next step, then "Team BookLeaf".
            """ + KnowledgeBase.TONE;

    private final TicketRepository ticketRepo;
    private final AiClient ai;

    public DraftService(TicketRepository ticketRepo, AiClient ai) {
        this.ticketRepo = ticketRepo;
        this.ai = ai;
    }

    @Transactional
    public String getOrCreateDraft(Long ticketId, boolean regenerate) {
        Ticket t = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        if (!regenerate && t.getAiDraft() != null && !t.getAiDraft().isBlank()) {
            return t.getAiDraft(); // cached: no new tokens spent
        }
        String draft = ai.complete(SYSTEM, buildContext(t), false).trim();
        t.setAiDraft(draft);
        return draft;
    }

    private String buildContext(Ticket t) {
        TicketCategory cat = t.getCategory();
        StringBuilder sb = new StringBuilder();
        sb.append("Author: ").append(t.getAuthor().getName()).append('\n');
        sb.append("Today: ").append(LocalDate.now().format(FMT)).append('\n');
        sb.append("Ticket subject: ").append(t.getSubject()).append('\n');
        sb.append("Ticket description: ").append(t.getDescription()).append("\n\n");

        Book b = t.getBook();
        if (b == null) {
            sb.append("This is a general / account-level query (no specific book).\n");
        } else if (b.getMrp() == null) {
            sb.append("Book: ").append(b.getTitle()).append(" (").append(b.getId()).append(")\n")
              .append("Status: ").append(b.getStatus())
              .append(". The book is still in production: no sales or royalties yet, no MRP or publication date.\n");
        } else {
            sb.append("Book: ").append(b.getTitle()).append(" (").append(b.getId()).append(")\n")
              .append("Status: ").append(b.getStatus()).append('\n')
              .append("MRP: Rs ").append(b.getMrp()).append(", copies sold: ").append(b.getCopiesSold()).append('\n')
              .append("Royalty earned: Rs ").append(b.getRoyaltyEarned())
              .append(", paid: Rs ").append(b.getRoyaltyPaid())
              .append(", pending: Rs ").append(b.getRoyaltyPending()).append('\n')
              .append("Last payout: ").append(b.getLastPayoutDate() == null
                      ? "no payout made yet" : b.getLastPayoutDate().format(FMT)).append('\n')
              .append("Print partner: ").append(b.getPrintPartner()).append('\n')
              .append("Available on: ").append(String.join(", ", b.getPlatforms())).append('\n');
            if (cat == TicketCategory.ROYALTY_PAYMENTS) {
                sb.append("Next royalty payout deadline: ")
                  .append(nextPayoutDeadline(LocalDate.now()).format(FMT)).append('\n');
                sb.append(b.getRoyaltyPending() < 1000
                        ? "Pending royalty is BELOW the Rs 1,000 minimum payout, so it rolls over to the next quarter.\n"
                        : "Pending royalty is above the Rs 1,000 minimum payout.\n");
            }
        }
        sb.append("\nBookLeaf policy:\n").append(KnowledgeBase.forCategory(cat));
        sb.append("\nWrite the reply to the author now.");
        return sb.toString();
    }

    // Quarter ends Mar/Jun/Sep/Dec; payout within 45 days after quarter end
    static LocalDate nextPayoutDeadline(LocalDate today) {
        YearMonth cur = YearMonth.from(today).withMonth(((today.getMonthValue() - 1) / 3 + 1) * 3);
        LocalDate lastQuarterDeadline = cur.minusMonths(3).atEndOfMonth().plusDays(45);
        return lastQuarterDeadline.isBefore(today) ? cur.atEndOfMonth().plusDays(45) : lastQuarterDeadline;
    }
}
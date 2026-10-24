package bookleaf.supportportal.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import bookleaf.supportportal.entity.TicketCategory;
import bookleaf.supportportal.entity.TicketPriority;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class TicketClassifier {

    private static final Logger log = LoggerFactory.getLogger(TicketClassifier.class);

    public record Classification(TicketCategory category, TicketPriority priority, boolean fromAi) {}

    private static final String SYSTEM = """
            You classify author support tickets for BookLeaf Publishing.
            Reply with ONLY a JSON object: {"category":"<CATEGORY>","priority":"<PRIORITY>"}
            CATEGORY must be exactly one of:
            ROYALTY_PAYMENTS (royalty amount, payout delay, bank details)
            ISBN_METADATA (ISBN errors, wrong title/description/metadata)
            PRINTING_QUALITY (print defects, damaged copies, reprints)
            DISTRIBUTION_AVAILABILITY (book unavailable or missing on a platform)
            BOOK_STATUS_PRODUCTION (progress of a book still in production)
            GENERAL_INQUIRY (anything else)
            PRIORITY must be exactly one of:
            CRITICAL: money overdue for many months, data errors hurting sales, legal threats
            HIGH: ISBN errors, defective printed copies, book unavailable on a platform, delayed royalty
            MEDIUM: status or amount questions with no urgency
            LOW: general questions, small updates, how-to questions
            """;

    private final AiClient ai;
    private final ObjectMapper mapper = new ObjectMapper();

    public TicketClassifier(AiClient ai) {
        this.ai = ai;
    }

    public Classification classify(String subject, String description) {
        try {
            String user = "Subject: " + subject + "\nDescription: " + trim(description, 1500);
            String raw = ai.complete(SYSTEM, user, true);
            JsonNode n = mapper.readTree(extractJson(raw));
            TicketCategory cat = TicketCategory.valueOf(n.get("category").asText().trim().toUpperCase());
            TicketPriority pri = TicketPriority.valueOf(n.get("priority").asText().trim().toUpperCase());
            return new Classification(cat, applyPolicy(cat, pri), true);
        } catch (Exception e) {
            log.warn("AI classification failed, using keyword fallback: {}", e.getMessage());
            return fallback(subject + " " + description);
        }
    }

    // BookLeaf policy: ISBN errors are always at least HIGH
    private TicketPriority applyPolicy(TicketCategory cat, TicketPriority pri) {
        if (cat == TicketCategory.ISBN_METADATA
                && (pri == TicketPriority.MEDIUM || pri == TicketPriority.LOW)) {
            return TicketPriority.HIGH;
        }
        return pri;
    }

    private Classification fallback(String text) {
        String s = text.toLowerCase();
        TicketCategory cat = TicketCategory.GENERAL_INQUIRY;
        if (has(s, "isbn", "metadata")) cat = TicketCategory.ISBN_METADATA;
        else if (has(s, "royalt", "payout", "payment", "bank")) cat = TicketCategory.ROYALTY_PAYMENTS;
        else if (has(s, "print", "blurry", "binding", "reprint", "damag", "misalign")) cat = TicketCategory.PRINTING_QUALITY;
        else if (has(s, "unavailable", "amazon", "flipkart", "listing", "not showing")) cat = TicketCategory.DISTRIBUTION_AVAILABILITY;
        else if (has(s, "typesetting", "cover design", "proofread", "production", "status")) cat = TicketCategory.BOOK_STATUS_PRODUCTION;

        TicketPriority pri = (cat == TicketCategory.ISBN_METADATA || cat == TicketCategory.PRINTING_QUALITY)
                ? TicketPriority.HIGH : TicketPriority.MEDIUM;
        return new Classification(cat, pri, false);
    }

    private boolean has(String s, String... keys) {
        for (String k : keys) if (s.contains(k)) return true;
        return false;
    }

    private String extractJson(String raw) {
        int s = raw.indexOf('{'), e = raw.lastIndexOf('}');
        if (s < 0 || e <= s) throw new IllegalArgumentException("No JSON in AI output");
        return raw.substring(s, e + 1);
    }

    private String trim(String t, int max) {
        return t.length() > max ? t.substring(0, max) : t;
    }
}
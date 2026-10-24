package bookleaf.supportportal.ai;

import java.util.Map;
import bookleaf.supportportal.entity.TicketCategory;

public final class KnowledgeBase {
    private KnowledgeBase() {}

    public static final String TONE = """
            Tone rules:
            - Empathetic and professional. Authors are partners, not customers to be managed.
            - Acknowledge the concern first, then give the solution.
            - Be specific: use the real numbers, dates and statuses given in the context.
            - If it is BookLeaf's fault (delayed royalty, ISBN error), own it directly. No corporate deflection.
            - If escalation is needed, give a clear timeline (e.g. "within 48 hours"), never open-ended promises.
            - Always end with a clear next step.
            """;

    private static final Map<TicketCategory, String> SECTIONS = Map.of(
        TicketCategory.ROYALTY_PAYMENTS, """
            Royalty policy: 80/20 split of net profit (80% author, 20% BookLeaf).
            Net profit = MRP minus printing cost, platform commission (Amazon/Flipkart) and shipping.
            Royalties are calculated quarterly and paid within 45 days of quarter end.
            Minimum payout is Rs 1,000; below that, royalties roll over to the next quarter.
            Payouts go by bank transfer to the account linked in the author dashboard.
            Authors can see a per-platform royalty breakdown in the dashboard; offer a line-by-line breakdown if asked.
            If a payment is genuinely overdue, escalate with a 48-hour resolution timeline.
            """,
        TicketCategory.ISBN_METADATA, """
            ISBN policy: every book gets a unique ISBN under BookLeaf's publisher imprint.
            An ISBN under the author's own imprint must be obtained by the author independently.
            ISBN errors (duplicate, wrong book linked, mismatch) are HIGH priority: acknowledge it is a serious data issue,
            escalate to the production team immediately, give a 48-hour resolution timeline.
            """,
        TicketCategory.PRINTING_QUALITY, """
            Printing: most orders are printed in-house (Delhi); overflow goes to Repro India or Epitome Books.
            Standard turnaround is 5-7 business days.
            For quality issues (misprints, binding defects, colour problems): apologise sincerely, ask for photos of the
            defective copies, and confirm a free reprint after verification (5-7 business days).
            """,
        TicketCategory.DISTRIBUTION_AVAILABILITY, """
            Distribution: books are listed on Amazon India, Flipkart, Amazon US, Amazon UK and the BookLeaf Store.
            New listings go live 7-10 business days after publication is complete.
            "Unavailable" on a platform usually means a stock sync issue; BookLeaf can trigger a re-sync, live again in 24-48 hours.
            """,
        TicketCategory.BOOK_STATUS_PRODUCTION, """
            Production stages: Manuscript Received, Editing (if opted), Cover Design, Typesetting, Proofreading,
            ISBN Assignment, Printing, Distribution Setup, Published & Live.
            Authors get an email at each stage. Delays usually happen at Cover Design (waiting for author approval)
            and Proofreading (revision rounds). Be honest about the reason, never blame the author; frame it collaboratively
            and give a specific updated timeline.
            """,
        TicketCategory.GENERAL_INQUIRY, """
            General: BookLeaf is a self-publishing company in India and the US (packages: Standard Free and Bestseller Breakthrough).
            We handle cover design, typesetting, ISBN, printing, distribution and royalty management.
            Metadata updates (e.g. book description) can be submitted via the dashboard or by emailing the BookLeaf team;
            changes reflect on platforms in 3-5 business days.
            """
    );

    public static String forCategory(TicketCategory c) {
        return SECTIONS.getOrDefault(c, SECTIONS.get(TicketCategory.GENERAL_INQUIRY));
    }
}
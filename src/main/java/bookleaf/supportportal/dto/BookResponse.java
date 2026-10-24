package bookleaf.supportportal.dto;

import java.time.LocalDate;
import java.util.List;

public record BookResponse(String id, String title, String isbn, String genre,
        LocalDate publicationDate, String status, Integer mrp, Integer royaltyPerCopy,
        int copiesSold, int royaltyEarned, int royaltyPaid, int royaltyPending,
        LocalDate lastPayoutDate, String printPartner, List<String> platforms) {}
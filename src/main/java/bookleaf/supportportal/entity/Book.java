package bookleaf.supportportal.entity;



import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Book {
    @Id
    private String id; // BK001

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(nullable = false)
    private String title;
    private String isbn;
    private String genre;
    private LocalDate publicationDate;   // null if in production
    private String status;
    private Integer mrp;                 // null if in production
    private Integer royaltyPerCopy;      // null if in production
    private int copiesSold;
    private int royaltyEarned;
    private int royaltyPaid;
    private int royaltyPending;
    private LocalDate lastPayoutDate;    // null if never paid
    private String printPartner;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "book_platforms", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "platform")
    @Builder.Default
    private List<String> platforms = new ArrayList<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public LocalDate getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(LocalDate publicationDate) {
		this.publicationDate = publicationDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getMrp() {
		return mrp;
	}

	public void setMrp(Integer mrp) {
		this.mrp = mrp;
	}

	public Integer getRoyaltyPerCopy() {
		return royaltyPerCopy;
	}

	public void setRoyaltyPerCopy(Integer royaltyPerCopy) {
		this.royaltyPerCopy = royaltyPerCopy;
	}

	public int getCopiesSold() {
		return copiesSold;
	}

	public void setCopiesSold(int copiesSold) {
		this.copiesSold = copiesSold;
	}

	public int getRoyaltyEarned() {
		return royaltyEarned;
	}

	public void setRoyaltyEarned(int royaltyEarned) {
		this.royaltyEarned = royaltyEarned;
	}

	public int getRoyaltyPaid() {
		return royaltyPaid;
	}

	public void setRoyaltyPaid(int royaltyPaid) {
		this.royaltyPaid = royaltyPaid;
	}

	public int getRoyaltyPending() {
		return royaltyPending;
	}

	public void setRoyaltyPending(int royaltyPending) {
		this.royaltyPending = royaltyPending;
	}

	public LocalDate getLastPayoutDate() {
		return lastPayoutDate;
	}

	public void setLastPayoutDate(LocalDate lastPayoutDate) {
		this.lastPayoutDate = lastPayoutDate;
	}

	public String getPrintPartner() {
		return printPartner;
	}

	public void setPrintPartner(String printPartner) {
		this.printPartner = printPartner;
	}

	public List<String> getPlatforms() {
		return platforms;
	}

	public void setPlatforms(List<String> platforms) {
		this.platforms = platforms;
	}
    
    
}
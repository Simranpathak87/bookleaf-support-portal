package bookleaf.supportportal.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book; // null = General / Account Level

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    private String attachmentName; // UI only

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketCategory category = TicketCategory.UNCLASSIFIED;
    private boolean categoryOverridden;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketPriority priority = TicketPriority.MEDIUM;
    private boolean priorityOverridden;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_admin_id")
    private User assignedAdmin;

    @Column(columnDefinition = "TEXT")
    private String aiDraft;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private String aiStatus; // DONE, FALLBACK

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); updatedAt = createdAt; }

    @PreUpdate
    void onUpdate() { updatedAt = LocalDateTime.now(); }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAttachmentName() {
		return attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

	public TicketCategory getCategory() {
		return category;
	}

	public void setCategory(TicketCategory category) {
		this.category = category;
	}

	public boolean isCategoryOverridden() {
		return categoryOverridden;
	}

	public void setCategoryOverridden(boolean categoryOverridden) {
		this.categoryOverridden = categoryOverridden;
	}

	public TicketPriority getPriority() {
		return priority;
	}

	public void setPriority(TicketPriority priority) {
		this.priority = priority;
	}

	public boolean isPriorityOverridden() {
		return priorityOverridden;
	}

	public void setPriorityOverridden(boolean priorityOverridden) {
		this.priorityOverridden = priorityOverridden;
	}

	public TicketStatus getStatus() {
		return status;
	}

	public void setStatus(TicketStatus status) {
		this.status = status;
	}

	public User getAssignedAdmin() {
		return assignedAdmin;
	}

	public void setAssignedAdmin(User assignedAdmin) {
		this.assignedAdmin = assignedAdmin;
	}

	public String getAiDraft() {
		return aiDraft;
	}

	public void setAiDraft(String aiDraft) {
		this.aiDraft = aiDraft;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getAiStatus() {
		return aiStatus;
	}

	public void setAiStatus(String aiStatus) {
		this.aiStatus = aiStatus;
	}

	
	}
	
    
    
    
    
    

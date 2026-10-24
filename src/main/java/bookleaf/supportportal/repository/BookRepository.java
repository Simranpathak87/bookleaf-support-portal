package bookleaf.supportportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;



import bookleaf.supportportal.entity.Book;

public interface BookRepository extends JpaRepository<Book, String> {
		    List<Book> findByAuthorId(String authorId);
		    
		    java.util.Optional<Book> findByIdAndAuthorId(String id, String authorId);
	}
	



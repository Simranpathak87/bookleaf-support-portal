package bookleaf.supportportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import bookleaf.supportportal.entity.User;

public interface UserRepository extends JpaRepository<User, String>{
	
		    Optional<User> findByEmail(String email);

			
	}



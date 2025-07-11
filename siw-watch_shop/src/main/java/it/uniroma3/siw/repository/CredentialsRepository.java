package it.uniroma3.siw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Credentials;

@Repository
public interface CredentialsRepository extends JpaRepository<Credentials, Long>{
	
	public Optional<Credentials> findByEmail(String email);
	
	public Optional<Credentials> findByUsername(String username);
	
	public boolean existsByEmail(String email);
	
	public boolean existsByUsername(String username);
	
	@Query("SELECT c FROM Credentials c WHERE c.username = :value OR c.email = : value")
	public Optional<Credentials> findByUsernameOrEmail(@Param("value") String value);


}

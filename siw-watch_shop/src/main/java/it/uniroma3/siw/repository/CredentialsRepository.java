package it.uniroma3.siw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Credentials;

@Repository
public interface CredentialsRepository extends JpaRepository<Credentials, Long>{
	
	public Optional<Credentials> findByEmail(String email);
	
	public Optional<Credentials> findByUsername(String username);
	
	public boolean existsByEmail(String email);
	
	public boolean existsByUsername(String username);


}

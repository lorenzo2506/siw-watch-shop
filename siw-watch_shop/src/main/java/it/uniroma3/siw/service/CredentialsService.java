package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialsRepository;

@Service
public class CredentialsService {
	
	@Autowired private CredentialsRepository credentialsRepo;
	
	public Credentials getCredentials(Long id) {
		return credentialsRepo.findById(id).get();
	}
	
	public Credentials getCredentialsByUsername(String username) {
		return credentialsRepo.findByUsername(username).get();
	}
	
	public Credentials getCredentialsByEmail(String email) {
		return credentialsRepo.findByUsername(email).get();
	}
	
	public Credentials getCredentialsByUsernameOrEmail(String value) {
		
	    Credentials credentials = getCredentialsByUsername(value);
	    
	    if (credentials != null)
	        return credentials;
	    return getCredentialsByEmail(value);
	}
	
    public boolean existsByUsername(String username) {
        return credentialsRepo.findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return credentialsRepo.findByEmail(email).isPresent();
    }

	
	
	public void saveCredentials(Credentials c) {
		credentialsRepo.save(c);
	}
	
	

}

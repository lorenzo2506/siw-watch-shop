package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class CredentialsService implements UserDetailsService {
    
    @Autowired
    private CredentialsRepository credentialsRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public Credentials saveCredentials(Credentials credentials) {
        System.out.println("=== SAVING CREDENTIALS ===");
        System.out.println("Before save - Email: " + credentials.getEmail());
        System.out.println("Before save - Username: " + credentials.getUsername());
        System.out.println("Before save - Role: " + credentials.getRole());
        System.out.println("Before save - Password is null: " + (credentials.getPassword() == null));
        System.out.println("Before save - User: " + credentials.getUser());
        
        // Salva prima l'utente
        if (credentials.getUser() != null) {
            System.out.println("Saving user first...");
            User savedUser = userRepository.save(credentials.getUser());
            System.out.println("User saved with ID: " + savedUser.getId());
            credentials.setUser(savedUser);
        }
        
        // Poi salva le credenziali
        System.out.println("Saving credentials...");
        Credentials savedCredentials = credentialsRepository.save(credentials);
        System.out.println("Credentials saved with ID: " + savedCredentials.getId());
        
        return savedCredentials;
    }
    
    public Credentials getByUsername(String username) {
        System.out.println("Looking for user with username: " + username);
        Optional<Credentials> credentials = credentialsRepository.findByUsername(username);
        if (credentials.isPresent()) {
            System.out.println("User found!");
            return credentials.get();
        } else {
            System.out.println("User NOT found!");
            return null;
        }
    }
    
    public boolean existsByUsername(String username) {
        boolean exists = credentialsRepository.existsByUsername(username);
        System.out.println("Username '" + username + "' exists: " + exists);
        return exists;
    }
    
    public boolean existsByEmail(String email) {
        boolean exists = credentialsRepository.existsByEmail(email);
        System.out.println("Email '" + email + "' exists: " + exists);
        return exists;
    }
    
    public Credentials getByEmail(String email) {
        System.out.println("Looking for user with email: " + email);
        Optional<Credentials> optional = credentialsRepository.findByEmail(email);
        if (optional.isPresent()) {
            System.out.println("User found by email!");
            return optional.get();
        } else {
            System.out.println("User NOT found by email!");
            return null;
        }
    }
    
    public Credentials getByUsernameOrEmail(String value) {
        return credentialsRepository.findByUsernameOrEmail(value).orElse(null);
    }
    
    // METODO PER GESTIRE IL LOGIN AUTOMATICO TRAMITE SPRING SECURITY
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        System.out.println("=== LOADING USER BY USERNAME (Spring Security) ===");
        System.out.println("Looking for: " + usernameOrEmail);
        
        try {
            Optional<Credentials> credentialsOpt = credentialsRepository.findByUsernameOrEmail(usernameOrEmail);
            
            if (credentialsOpt.isPresent()) {
                Credentials credentials = credentialsOpt.get();
                System.out.println("User found: " + credentials.getUsername());
                System.out.println("Email: " + credentials.getEmail());
                System.out.println("Role: " + credentials.getRole());
                System.out.println("Password is null: " + (credentials.getPassword() == null));
                System.out.println("User object: " + credentials.getUser());
                
                // IMPORTANTE: Verifica che la password non sia null per login tradizionale
                if (credentials.getPassword() == null) {
                    System.out.println("ERROR: Password is null - this might be an OAuth2 user trying traditional login");
                    throw new UsernameNotFoundException("Account OAuth2 non può usare login tradizionale: " + usernameOrEmail);
                }
                
                return credentials;
            } else {
                System.out.println("User not found: " + usernameOrEmail);
                throw new UsernameNotFoundException("User not found: " + usernameOrEmail);
            }
        } catch (Exception e) {
            System.out.println("Exception in loadUserByUsername: " + e.getMessage());
            e.printStackTrace();
            throw new UsernameNotFoundException("Error loading user: " + usernameOrEmail, e);
        }
    }
}
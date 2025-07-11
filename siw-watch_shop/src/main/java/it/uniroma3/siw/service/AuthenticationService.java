package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Role;
import it.uniroma3.siw.model.User;

@Service
public class AuthenticationService {
    
    @Autowired 
    private CredentialsService credentialsService;
    
    public UserDetails getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            Object principal = auth.getPrincipal();
            
            if (principal instanceof Credentials credentials) {
                return credentials;
            } else if (principal instanceof OAuth2User) {
                String email = (String) ((OAuth2User) principal).getAttributes().get("email");
                Credentials credentials = credentialsService.getByEmail(email);
                return credentials;
            }
        }
        return null;
    }
    
    public User getCurrentUser() {
        UserDetails userDetails = getCurrentUserDetails();
        if (userDetails instanceof Credentials credentials) {
            return credentials.getUser();
        }
        return null;
    }
    
    public boolean isAdmin() {
        UserDetails userDetails = getCurrentUserDetails();
        if (userDetails instanceof Credentials credentials) {
            return credentials.getRole() == Role.ADMIN;
        }
        return false;
    }
    
    
}
package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Role;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;

@ControllerAdvice
public class GlobalController {

	@Autowired private CredentialsService credentialsService;
	
    @ModelAttribute("userDetails")
    public UserDetails getUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            Object principal = auth.getPrincipal();
            
            if (principal instanceof Credentials credentials) {
                return credentials;
            }
            else if(principal instanceof OAuth2User) {
            	String email = (String) ((OAuth2User) principal).getAttributes().get("email");
                Credentials credentials = credentialsService.getByEmail(email);
                return credentials;
            }
        }
        return null;
    }

    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            Object principal = auth.getPrincipal();
            
            if (principal instanceof Credentials credentials) {
                return credentials.getUser();
            } else if (principal instanceof OAuth2User) {
                // Recupera l'email dall'OAuth2User e cerca nel database
                String email = (String) ((OAuth2User) principal).getAttributes().get("email");
                Credentials credentials = credentialsService.getByEmail(email);
                return credentials.getUser();
            }
        }
        return null;
    }

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin() {
        UserDetails userDetails = getUserDetails();
        if (userDetails instanceof Credentials) {
            return ((Credentials) userDetails).getRole() == Role.ADMIN;
        }
        return false;
    }

    @ModelAttribute("isUser")
    public boolean isUser() {
        UserDetails userDetails = getUserDetails();
        if (userDetails instanceof Credentials) {
            return ((Credentials) userDetails).getRole() == Role.USER;
        }
        return false;
    }
}
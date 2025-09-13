package it.uniroma3.siw.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Role;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private CredentialsService credentialsService;

    public CustomOAuth2UserService() {
        System.out.println("=== CustomOAuth2UserService CONSTRUCTOR CALLED ===");
    }

    // Per OAuth2 standard
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("=== CustomOAuth2UserService.loadUser() CALLED (OAuth2) ===");
        
        OAuth2User oAuth2User = super.loadUser(userRequest);
        return processOAuth2User(oAuth2User, userRequest.getClientRegistration().getRegistrationId());
    }
    

    public OAuth2User processOAuth2User(OAuth2User oAuth2User, String registrationId) {
        String email = oAuth2User.getAttribute("email");
        
        // Verifica se l'utente esiste già tramite EMAIL
        Credentials existingCredentials = credentialsService.getByEmail(email);
        
        if (existingCredentials != null) {
            System.out.println("User already exists with email: " + email);
            // IMPORTANTE: Restituisci ESATTAMENTE le credenziali esistenti
            // NON creare nuovi oggetti User
            return new CustomOAuth2User(oAuth2User, existingCredentials, true);
        } else {
            // Solo per utenti veramente nuovi
            String name = oAuth2User.getAttribute("given_name");
            String surname = oAuth2User.getAttribute("family_name");
            
            // Crea User temporaneo solo per nuovi utenti
            User newUser = new User();
            newUser.setName(name != null ? name : "");
            newUser.setSurname(surname != null ? surname : "");
            
            Credentials tempCredentials = new Credentials();
            tempCredentials.setEmail(email);
            tempCredentials.setRole(Role.USER);
            tempCredentials.setUser(newUser);
            
            return new CustomOAuth2User(oAuth2User, tempCredentials, false);
        }
    }
}

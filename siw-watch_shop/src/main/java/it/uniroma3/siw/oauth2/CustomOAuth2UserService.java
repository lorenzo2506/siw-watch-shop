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
        System.out.println("=== Processing OAuth2 User ===");
        System.out.println("Provider: " + registrationId);
        System.out.println("User attributes: " + oAuth2User.getAttributes());
        
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("given_name");
        String surname = oAuth2User.getAttribute("family_name");
        
        // Se non abbiamo nome e cognome separati, proviamo a prenderli da "name"
        if (name == null || surname == null) {
            String fullName = oAuth2User.getAttribute("name");
            if (fullName != null) {
                String[] nameParts = fullName.split(" ", 2);
                name = nameParts[0];
                surname = nameParts.length > 1 ? nameParts[1] : "";
            }
        }
        
        System.out.println("Extracted - Email: " + email + ", Name: " + name + ", Surname: " + surname);
        
        if (email == null) {
            System.out.println("ERROR: Email is null!");
            throw new OAuth2AuthenticationException("Email non disponibile dal provider OAuth2");
        }
        
        // Verifica se l'utente esiste già tramite EMAIL (non username!)
        Credentials existingCredentials = credentialsService.getByEmail(email);
        
        if (existingCredentials != null) {
            System.out.println("User already exists with email: " + email);
            System.out.println("Existing username: " + existingCredentials.getUsername());
            System.out.println("Existing user: " + existingCredentials.getUser());
            return new CustomOAuth2User(oAuth2User, existingCredentials, true);
        } else {
            System.out.println("New user with email: " + email + " - needs to complete registration");
            
            // Crea User temporaneo (name, surname)
            User newUser = new User();
            newUser.setName(name != null ? name : "");
            newUser.setSurname(surname != null ? surname : "");
            
            // Crea Credentials temporanee (email, role) - username sarà scelto dopo
            Credentials tempCredentials = new Credentials();
            tempCredentials.setEmail(email);
            tempCredentials.setRole(Role.USER);
            tempCredentials.setUser(newUser);
            // password = null per OAuth2
            // username = null per ora (sarà scelto nello step 2)
            
            System.out.println("Created temp credentials:");
            System.out.println("- Email: " + tempCredentials.getEmail());
            System.out.println("- Username: " + tempCredentials.getUsername() + " (null è normale)");
            System.out.println("- Password: " + tempCredentials.getPassword() + " (null è normale)");
            System.out.println("- Role: " + tempCredentials.getRole());
            System.out.println("- User name: " + tempCredentials.getUser().getName());
            System.out.println("- User surname: " + tempCredentials.getUser().getSurname());
            
            return new CustomOAuth2User(oAuth2User, tempCredentials, false);
        }
    }
}

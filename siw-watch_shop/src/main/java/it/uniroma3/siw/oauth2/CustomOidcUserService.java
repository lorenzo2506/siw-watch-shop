package it.uniroma3.siw.oauth2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService extends OidcUserService {
    
    @Autowired
    private CustomOAuth2UserService oAuth2UserService;
    
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("=== CustomOidcUserService.loadUser() CALLED (OIDC) ===");
        
        OidcUser oidcUser = super.loadUser(userRequest);
        
        // Usa il metodo di processamento del servizio OAuth2
        OAuth2User processedUser = oAuth2UserService.processOAuth2User(oidcUser, userRequest.getClientRegistration().getRegistrationId());
        
        if (processedUser instanceof CustomOAuth2User customUser) {
            return new CustomOidcUser(oidcUser, customUser.getCredentials(), customUser.isExistingUser());
        }
        
        throw new OAuth2AuthenticationException("Errore nel processamento OIDC user");
    }
}
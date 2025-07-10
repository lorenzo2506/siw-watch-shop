package it.uniroma3.siw.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Role;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserService userService;

    @Autowired
    private CredentialsService credentialsService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId(); // es. "google"

        Map<String, Object> attributes = oauth2User.getAttributes();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, attributes);

        String email = userInfo.getEmail();
        String name = userInfo.getName();

        Credentials credentials = credentialsService.getByEmail(email);

        if (credentials == null) {
            // Nuovo utente OAuth2
            User user = new User();
            user.setName(name);
            user.setSurname(""); // lo completerà in seguito
            userService.saveUser(user);

            credentials = new Credentials();
            credentials.setEmail(email);
            credentials.setUsername(null); // verrà scelto dopo il primo login
            credentials.setPassword(null); // nessuna password
            credentials.setRole(Role.USER);
            credentials.setUser(user);

            credentialsService.saveCredentials(credentials);
        }

        return oauth2User;
    }
}

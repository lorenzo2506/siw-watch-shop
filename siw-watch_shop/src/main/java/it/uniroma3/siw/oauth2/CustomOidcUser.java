package it.uniroma3.siw.oauth2;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import it.uniroma3.siw.model.Credentials;

public class CustomOidcUser implements OidcUser {
    
    private final OidcUser oidcUser;
    private final Credentials credentials;
    private final boolean isExistingUser;
    
    public CustomOidcUser(OidcUser oidcUser, Credentials credentials, boolean isExistingUser) {
        this.oidcUser = oidcUser;
        this.credentials = credentials;
        this.isExistingUser = isExistingUser;
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return oidcUser.getAttributes();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return credentials.getAuthorities();
    }
    
    @Override
    public String getName() {
        return credentials.getEmail(); // Usiamo email come identificatore
    }
    
    @Override
    public OidcIdToken getIdToken() {
        return oidcUser.getIdToken();
    }
    
    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUser.getUserInfo();
    }
    
    @Override
    public Map<String, Object> getClaims() {
        return oidcUser.getClaims();
    }
    
    public Credentials getCredentials() {
        return credentials;
    }
    
    public boolean isExistingUser() {
        return isExistingUser;
    }
    
    public String getEmail() {
        return credentials.getEmail();
    }
}
package it.uniroma3.siw.oauth2;

import java.io.IOException;
import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        
        if (!(authentication.getPrincipal() instanceof CustomOAuth2User)) {
            response.sendRedirect("/login?error=oauth");
            return;
        }
        
        CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();
        
        if (customUser.isExistingUser()) {
            // Controlla il ruolo per decidere dove reindirizzare
            Collection<? extends GrantedAuthority> authorities = customUser.getAuthorities();
            
            for (GrantedAuthority authority : authorities) {
                if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                    response.sendRedirect("/admin");
                    return;
                }
            }
            
            response.sendRedirect("/");
        } else {
            request.getSession().setAttribute("credentials", customUser.getCredentials());
            response.sendRedirect("/register/step2");
        }
    }
}

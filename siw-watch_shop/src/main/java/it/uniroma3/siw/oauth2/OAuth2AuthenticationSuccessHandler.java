package it.uniroma3.siw.oauth2;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        System.out.println("=== OAuth2 Authentication Success ===");
        System.out.println("Principal type: " + authentication.getPrincipal().getClass());
        
        if (authentication.getPrincipal() instanceof CustomOAuth2User customUser) {
            System.out.println("CustomOAuth2User detected!");
            handleCustomUser(customUser, request, response);
        } else if (authentication.getPrincipal() instanceof CustomOidcUser customOidcUser) {
            System.out.println("CustomOidcUser detected!");
            handleCustomOidcUser(customOidcUser, request, response);
        } else {
            System.out.println("Unexpected principal type: " + authentication.getPrincipal().getClass());
            response.sendRedirect("/login?error=oauth");
        }
    }
    
    private void handleCustomUser(CustomOAuth2User customUser, HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("User email: " + customUser.getEmail());
        System.out.println("Is existing user: " + customUser.isExistingUser());
        
        if (customUser.isExistingUser()) {
            System.out.println("Redirecting existing user to home");
            response.sendRedirect("/");
        } else {
            System.out.println("New user, redirecting to step 2");
            HttpSession session = request.getSession();
            session.setAttribute("credentials", customUser.getCredentials());
            response.sendRedirect("/register/step2");
        }
    }
    
    private void handleCustomOidcUser(CustomOidcUser customOidcUser, HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("User email: " + customOidcUser.getEmail());
        System.out.println("Is existing user: " + customOidcUser.isExistingUser());
        
        if (customOidcUser.isExistingUser()) {
            System.out.println("Redirecting existing user to home");
            response.sendRedirect("/");
        } else {
            System.out.println("New user, redirecting to step 2");
            HttpSession session = request.getSession();
            session.setAttribute("credentials", customOidcUser.getCredentials());
            response.sendRedirect("/register/step2");
        }
    }
}
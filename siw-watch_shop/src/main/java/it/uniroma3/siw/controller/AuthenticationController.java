package it.uniroma3.siw.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;

@Controller
public class AuthenticationController {

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Mostra login form
    @GetMapping("/login")
    public String login() {
        return "login.html"; // login.html
    }

    // Mostra form registrazione
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("credentials", new Credentials());
        model.addAttribute("user", new User());
        return "register.html"; // register.html
    }

    // Gestisce POST registrazione
    @PostMapping("/register")
    public String register(@ModelAttribute Credentials credentials, @ModelAttribute User user) {
        userService.saveUser(user);
        credentials.setUser(user);
        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        credentials.setRole(Credentials.DEFAULT_ROLE);
        credentials.setUsername(null); // sarà scelto dopo
        credentialsService.saveCredentials(credentials);
        return "redirect:/login";
    }

    // Mostra form per scelta username (post-login)
    @GetMapping("/choose-username")
    public String chooseUsernameForm(Model model) {
        model.addAttribute("credentials", new Credentials());
        return "choose-username.html";
    }

    // Salva lo username scelto
    @PostMapping("/choose-username")
    public String chooseUsername(@RequestParam String username, Authentication auth, Model model) {
        String email = auth.getName(); // username == email per SpringSecurity

        Credentials credentials = credentialsService.getCredentialsByEmail(email);
        if (credentials == null) {
            return "redirect:/logout"; // fallback
        }

        if (credentialsService.existsByUsername(username)) {
            model.addAttribute("error", "Username già in uso");
            return "choose-username.html";
        }

        credentials.setUsername(username);
        credentialsService.saveCredentials(credentials);
        return "redirect:/";
    }
    
    /*@GetMapping("/default")
    public String defaultAfterLogin(Authentication auth) {
        String email = auth.getName(); // viene mappato come principal.name

        Credentials credentials = credentialsService.getCredentialsByEmail(email);
        if (credentials == null || credentials.getUsername() == null) {
            return "redirect:/choose-username.html";
        }

        return "redirect:/";
    }*/

}

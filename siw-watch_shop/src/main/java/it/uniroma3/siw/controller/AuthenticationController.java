package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Role;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@SessionAttributes("credentials")
public class AuthenticationController {

    @Autowired
    private CredentialsService credentialsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Step 1: Mostra form per email e password
    @GetMapping("/register")
    public String showEmailPasswordForm(Model model) {
        Credentials credentials = new Credentials();
        credentials.setUser(new User());
        model.addAttribute("credentials", credentials);
        return "register-step1";
    }

    // Step 1: Processa email e password
    @PostMapping("/register/step1")
    public String processEmailPassword(@ModelAttribute("credentials") Credentials credentials,
                                     Model model, HttpSession session) {
        
        System.out.println("=== STEP 1 DEBUG ===");
        System.out.println("Email: " + credentials.getEmail());
        System.out.println("Password length: " + (credentials.getPassword() != null ? credentials.getPassword().length() : "null"));
        System.out.println("User object: " + credentials.getUser());
        if (credentials.getUser() != null) {
            System.out.println("User name: " + credentials.getUser().getName());
            System.out.println("User surname: " + credentials.getUser().getSurname());
        }
        
        // Controlla se l'email esiste già
        if (credentialsService.existsByEmail(credentials.getEmail())) {
            model.addAttribute("emailExists", "Email già registrata");
            return "register-step1";
        }
        
        // IMPORTANTE: Assicurati che l'oggetto User sia inizializzato
        if (credentials.getUser() == null) {
            credentials.setUser(new User());
        }
        
        // Cripta la password e imposta il ruolo
        String originalPassword = credentials.getPassword();
        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        credentials.setRole(Role.USER);
        
        System.out.println("Original password: " + originalPassword);
        System.out.println("Encoded password: " + credentials.getPassword());
        System.out.println("Role: " + credentials.getRole());
        
        return "redirect:/register/step2";
    }

    // Step 2: Mostra form per username
    @GetMapping("/register/step2")
    public String showUsernameForm(@ModelAttribute("credentials") Credentials credentials, Model model) {
        
        System.out.println("=== STEP 2 GET DEBUG ===");
        System.out.println("Credentials from session: " + credentials);
        
        // Controlla se abbiamo le credenziali dalla sessione
        if (credentials == null || credentials.getEmail() == null) {
            System.out.println("Redirecting to register - missing credentials or email");
            return "redirect:/register";
        }
        
        // Verifica che l'oggetto User sia presente
        if (credentials.getUser() == null) {
            System.out.println("Redirecting to register - missing user object");
            return "redirect:/register";
        }
        
        System.out.println("Email from session: " + credentials.getEmail());
        System.out.println("User from session: " + credentials.getUser());
        if (credentials.getUser() != null) {
            System.out.println("User name from session: " + credentials.getUser().getName());
            System.out.println("User surname from session: " + credentials.getUser().getSurname());
        }
        
        return "register-step2";
    }

    // Step 2: Processa username e completa registrazione
    @PostMapping("/register/step2")
    public String processUsername(@ModelAttribute("credentials") Credentials credentials,
                                Model model, SessionStatus sessionStatus,
                                RedirectAttributes redirectAttributes) {
        
        System.out.println("=== STEP 2 POST DEBUG ===");
        System.out.println("Credentials: " + credentials);
        
        // Verifica che abbiamo tutti i dati necessari
        if (credentials == null || credentials.getEmail() == null || 
            credentials.getPassword() == null || credentials.getUser() == null) {
            System.out.println("Missing required data - redirecting to register");
            return "redirect:/register";
        }
        
        System.out.println("Email: " + credentials.getEmail());
        System.out.println("Username: " + credentials.getUsername());
        System.out.println("Password (encoded): " + credentials.getPassword());
        System.out.println("Role: " + credentials.getRole());
        System.out.println("User: " + credentials.getUser());
        if (credentials.getUser() != null) {
            System.out.println("User name: " + credentials.getUser().getName());
            System.out.println("User surname: " + credentials.getUser().getSurname());
        }
        
        // Controlla se username esiste già
        if (credentialsService.existsByUsername(credentials.getUsername())) {
            model.addAttribute("usernameExists", "Username già in uso");
            return "register-step2";
        }
        
        // Controlla username vuoto (usa isBlank() invece di == "")
        if (credentials.getUsername() == null || credentials.getUsername().trim().isEmpty()) {
            System.out.println("USERNAME NULLO O VUOTO");
            model.addAttribute("usernameExists", "Username non può essere vuoto");
            return "register-step2";
        }
        
        try {
            // Salva le credenziali
            Credentials savedCredentials = credentialsService.saveCredentials(credentials);
            System.out.println("Credentials saved successfully!");
            System.out.println("Saved credentials ID: " + savedCredentials.getId());
            System.out.println("Saved user ID: " + (savedCredentials.getUser() != null ? savedCredentials.getUser().getId() : "null"));
            
            // Verifica che sia stato salvato correttamente
            Credentials retrievedCredentials = credentialsService.getByUsername(credentials.getUsername());
            if (retrievedCredentials != null) {
                System.out.println("Verification: User can be found by username");
                System.out.println("Retrieved email: " + retrievedCredentials.getEmail());
                System.out.println("Retrieved role: " + retrievedCredentials.getRole());
            } else {
                System.out.println("ERROR: User NOT found by username after save!");
            }
            
        } catch (Exception e) {
            System.out.println("ERROR saving credentials: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Errore durante il salvataggio");
            return "register-step2";
        }
        
        // Pulisci la sessione
        sessionStatus.setComplete();
        
        // Reindirizza al login con messaggio di successo
        redirectAttributes.addFlashAttribute("registrationSuccess", "Registrazione completata con successo!");
        return "redirect:/login";
    }

    // Metodo per annullare la registrazione
    @GetMapping("/register/cancel")
    public String cancelRegistration(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "redirect:/";
    }

    // Login page
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "logout", required = false) String logout,
                               Model model) {
        
        if (error != null) {
            model.addAttribute("errorMessage", "Username o password non validi");
            System.out.println("Login error occurred");
        }
        
        if (logout != null) {
            model.addAttribute("logoutMessage", "Sei stato disconnesso con successo");
        }
        
        return "login";
    }
}
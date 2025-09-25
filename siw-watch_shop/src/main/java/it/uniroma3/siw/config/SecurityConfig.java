package it.uniroma3.siw.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import it.uniroma3.siw.oauth2.CustomOAuth2UserService;
import it.uniroma3.siw.oauth2.OAuth2AuthenticationSuccessHandler;
import it.uniroma3.siw.service.CredentialsService;

@Configuration
public class SecurityConfig {

    @Autowired
    private CredentialsService credentialsService;

    @Autowired(required = false)  // IMPORTANTE: required = false
    private CustomOAuth2UserService oAuth2UserService;

    @Autowired(required = false)  // IMPORTANTE: required = false
    private OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;
    
    @Autowired
    private CustomAuthenticationSuccessHandler customAuthSuccessHandler;

    @Autowired(required = false)  // IMPORTANTE: required = false
    private ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**","/", "/index", "/login", "/register", "/register/**", 
                               "/css/**", "/images/**", "/watches", 
                               "/watch/**", "/watch", "/watches/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(customAuthSuccessHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/").permitAll()
            )
            .csrf(csrf -> csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringRequestMatchers(
                        "/logout", "/oauth2/**", "/login/**", "/login/oauth2/**",
                        "/admin/**", "/register/**"
                    )
                );
        
        // Configura OAuth2 solo se disponibile
        if (clientRegistrationRepository != null && oAuth2UserService != null && oAuth2SuccessHandler != null) {
            http.oauth2Login(oauth -> oauth
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(oAuth2UserService)
                )
                .successHandler(oAuth2SuccessHandler)
            );
        }
        
        return http.build();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(credentialsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }
}
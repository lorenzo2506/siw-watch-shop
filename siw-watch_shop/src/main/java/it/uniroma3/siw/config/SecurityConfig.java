package it.uniroma3.siw.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import it.uniroma3.siw.oauth2.CustomOAuth2UserService;
import it.uniroma3.siw.oauth2.CustomOidcUserService;
import it.uniroma3.siw.oauth2.OAuth2AuthenticationSuccessHandler;
import it.uniroma3.siw.service.CredentialsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

	@Autowired
	private CredentialsService credentialsService;

	@Autowired
    private CustomOAuth2UserService oAuth2UserService;
    
    @Autowired
    private CustomOidcUserService oidcUserService;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("=== CONFIGURING SECURITY FILTER CHAIN ===");
        System.out.println("OAuth2UserService: " + oAuth2UserService);
        System.out.println("OidcUserService: " + oidcUserService);
        System.out.println("OAuth2SuccessHandler: " + oAuth2SuccessHandler);
        
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/index", "/login", "/register", "/register/**", "/css/**", "/images/**", "/watches").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .oauth2Login(oauth -> {
                System.out.println("=== CONFIGURING OAUTH2 LOGIN ===");
                oauth
                    .loginPage("/login")
                    .userInfoEndpoint(userInfo -> {
                        System.out.println("=== CONFIGURING USER INFO ENDPOINT ===");
                        System.out.println("Setting userService to: " + oAuth2UserService);
                        System.out.println("Setting oidcUserService to: " + oidcUserService);
                        userInfo
                            .userService(oAuth2UserService)           // Per OAuth2 normale
                            .oidcUserService(oidcUserService);        // Per OIDC (Google)
                    })
                    .successHandler(oAuth2SuccessHandler);
            })
            .logout(logout -> logout
                .logoutSuccessUrl("/").permitAll()
            );

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
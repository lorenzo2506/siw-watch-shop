package it.uniroma3.siw.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

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
    
    @Autowired
    private CustomAuthenticationSuccessHandler customAuthSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/index", "/login", "/register", "/register/**", 
                               "/css/**", "/images/**", "/watches", "/formNewWatch", 
                               "/watch/**", "/watch", "/watches/*").permitAll()
                .requestMatchers("/currentOrder/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(customAuthSuccessHandler)
                .permitAll()
            )
            .oauth2Login(oauth -> {
                oauth
                    .loginPage("/login")
                    .userInfoEndpoint(userInfo -> {
                        userInfo
                            .userService(oAuth2UserService)
                            .oidcUserService(oidcUserService);
                    })
                    .successHandler(oAuth2SuccessHandler);
            })
            .logout(logout -> logout
                .logoutSuccessUrl("/").permitAll()
            )
            // CSRF CONFIGURAZIONE CORRETTA
            .csrf(csrf -> csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringRequestMatchers(
                        "/logout",
                        "/oauth2/**",
                        "/login/**",
                        "/login/oauth2/**",
                        "/admin/**",
                        "/register/step2",
                        "/currentOrder/**",
                        "/watch/*/reviews",
                        "/watch/*/reviews/**"
                    )
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
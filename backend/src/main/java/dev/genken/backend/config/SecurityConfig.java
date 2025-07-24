package dev.genken.backend.config;

import dev.genken.backend.repository.UserRepository;
import dev.genken.backend.security.JwtAuthenticationFilter;
import dev.genken.backend.service.JwtVerificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http, JwtVerificationService verificationService, UserRepository userRepository
    ) throws Exception {
        http.authorizeHttpRequests(
            authz -> authz.anyRequest().permitAll()
        )
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(
                new JwtAuthenticationFilter(verificationService, userRepository),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}

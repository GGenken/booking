package dev.genken.backend.config;

import dev.genken.backend.exception.handler.GlobalExceptionHandler;
import dev.genken.backend.repository.UserRepository;
import dev.genken.backend.security.JwtAuthenticationFilter;
import dev.genken.backend.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        AuthService authService,
        GlobalExceptionHandler globalExceptionHandler
    ) throws Exception {
        http.authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(
                new JwtAuthenticationFilter(authService, globalExceptionHandler),
                UsernamePasswordAuthenticationFilter.class
            );
        return http.build();
    }
}

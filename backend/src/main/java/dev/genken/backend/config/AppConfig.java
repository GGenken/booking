package dev.genken.backend.config;

import dev.genken.backend.exception.handler.GlobalExceptionHandler;
import dev.genken.backend.repository.UserRepository;
import dev.genken.backend.service.AuthService;
import dev.genken.backend.security.JwtAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class AppConfig {
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter(AuthService authService, GlobalExceptionHandler globalExceptionHandler) {
        FilterRegistrationBean<JwtAuthenticationFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new JwtAuthenticationFilter(authService, globalExceptionHandler));

        reg.addUrlPatterns("/api/*");
        reg.setOrder(1);
        return reg;
    }
}

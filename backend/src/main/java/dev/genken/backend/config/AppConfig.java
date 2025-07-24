package dev.genken.backend.config;

import dev.genken.backend.repository.UserRepository;
import dev.genken.backend.service.JwtVerificationService;
import dev.genken.backend.security.JwtAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter(
        JwtVerificationService verificationService, UserRepository userRepository
    ) {
        FilterRegistrationBean<JwtAuthenticationFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new JwtAuthenticationFilter(verificationService, userRepository));
        reg.addUrlPatterns("/api/*");
        reg.setOrder(1);
        return reg;
    }
}

package dev.genken.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.genken.backend.dto.ProblemDetailsDto;
import dev.genken.backend.entity.User;
import dev.genken.backend.exception.handler.GlobalExceptionHandler;
import dev.genken.backend.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AuthService authService;
    private final GlobalExceptionHandler globalExceptionHandler;
    private final ObjectMapper objectMapper;


    public JwtAuthenticationFilter(AuthService authService, GlobalExceptionHandler globalExceptionHandler) {
        this.authService = authService;
        this.globalExceptionHandler = globalExceptionHandler;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        List<String> paths = Arrays.asList("/api/ping", "/api/auth/login", "/swagger-ui", "/v3/api-docs");
        for (var p : paths) {
            if (path.startsWith(p)) { return true; }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws IOException {
        try {  // TODO: ACHTUNG - HUGE CRUTCH
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (header == null) {
                throw new AuthenticationCredentialsNotFoundException("Authorization header is missing");
            }
            if (!header.startsWith("Bearer ")) {
                throw new BadCredentialsException("Authorization header does not start with 'Bearer '");
            }

            String token = header.substring(7);
            Map<String, Object> claims = authService.getClaimsFromJwt(token);

            UUID uuid = UUID.fromString(claims.get("uuid").toString());
            try {
                User user = authService.findByUuid(uuid);
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    user,
                    token,
                    List.of(authority)
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (NoSuchElementException e) {
                throw new BadCredentialsException("User with UUID provided in the token not found");
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            ResponseEntity<ProblemDetailsDto> problem = globalExceptionHandler.handleException(e, request);
            response.setStatus(problem.getStatusCode().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(problem.getBody()));
        }
    }
}

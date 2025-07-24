package dev.genken.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.genken.backend.exception.JwtVerificationException;
import dev.genken.backend.service.JwtVerificationService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.text.ParseException;
import java.util.UUID;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtVerificationService verificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(JwtVerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            verificationService.verifyToken(token);

            Map<String, Object> claims;
            try {
                SignedJWT jwt = SignedJWT.parse(token);
                JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();
                claims = claimsSet.getClaims();
            } catch (ParseException e) {
                throw new BadCredentialsException("Invalid JWT format", e);
            }

            String username = (String) claims.getOrDefault("username", "anonymous");
            String role = (String) claims.getOrDefault("role", "guest");

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, List.of(authority));

            auth.setDetails(claims);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } else {
            SimpleGrantedAuthority guestAuth = new SimpleGrantedAuthority("guest");
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("anonymous", null, List.of(guestAuth));
            auth.setDetails(Map.of("username", "anonymous", "role", "guest"));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}

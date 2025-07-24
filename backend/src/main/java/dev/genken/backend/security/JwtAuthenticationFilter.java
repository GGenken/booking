package dev.genken.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.genken.backend.entity.Role;
import dev.genken.backend.entity.User;
import dev.genken.backend.exception.JwtVerificationException;
import dev.genken.backend.repository.UserRepository;
import dev.genken.backend.service.JwtVerificationService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.io.IOException;
import java.util.*;
import java.text.ParseException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtVerificationService verificationService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(JwtVerificationService verificationService, UserRepository userRepository) {
        this.verificationService = verificationService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        Optional<User> user;
        String token = "";
        SimpleGrantedAuthority authority;
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            verificationService.verifyToken(token);

            Map<String, Object> claims;
            try {
                SignedJWT jwt = SignedJWT.parse(token);
                JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();
                claims = claimsSet.getClaims();
            } catch (ParseException e) {
                throw new BadCredentialsException("Invalid JWT format", e);
            }

            UUID uuid = UUID.fromString((String) claims.get("uuid"));
            user = userRepository.findByUuid(uuid);
            authority = new SimpleGrantedAuthority(Role.USER.getAuthority());
        } else {
            user = Optional.empty();
            authority = new SimpleGrantedAuthority(Role.GUEST.getAuthority());
        }
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            user, token, List.of(authority)
        );

        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }
}

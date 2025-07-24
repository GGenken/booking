package dev.genken.backend.security;

import dev.genken.backend.entity.Role;
import dev.genken.backend.entity.User;
import dev.genken.backend.repository.UserRepository;
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
import java.util.*;
import java.text.ParseException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtVerificationService verificationService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtVerificationService verificationService, UserRepository userRepository) {
        this.verificationService = verificationService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        Optional<User> user = Optional.empty();
        String token = "";
        Map<String, Object> claims;
        SimpleGrantedAuthority authority;
        UsernamePasswordAuthenticationToken auth;
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            verificationService.verifyToken(token);
            try {
                SignedJWT jwt = SignedJWT.parse(token);
                JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();
                claims = claimsSet.getClaims();
            } catch (ParseException e) {
                throw new BadCredentialsException("Invalid JWT format", e);
            }

            UUID uuid = UUID.fromString((String) claims.get("uuid"));
            user = Optional.ofNullable(userRepository.findByUuid(uuid).orElseThrow(() -> new NoSuchElementException("User not found with UUID: " + uuid)));;
            authority = new SimpleGrantedAuthority(user.map(value -> value.getRole().name()).orElseGet(Role.GUEST::name));
        } else {
            authority = new SimpleGrantedAuthority(Role.GUEST.getAuthority());
        }
        auth = new UsernamePasswordAuthenticationToken(
            user.isPresent() ? user.get() : "guest", token, List.of(authority)
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }
}

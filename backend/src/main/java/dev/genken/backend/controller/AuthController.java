package dev.genken.backend.controller;

import dev.genken.backend.dto.UserRequestDto;
import dev.genken.backend.entity.Role;
import dev.genken.backend.entity.User;
import jakarta.validation.Valid;

import java.util.NoSuchElementException;
import java.util.UUID;

import dev.genken.backend.dto.AuthResponseDto;
import dev.genken.backend.dto.UserResponseDto;
import dev.genken.backend.service.AuthService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserRequestDto dto) {
        try {
            AuthResponseDto response = authService.login(dto);
            return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + response.getToken())
                .build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRequestDto dto, Authentication authentication) {
        try {
            String token = authentication.getCredentials().toString();
            UserResponseDto user = authService.register(dto, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/users/{uuid}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID uuid, Authentication authentication) {
        try {
            String token = authentication.getCredentials().toString();
            authService.deleteUser(uuid, token);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@AuthenticationPrincipal User user) {
        if (user == null || user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(authService.getAllUsers());
    }
}

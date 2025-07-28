package dev.genken.backend.controller;

import dev.genken.backend.dto.ProblemDetailsDto;
import dev.genken.backend.dto.UserRequestDto;
import dev.genken.backend.entity.Role;
import dev.genken.backend.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import dev.genken.backend.dto.AuthResponseDto;
import dev.genken.backend.service.AuthService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Authentication")
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    @Operation(summary = "Login and obtain the JWT token")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Login successful, token generated"),
            @ApiResponse(
                responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = ProblemDetailsDto.class))
            ),
            @ApiResponse(

                responseCode = "500", description = "Internal Server Error",
                content = @Content(schema = @Schema(implementation = ProblemDetailsDto.class))
            )
        }
    )
    public ResponseEntity<Void> login(@Valid @RequestBody UserRequestDto dto) {
        AuthResponseDto response = authService.login(dto);
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + response.getToken()).build();
    }

    @PostMapping("/users")
    @Operation(summary = "Create a new user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(
                responseCode = "403", description = "Forbidden - administrator privileges required",
                content = @Content(schema = @Schema(implementation = ProblemDetailsDto.class))
            ),
            @ApiResponse(
                responseCode = "500", description = "Internal Server Error",
                content = @Content(schema = @Schema(implementation = ProblemDetailsDto.class))
            )
        }
    )
    public ResponseEntity<User> register(@Valid @RequestBody UserRequestDto dto, @Parameter(hidden = true) Authentication authentication) {
        String token = authentication.getCredentials().toString();
        User user = authService.register(dto, token);
        return ResponseEntity.created(URI.create("/api/auth/users/" + user.getUuid().toString())).body(user);
    }

    @DeleteMapping("/users/{uuid}")
    @Operation(summary = "Delete a user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(
            responseCode = "404", description = "User not found",
            content = @Content(schema = @Schema(implementation = ProblemDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "403", description = "Forbidden - administrator privileges required",
            content = @Content(schema = @Schema(implementation = ProblemDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "500", description = "Internal Server Error",
            content = @Content(schema = @Schema(implementation = ProblemDetailsDto.class))
        )
    })
    public ResponseEntity<Void> deleteUser(@PathVariable UUID uuid, @Parameter(hidden = true) Authentication authentication) {
        String token = authentication.getCredentials().toString();
        authService.deleteUser(uuid, token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
        @ApiResponse(
            responseCode = "403", description = "Forbidden - admin privileges required",
            content = @Content(schema = @Schema(implementation = ProblemDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "500", description = "Internal Server Error",
            content = @Content(schema = @Schema(implementation = ProblemDetailsDto.class))
        )
    })
    public ResponseEntity<List<User>> getUsers(@AuthenticationPrincipal User user) {
        if (user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Administrator privileges required");
        }
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @Operation(summary = "Get your ID, UUID, role and username", description = "Get the authenticated user's details if valid", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "Successfully verified the user"
        ),
        @ApiResponse(
            responseCode = "401", description = "Unauthorized - User is not authenticated or invalid token",
            content = @Content(schema = @Schema(implementation = ProblemDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "500", description = "Internal Server Error",
            content = @Content(schema = @Schema(implementation = ProblemDetailsDto.class))
        )
    })
    @GetMapping("/whoami")
    public ResponseEntity<User> verify(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(user);
    }
}
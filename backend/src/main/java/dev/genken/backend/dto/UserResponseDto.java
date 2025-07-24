package dev.genken.backend.dto;

import dev.genken.backend.entity.Role;

import java.util.UUID;

public class UserResponseDto {
    private UUID uuid;
    private Role role;
    private String username;

    public UUID getUuid() { return uuid; }

    public void setUuid(String uuid) { this.uuid = UUID.fromString(uuid); }

    public Role getRole() { return role; }

    public void setRole(String role) { this.role = Role.valueOf(role.toUpperCase()); }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }
}

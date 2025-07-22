package dev.genken.backend.dto;

import java.util.UUID;

public class UserResponseDto {
    private UUID uuid;
    private String role;
    private String username;

    public UUID getUuid() { return uuid; }

    public void setUuid(String uuid) { this.uuid = UUID.fromString(uuid); }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }
}

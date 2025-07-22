package dev.genken.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRequestDto {
    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 16, message = "Username must be from 2 to 16 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9]+$",
        message = "Username must be alphanumeric"
    )
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 4, max = 128, message = "Password must be from 4 to 128 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9!@#$%^&*()_+=]+$",
        message = "Password must be alphanumeric with \"!@#$%^&*()_+=\""
    )
    private String password;

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }
}

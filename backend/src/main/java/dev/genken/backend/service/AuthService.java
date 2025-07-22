package dev.genken.backend.service;

import dev.genken.backend.dto.AuthResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import dev.genken.backend.dto.UserRequestDto;
import dev.genken.backend.dto.UserResponseDto;
import dev.genken.backend.entity.User;
import dev.genken.backend.repository.UserRepository;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AuthService {
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    public AuthService(RestTemplate restTemplate, UserRepository userRepository) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
    }

    public UserResponseDto register(UserRequestDto request) {
        // TODO: check for a server's response code
        UserResponseDto response = restTemplate
            .postForObject(authServiceUrl + "/register", request, UserResponseDto.class);

        try {
            saveUser(response.getUsername(), response.getUuid(), response.getRole());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("User already exists");
        }
        return response;
    }

    public AuthResponseDto login(UserRequestDto request) {
        String url = authServiceUrl + "/login";
        try {
            AuthResponseDto response = restTemplate.postForObject(url, request, AuthResponseDto.class);
            return response;
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new SecurityException("Invalid credentials");
        } catch (HttpStatusCodeException e) {
            throw new IllegalStateException(
                "Auth service error: " + e.getStatusCode() + " → " + e.getResponseBodyAsString()
            );
        }
    }

    private void saveUser(String username, UUID uuid, String role) {
        User user = new User();
        user.setUsername(username);
        user.setUuid(uuid);
        user.setRole(role);
        userRepository.save(user);
    }

    private void saveOrUpdateUser(String username, UUID uuid, String role) {
        User user = userRepository.findByUuid(uuid)
            .orElseGet(() -> {
                User u = new User();
                u.setUsername(username);
                return u;
            });
        user.setUuid(uuid);
        user.setRole(role);
        userRepository.save(user);
    }

    public void deleteUser(UUID uuid) {
        String url = this.authServiceUrl + "/users/" + uuid;
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new NoSuchElementException("User not found in auth service");
        } catch (HttpStatusCodeException e) {
            throw new IllegalStateException("Auth service error: " + e.getMessage());
        }
    }
}

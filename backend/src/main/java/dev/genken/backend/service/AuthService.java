package dev.genken.backend.service;

import dev.genken.backend.dto.AuthResponseDto;
import dev.genken.backend.entity.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import dev.genken.backend.dto.UserRequestDto;
import dev.genken.backend.dto.UserResponseDto;
import dev.genken.backend.entity.User;
import dev.genken.backend.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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

    public UserResponseDto register(UserRequestDto request, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", token);
        HttpEntity<UserRequestDto> request_params = new HttpEntity<>(request, headers);
        UserResponseDto response = restTemplate.postForEntity(authServiceUrl + "/register", request_params, UserResponseDto.class).getBody();
        // TODO: check for a server's response code
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
            UserResponseDto user_parsed = response.getUser();
            saveOrUpdateUser(user_parsed.getUsername(), user_parsed.getUuid(), user_parsed.getRole());
            return response;
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new SecurityException("Invalid credentials");
        } catch (HttpStatusCodeException e) {
            throw new IllegalStateException("Auth service error");
        }
    }

    private void saveUser(String username, UUID uuid, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setUuid(uuid);
        user.setRole(role);
        userRepository.save(user);
    }

    private void saveOrUpdateUser(String username, UUID uuid, Role role) {
        User user = userRepository.findByUuid(uuid).orElseGet(() -> {
            User u = new User();
            u.setUsername(username);
            return u;
        });
        user.setUuid(uuid);
        user.setRole(role);
        userRepository.save(user);
    }

    public void deleteUser(UUID uuid, String token) {
        String url = authServiceUrl + "/users/" + uuid;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", token);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);
            Optional<User> userToDelete = userRepository.findByUuid(UUID.fromString(uuid.toString()));
            userToDelete.ifPresent(userRepository::delete);
        } catch (HttpClientErrorException.NotFound e) {
            throw new NoSuchElementException("User not found in auth service");
        } catch (HttpStatusCodeException e) {
            throw new IllegalStateException("Auth service error: " + e.getMessage());
        }
    }

    public List<User> getAllUsers() { return userRepository.findAll(); }
}

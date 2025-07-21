package dev.genken.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import dev.genken.backend.dto.AuthRequestDto;
import dev.genken.backend.dto.AuthResponseDto;
import dev.genken.backend.entity.User;
import dev.genken.backend.repository.UserRepository;

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

    public AuthResponseDto register(AuthRequestDto request) {
        // TODO: check for a server's response code
        AuthResponseDto response = restTemplate
            .postForObject(authServiceUrl + "/register", request, AuthResponseDto.class);

        try {
            saveUser(response.getUsername(), response.getUuid(), response.getRole());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("User already exists");
        }
        return response;
    }

    public AuthResponseDto login(AuthRequestDto request) {
        AuthResponseDto response = restTemplate
            .postForObject(authServiceUrl + "/login", request, AuthResponseDto.class);
        saveOrUpdateUser(response.getUsername(), response.getUuid(), response.getRole());
        return response;
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
}

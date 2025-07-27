package dev.genken.backend.service;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ExpiredJWTException;
import dev.genken.backend.dto.AuthResponseDto;
import dev.genken.backend.entity.Role;
import dev.genken.backend.exception.AuthServiceException;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import dev.genken.backend.dto.UserRequestDto;
import dev.genken.backend.dto.UserResponseDto;
import dev.genken.backend.entity.User;
import dev.genken.backend.repository.UserRepository;

import java.text.ParseException;
import java.util.*;

@Service
public class AuthService {
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    public AuthService(RestTemplate authRestTemplate, UserRepository userRepository) {
        this.restTemplate = authRestTemplate;
        this.userRepository = userRepository;
    }

    public User register(UserRequestDto request, String token) {
        var headers = new HttpHeaders();
        headers.set("X-Auth-Token", token);
        HttpEntity<UserRequestDto> restRequest = new HttpEntity<>(request, headers);

        try {
            UserResponseDto response = restTemplate.postForEntity("/register", restRequest, UserResponseDto.class).getBody();
            return saveUser(response);
        } catch (HttpStatusCodeException e) {
            throw AuthServiceException.fromHttpException(e);
        }
    }

    private User saveUser(String username, UUID uuid, Role role) {
        User user = new User(username, uuid, role);
        userRepository.save(user);
        return user;
    }

    private User saveUser(UserResponseDto response) {
        return saveUser(response.getUsername(), response.getUuid(), response.getRole());
    }

    public AuthResponseDto login(UserRequestDto request) {
        try {
            AuthResponseDto response = restTemplate.postForObject("/login", request, AuthResponseDto.class);
            saveOrUpdateUser(response);
            return response;
        } catch (HttpClientErrorException e) {
            throw AuthServiceException.fromHttpException(e);
        }
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

    private void saveOrUpdateUser(AuthResponseDto response) {
        saveOrUpdateUser(response.getUser());
    }

    private void saveOrUpdateUser(UserResponseDto response) {
        saveOrUpdateUser(response.getUsername(), response.getUuid(), response.getRole());
    }

    public void deleteUser(UUID uuid, String token) {
        var headers = new HttpHeaders();
        headers.set("X-Auth-Token", token);
        var restRequest = new HttpEntity<>(headers);
        try {
            restTemplate.exchange("/users/" + uuid, HttpMethod.DELETE, restRequest, Void.class);
            Optional<User> userToDelete = userRepository.findByUuid(uuid);
            userToDelete.ifPresent(userRepository::delete);
        } catch (HttpClientErrorException e) {
            throw AuthServiceException.fromHttpException(e);
        }
    }

    public List<User> getAllUsers() { return userRepository.findAll(); }

    public Map<String, Object> getClaimsFromJwt(String token) {
        var headers = new HttpHeaders();
        headers.set("X-Auth-Token", token);
        var restRequest = new HttpEntity<>(headers);

        try {
            SignedJWT jwt = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();

            Date expirationDate = claimsSet.getExpirationTime();
            if (expirationDate != null && expirationDate.before(new Date())) {
                throw new ExpiredJWTException("JWT token has expired");
            }

            Map<String, Object> claims = claimsSet.getClaims();

            restTemplate.postForEntity(
                "/verify-jwt",
                restRequest,
                Void.class
            );

            return claims;
        } catch (ParseException e) {
            throw new BadCredentialsException("Unparseable token", e);
        } catch (HttpStatusCodeException e) {
            throw AuthServiceException.fromHttpException(e);
        } catch (ExpiredJWTException e) {
            throw new BadCredentialsException("JWT token has expired", e);
        }
    }

    public User findByUuid(UUID uuid) {
        return userRepository.findByUuid(uuid).orElseThrow(() -> new NoSuchElementException("User not found with UUID: " + uuid));
    }
}

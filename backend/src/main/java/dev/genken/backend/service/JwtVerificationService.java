package dev.genken.backend.service;

import dev.genken.backend.exception.JwtVerificationException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class JwtVerificationService {
    private final RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    public JwtVerificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void verifyToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                authServiceUrl + "/verify-jwt",
                HttpMethod.POST,
                request,
                Void.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
            } else {
                throw new JwtVerificationException("Unexpected status: " + response.getStatusCode());
            }
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new BadCredentialsException("Invalid JWT token", e);
            }
            throw new JwtVerificationException("Verification failed: " + e.getStatusCode(), e);
        } catch (Exception e) {
            throw new JwtVerificationException("Error calling verification service", e);
        }
    }
}

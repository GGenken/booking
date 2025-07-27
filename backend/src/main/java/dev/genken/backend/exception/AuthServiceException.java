package dev.genken.backend.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.client.HttpStatusCodeException;

public class AuthServiceException extends ResponseStatusException {
    public AuthServiceException(HttpStatusCode status, String reason) {
        super(status, reason);
    }

    public static AuthServiceException fromHttpException(HttpStatusCodeException e) {
        String responseBody = e.getResponseBodyAsString();
        String detail;
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            if (root.has("error")) {
                detail = root.get("error").asText();
            } else {
                detail = responseBody;
            }
        } catch (JsonProcessingException parseEx) {
            detail = responseBody;
        }

        return new AuthServiceException(e.getStatusCode(), detail);
    }
}
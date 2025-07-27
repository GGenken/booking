package dev.genken.backend.exception.handler;

import dev.genken.backend.dto.ProblemDetailsDto;
import dev.genken.backend.exception.AuthServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class AuthServiceExceptionHandler {
    @ExceptionHandler(AuthServiceException.class)
    public ResponseEntity<ProblemDetailsDto> handleAuthServiceException(AuthServiceException e, HttpServletRequest r) {
        var problem = new ProblemDetailsDto(
            "Auth service error",
            e.getStatusCode().value(),
            e.getMessage(),
            r.getRequestURI()
        );
        return ResponseEntity.status(e.getStatusCode().value()).body(problem);
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ProblemDetailsDto> handleHttpStatusCodeException(HttpStatusCodeException e,  HttpServletRequest r) {
        return handleAuthServiceException(AuthServiceException.fromHttpException(e), r);
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ProblemDetailsDto> handleTimeoutException(TimeoutException e, HttpServletRequest r) {
        var problem = new ProblemDetailsDto(
            "Service Timeout",
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            "The authentication service is down",
            r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(problem);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ProblemDetailsDto> handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException e, HttpServletRequest r) {
        var problem = new ProblemDetailsDto(
            "JWT token missing",
            HttpStatus.UNAUTHORIZED.value(),
            "The token must be supplied in format 'Auth: Bearer JWT_token'",
            r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetailsDto> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest r) {
        var problem = new ProblemDetailsDto(
            "JWT token is malformed or expired",
            HttpStatus.BAD_REQUEST.value(),
            e.getMessage(),
            r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetailsDto> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest r) {
        var problem = new ProblemDetailsDto(
            "Insufficient privileges",
            HttpStatus.FORBIDDEN.value(),
            e.getMessage(),
            r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
    }
}

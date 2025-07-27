package dev.genken.backend.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import dev.genken.backend.dto.ProblemDetailsDto;
import dev.genken.backend.exception.AuthServiceException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

// huge and gross crutch, but filter can't autowire exception handlers and I don't have time to rework the handlers

@ControllerAdvice
public class GlobalExceptionHandler {
    private final AuthServiceExceptionHandler authServiceExceptionHandler;
    private final JsonExceptionHandler jsonExceptionHandler;
    private final PersistenceExceptionHandler persistenceExceptionHandler;
    private final RequestValidationExceptionHandler requestValidationExceptionHandler;
    private final UnexpectedExceptionHandler unexpectedExceptionHandler;

    public GlobalExceptionHandler(
        AuthServiceExceptionHandler authServiceExceptionHandler,
        JsonExceptionHandler jsonExceptionHandler,
        PersistenceExceptionHandler persistenceExceptionHandler,
        RequestValidationExceptionHandler requestValidationExceptionHandler,
        UnexpectedExceptionHandler unexpectedExceptionHandler
    ) {
        this.authServiceExceptionHandler = authServiceExceptionHandler;
        this.jsonExceptionHandler = jsonExceptionHandler;
        this.persistenceExceptionHandler = persistenceExceptionHandler;
        this.requestValidationExceptionHandler = requestValidationExceptionHandler;
        this.unexpectedExceptionHandler = unexpectedExceptionHandler;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetailsDto> handleException(Exception e, HttpServletRequest r) {
        if (e instanceof AuthServiceException) {
            return authServiceExceptionHandler.handleAuthServiceException((AuthServiceException) e, r);
        }
        if (e instanceof HttpStatusCodeException) {
            return authServiceExceptionHandler.handleHttpStatusCodeException((HttpStatusCodeException) e, r);
        }
        if (e instanceof TimeoutException) {
            return authServiceExceptionHandler.handleTimeoutException((TimeoutException) e, r);
        }
        if (e instanceof AuthenticationCredentialsNotFoundException) {
            return authServiceExceptionHandler.handleAuthenticationCredentialsNotFoundException((AuthenticationCredentialsNotFoundException) e, r);
        }
        if (e instanceof BadCredentialsException) {
            return authServiceExceptionHandler.handleBadCredentialsException((BadCredentialsException) e, r);
        }
        if (e instanceof AccessDeniedException) {
            return authServiceExceptionHandler.handleAccessDeniedException((AccessDeniedException) e, r);
        }


        if (e instanceof HttpMessageNotReadableException) {
            return jsonExceptionHandler.handleMalformedJson((HttpMessageNotReadableException) e, r);
        }
        if (e instanceof UnrecognizedPropertyException) {
            return jsonExceptionHandler.handleUnknownField((UnrecognizedPropertyException) e, r);
        }
        if (e instanceof MismatchedInputException) {
            return jsonExceptionHandler.handleTypeMismatch((MismatchedInputException) e, r);
        }
        if (e instanceof JsonProcessingException) {
            return jsonExceptionHandler.handleJsonProcessingException((JsonProcessingException) e, r);
        }
        if (e instanceof MethodArgumentNotValidException) {
            return jsonExceptionHandler.handleValidationErrors((MethodArgumentNotValidException) e, r);
        }


        if (e instanceof MissingServletRequestParameterException) {
            return requestValidationExceptionHandler.handleMissingParameter((MissingServletRequestParameterException) e, r);
        }
        if (e instanceof ConstraintViolationException) {
            return requestValidationExceptionHandler.handleConstraintViolations((ConstraintViolationException) e, r);
        }
        if (e instanceof HttpMediaTypeNotSupportedException) {
            return requestValidationExceptionHandler.handleUnsupportedContentType((HttpMediaTypeNotSupportedException) e, r);
        }


        if (e instanceof EmptyResultDataAccessException) {
            return persistenceExceptionHandler.handleDeleteMissing((EmptyResultDataAccessException) e, r);
        }
        if (e instanceof JpaObjectRetrievalFailureException) {
            return persistenceExceptionHandler.handleMissingReference((JpaObjectRetrievalFailureException) e, r);
        }
        if (e instanceof DataIntegrityViolationException) {
            return persistenceExceptionHandler.handleIntegrityViolationException((DataIntegrityViolationException) e, r);
        }
        if (e instanceof NoSuchElementException) {
            return persistenceExceptionHandler.handleNoSuchElementException((NoSuchElementException) e, r);
        }


        if (e instanceof IllegalArgumentException) {
            return unexpectedExceptionHandler.handleIllegalArgumentException((IllegalArgumentException) e, r);
        }

        return unexpectedExceptionHandler.handleUnexpectedError(e, r);
    }
}


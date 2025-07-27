package dev.genken.backend.exception.handler;

import dev.genken.backend.dto.ProblemDetailsDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

@RestControllerAdvice
public class RequestValidationExceptionHandler {
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ProblemDetailsDto> handleMissingParameter(MissingServletRequestParameterException e, HttpServletRequest r) {
        var param = e.getParameterName();
        var message = String.format("Missing required parameter '%s'.", param);

        var problem = new ProblemDetailsDto(
            "Missing request parameter",
            HttpStatus.BAD_REQUEST.value(),
            message,
            r.getRequestURI(),
            Map.of(param, "Required parameter")
        );
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetailsDto> handleConstraintViolations(ConstraintViolationException e, HttpServletRequest r) {
        var errors = new HashMap<String, String>();
        for (var v : e.getConstraintViolations()) {
            var path = v.getPropertyPath().toString();
            var field = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
            errors.putIfAbsent(field, v.getMessage());
        }

        var problem = new ProblemDetailsDto(
            "Constraint violation",
            HttpStatus.BAD_REQUEST.value(),
            "One or more parameters are invalid",
            r.getRequestURI(),
            errors
        );
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ProblemDetailsDto> handleUnsupportedContentType(HttpMediaTypeNotSupportedException e, HttpServletRequest r) {
        var contentType = e.getContentType();
        var joiner = new StringJoiner(", ");
        for (var mediaType : e.getSupportedMediaTypes()) {
            joiner.add(mediaType.toString());
        }
        var supported = joiner.toString();

        var message = String.format(
            "Content-Type '%s' is not supported; supported types: %s.",
            contentType != null ? contentType : "unknown", supported
        );
        var problem = new ProblemDetailsDto(
            "Unsupported content type",
            HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
            message,
            r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(problem);
    }

}

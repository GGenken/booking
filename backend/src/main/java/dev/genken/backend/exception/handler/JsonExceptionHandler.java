package dev.genken.backend.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import dev.genken.backend.dto.ProblemDetailsDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class JsonExceptionHandler {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetailsDto> handleMalformedJson(HttpMessageNotReadableException e, HttpServletRequest r) {
        var problem = new ProblemDetailsDto(
            "Malformed JSON",
            HttpStatus.BAD_REQUEST.value(),
            e.getMessage(),
            r.getRequestURI()
        );
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<ProblemDetailsDto> handleUnknownField(UnrecognizedPropertyException e, HttpServletRequest r) {
        String field = e.getPropertyName();

        String message = String.format("Unrecognized property '%s'", field);
        var problem = new ProblemDetailsDto(
            "Unrecognized property in JSON",
            HttpStatus.BAD_REQUEST.value(),
            message,
            r.getRequestURI(),
            Map.of(field, "Unrecognized property")
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(MismatchedInputException.class)
    public ResponseEntity<ProblemDetailsDto> handleTypeMismatch(MismatchedInputException e, HttpServletRequest r) {
        StringBuilder fieldPath = new StringBuilder();
        if (e.getPath() != null) {
            for (JsonMappingException.Reference ref : e.getPath()) {
                fieldPath.append(ref.getFieldName()).append(".");
            }
        }

        String field = !fieldPath.isEmpty() ? fieldPath.substring(0, fieldPath.length() - 1) : "unknown";
        String expectedType = e.getTargetType() != null ? e.getTargetType().getSimpleName() : "unknown";

        String message = String.format("Invalid type for field '%s'; expected: %s", field, expectedType);
        var problem = new ProblemDetailsDto(
            "Type mismatch in JSON",
            HttpStatus.BAD_REQUEST.value(),
            message,
            r.getRequestURI(),
            Map.of(field, expectedType)
        );
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ProblemDetailsDto> handleJsonProcessingException(JsonProcessingException e, HttpServletRequest r) {
        var problem = new ProblemDetailsDto(
            "JSON Processing Error",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            e.getMessage(),
            r.getRequestURI()
        );
        return ResponseEntity.internalServerError().body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetailsDto> handleValidationErrors(MethodArgumentNotValidException e,HttpServletRequest r) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(
            err -> errors.put(err.getField(), err.getDefaultMessage())
        );

        var problem = new ProblemDetailsDto(
            "Validation failed",
            HttpStatus.BAD_REQUEST.value(),
            "Some fields are invalid",
            r.getRequestURI(),
            errors
        );
        return ResponseEntity.badRequest().body(problem);
    }
}

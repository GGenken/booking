package dev.genken.backend.exception.handler;

import dev.genken.backend.dto.ProblemDetailsDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class PersistenceExceptionHandler {
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ProblemDetailsDto> handleDeleteMissing(EmptyResultDataAccessException e, HttpServletRequest r) {
        var problem = new ProblemDetailsDto(
            "Resource requested for deletion could not be found",
            HttpStatus.NOT_FOUND.value(),
            e.getMessage(),
            r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(JpaObjectRetrievalFailureException.class)
    public ResponseEntity<ProblemDetailsDto> handleMissingReference(JpaObjectRetrievalFailureException e, HttpServletRequest r) {
        var problem = new ProblemDetailsDto(
            "Referenced entity was not found",
            HttpStatus.BAD_REQUEST.value(),
            e.getMessage(),
            r.getRequestURI()
        );
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetailsDto> handleIntegrityViolationException(DataIntegrityViolationException e, HttpServletRequest r) {
        var msg = e.getMostSpecificCause().getMessage();
        if (msg == null) throw e;
        var status = msg.toLowerCase().contains("duplicate key") ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;

        var problem = new ProblemDetailsDto(
            "Data integrity violation",
            status.value(),
            e.getMessage(),
            r.getRequestURI()
        );

        return ResponseEntity.status(status).body(problem);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetailsDto> handleNoSuchElementException(NoSuchElementException e, HttpServletRequest r) {
        var problem = new ProblemDetailsDto(
            "Resource not found",
            HttpStatus.NOT_FOUND.value(),
            e.getMessage(),
            r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

}

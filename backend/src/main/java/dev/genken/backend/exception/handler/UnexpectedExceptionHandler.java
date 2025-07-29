package dev.genken.backend.exception.handler;

import dev.genken.backend.dto.ProblemDetailsDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class UnexpectedExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetailsDto> handleUnexpectedError(Exception e, HttpServletRequest r) {
        e.printStackTrace();  // TODO

        var problem = new ProblemDetailsDto(
            "Internal server error",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred",
            r.getRequestURI()
        );
        return ResponseEntity.internalServerError().body(problem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetailsDto> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest r) {
        var problem = new ProblemDetailsDto(
            "Invalid input",
            HttpStatus.BAD_REQUEST.value(),
            e.getMessage() != null ? e.getMessage() : "Illegal argument provided",
            r.getRequestURI()
        );
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetailsDto> handleNoResourceFoundException(NoResourceFoundException e, HttpServletRequest r) {
        var problem = new ProblemDetailsDto(
            "Not found",
            HttpStatus.NOT_FOUND.value(),
            e.getMessage() != null ? e.getMessage() : "No resource found",
            r.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }
}

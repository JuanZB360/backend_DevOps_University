package com.devopsuniversity.shared.web;

import com.devopsuniversity.auth.domain.exception.InvalidCredentialsException;
import com.devopsuniversity.auth.domain.exception.InvalidCredentialsFormatException;
import com.devopsuniversity.auth.domain.exception.UnauthenticatedException;
import com.devopsuniversity.auth.domain.exception.UsernameTakenException;
import com.devopsuniversity.player.domain.exception.InvalidPlayerNameException;
import com.devopsuniversity.player.domain.exception.PlayerNotFoundException;
import com.devopsuniversity.player.domain.exception.StaleProgressException;
import com.devopsuniversity.shared.domain.DomainException;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce excepciones a respuestas de error estándar (RFC 9457, Problem Details).
 *
 * <p>Todas las respuestas de error de la API tienen la misma forma:
 * {@code { type, title, status, detail, code, ... }}.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Estado HTTP de cada excepción de dominio. Añade aquí las nuevas. */
    private static final Map<Class<? extends DomainException>, HttpStatus> STATUS_BY_EXCEPTION = Map.of(
            PlayerNotFoundException.class, HttpStatus.NOT_FOUND,
            InvalidPlayerNameException.class, HttpStatus.BAD_REQUEST,
            StaleProgressException.class, HttpStatus.CONFLICT,
            InvalidCredentialsFormatException.class, HttpStatus.BAD_REQUEST,
            InvalidCredentialsException.class, HttpStatus.UNAUTHORIZED,
            UnauthenticatedException.class, HttpStatus.UNAUTHORIZED,
            UsernameTakenException.class, HttpStatus.CONFLICT);

    @ExceptionHandler(DomainException.class)
    ProblemDetail handleDomain(DomainException ex) {
        HttpStatus status = STATUS_BY_EXCEPTION.getOrDefault(ex.getClass(), HttpStatus.UNPROCESSABLE_CONTENT);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problem.setProperty("code", ex.getCode());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(e -> e.getField(), e -> String.valueOf(e.getDefaultMessage()), (a, b) -> a));
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "La petición contiene datos inválidos");
        problem.setProperty("code", "VALIDATION_ERROR");
        problem.setProperty("errors", errors);
        return problem;
    }

    /**
     * Restricción de la base de datos violada. El caso típico es que dos
     * personas registren el mismo usuario a la vez: la comprobación previa
     * pasa para ambas y la restricción UNIQUE frena a la segunda.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handleConflict(DataIntegrityViolationException ex) {
        log.warn("Conflicto de integridad: {}", ex.getMostSpecificCause().getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "El recurso ya existe o entra en conflicto con otro. Inténtalo de nuevo");
        problem.setProperty("code", "CONFLICT");
        return problem;
    }

    /** Ids mal formados u otros argumentos inválidos. */
    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setProperty("code", "BAD_REQUEST");
        return problem;
    }

    /** Último recurso: nunca exponemos detalles internos al cliente. */
    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception ex) {
        log.error("Error no controlado", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
        problem.setProperty("code", "INTERNAL_ERROR");
        return problem;
    }
}

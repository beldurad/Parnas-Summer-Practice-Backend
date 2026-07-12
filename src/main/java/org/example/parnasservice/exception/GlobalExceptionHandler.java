package org.example.parnasservice.exception;

import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.example.parnasservice.dto.response.ProblemDetails;
import org.example.parnasservice.dto.response.ProblemDetails.FieldViolation;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetails> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        List<FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
            .map(err -> new FieldViolation(err.getField(), err.getDefaultMessage(), err.getRejectedValue()))
            .collect(Collectors.toList());
        ProblemDetails problem = createProblem(422, "VALIDATION_ERROR", "Ошибка валидации",
            "Одно или несколько полей содержат недопустимые значения.", request);
        problem.setViolations(violations);
        log.warn("Validation failed: code={} violations={}", problem.getCode(), violations.size());
        return ResponseEntity.status(422).body(problem);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetails> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        List<FieldViolation> violations = ex.getConstraintViolations().stream()
            .map(v -> new FieldViolation(v.getPropertyPath().toString(), v.getMessage(), v.getInvalidValue()))
            .collect(Collectors.toList());
        ProblemDetails problem = createProblem(422, "VALIDATION_ERROR", "Ошибка валидации",
            "Одно или несколько полей содержат недопустимые значения.", request);
        problem.setViolations(violations);
        log.warn("Constraint validation failed: code={} violations={}", problem.getCode(), violations.size());
        return ResponseEntity.status(422).body(problem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetails> handleBadArgument(IllegalArgumentException ex, WebRequest request) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity.badRequest()
            .body(createProblem(400, "BAD_REQUEST", "Некорректный запрос", ex.getMessage(), request));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetails> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(404)
            .body(createProblem(404, "RESOURCE_NOT_FOUND", "Ресурс не найден", ex.getMessage(), request));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ProblemDetails> handleConflict(ConflictException ex, WebRequest request) {
        log.warn("Conflict: code={} message={}", ex.getCode(), ex.getMessage());
        return ResponseEntity.status(409)
            .body(createProblem(409, ex.getCode(), "Конфликт состояния", ex.getMessage(), request));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ProblemDetails> handleForbidden(ForbiddenException ex, WebRequest request) {
        log.warn("Forbidden: {}", ex.getMessage());
        return ResponseEntity.status(403)
            .body(createProblem(403, "FORBIDDEN", "Доступ запрещён", ex.getMessage(), request));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetails> handleGeneral(Exception ex, WebRequest request) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(500)
            .body(createProblem(500, "INTERNAL_ERROR", "Внутренняя ошибка", "Сервер не смог завершить операцию.", request));
    }

    private ProblemDetails createProblem(int status, String code, String title, String detail, WebRequest request) {
        ProblemDetails p = new ProblemDetails();
        p.setStatus(status);
        p.setCode(code);
        p.setTitle(title);
        p.setDetail(detail);
        p.setInstance(request.getDescription(false));
        p.setRequestId(MDC.get("requestId"));
        p.setTimestamp(Instant.now());
        p.setType("https://api.parnas.example/problems/" + code.toLowerCase().replace("_", "-"));
        return p;
    }
}

package com.gitinbits.exception;

import com.gitinbits.dto.response.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Global exception handler — translates all application and upstream exceptions
 * into a consistent {@link ErrorResponse} JSON structure.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ─── Domain Exceptions ────────────────────────────────────────────────────

    @ExceptionHandler(GitHubAuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(
            GitHubAuthException ex, HttpServletRequest request) {
        log.warn("Auth error [{}]: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(401, "Unauthorized", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(GitHubForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(
            GitHubForbiddenException ex, HttpServletRequest request) {
        log.warn("Forbidden error [{}]: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(403, "Forbidden", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(GitHubRateLimitException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitException(
            GitHubRateLimitException ex, HttpServletRequest request) {
        log.warn("Rate limit exceeded [{}]: resets at epoch {}", request.getRequestURI(), ex.getResetEpochSeconds());
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ErrorResponse.rateLimited(ex.getMessage(), request.getRequestURI(), ex.getResetEpochSeconds()));
    }

    @ExceptionHandler(GitHubNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            GitHubNotFoundException ex, HttpServletRequest request) {
        log.warn("Not found [{}]: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(404, "Not Found", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(GitHubApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(
            GitHubApiException ex, HttpServletRequest request) {
        log.error("GitHub API error [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(ErrorResponse.of(502, "Bad Gateway",
                        "GitHub API returned an error: " + ex.getMessage(), request.getRequestURI()));
    }

    // ─── Spring Web / Validation Exceptions ───────────────────────────────────

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeader(
            MissingRequestHeaderException ex, HttpServletRequest request) {
        String message = "Required header '" + ex.getHeaderName() + "' is missing. "
                + "All API endpoints (except /api/auth/*) require the X-GitHub-Org header.";
        log.warn("Missing header [{}]: {}", request.getRequestURI(), ex.getHeaderName());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(400, "Bad Request", message, request.getRequestURI()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        String message = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .findFirst()
                .orElse(ex.getMessage());
        log.warn("Validation error [{}]: {}", request.getRequestURI(), message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(400, "Bad Request", message, request.getRequestURI()));
    }

    // ─── RestClient HTTP Exceptions (safety net) ──────────────────────────────

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientError(
            HttpClientErrorException ex, HttpServletRequest request) {
        int statusCode = ex.getStatusCode().value();
        log.warn("Unhandled HTTP client error [{}]: {} {}", request.getRequestURI(), statusCode, ex.getMessage());

        if (statusCode == 401) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse.of(401, "Unauthorized",
                            "Invalid or expired GitHub OAuth token", request.getRequestURI()));
        }
        if (statusCode == 403) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ErrorResponse.of(403, "Forbidden",
                            "GitHub access denied. Check OAuth2 scopes (read:org, repo).", request.getRequestURI()));
        }
        if (statusCode == 404) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.of(404, "Not Found",
                            "GitHub resource not found", request.getRequestURI()));
        }
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ErrorResponse.of(502, "Bad Gateway",
                        "GitHub API error: " + ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpServerError(
            HttpServerErrorException ex, HttpServletRequest request) {
        log.error("GitHub server error [{}]: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ErrorResponse.of(502, "Bad Gateway",
                        "GitHub server error: " + ex.getStatusCode(), request.getRequestURI()));
    }

    // ─── Catch-All ────────────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(500, "Internal Server Error",
                        "An unexpected error occurred. Please try again.", request.getRequestURI()));
    }
}

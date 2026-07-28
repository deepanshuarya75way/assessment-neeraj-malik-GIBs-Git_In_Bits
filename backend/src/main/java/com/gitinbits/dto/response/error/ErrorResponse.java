package com.gitinbits.dto.response.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * Standard error response body returned by all error handlers.
 *
 * <p>Fields are aligned with RFC 7807 (Problem Details for HTTP APIs)
 * to make future standardization easy.
 *
 * @param status    HTTP status code (e.g., 404)
 * @param error     HTTP reason phrase (e.g., "Not Found")
 * @param message   Human-readable description of what went wrong
 * @param path      The request path that triggered the error
 * @param timestamp ISO-8601 timestamp of when the error occurred
 * @param rateLimitResetAt Only present for 429 errors; epoch-second reset time
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        Instant timestamp,
        Long rateLimitResetAt
) {
    /**
     * Convenience factory for errors without a rate-limit reset time.
     */
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(status, error, message, path, Instant.now(), null);
    }

    /**
     * Factory for rate-limit errors that include the reset timestamp.
     */
    public static ErrorResponse rateLimited(String message, String path, long resetEpochSeconds) {
        return new ErrorResponse(429, "Too Many Requests", message, path, Instant.now(), resetEpochSeconds);
    }
}

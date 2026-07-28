package com.gitinbits.exception;

/**
 * Thrown when the GitHub API returns an unexpected error (non-auth, non-rate-limit).
 * Maps to HTTP 502 (Bad Gateway) in the global exception handler,
 * indicating the upstream GitHub API failed.
 */
public class GitHubApiException extends RuntimeException {

    public GitHubApiException(String message) {
        super(message);
    }

    public GitHubApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

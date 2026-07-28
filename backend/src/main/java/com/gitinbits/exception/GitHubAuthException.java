package com.gitinbits.exception;

/**
 * Thrown when the GitHub OAuth2 token is missing, invalid, or expired.
 * Maps to HTTP 401 in the global exception handler.
 */
public class GitHubAuthException extends RuntimeException {

    public GitHubAuthException(String message) {
        super(message);
    }

    public GitHubAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.gitinbits.exception;

/**
 * Thrown when the GitHub API returns a 403 Forbidden response (excluding rate limits).
 * This usually indicates that the authenticated user does not have permission
 * to access the requested resource.
 */
public class GitHubForbiddenException extends RuntimeException {
    public GitHubForbiddenException(String message) {
        super(message);
    }
}

package com.gitinbits.exception;

/**
 * Thrown when GitHub's API rate limit is exhausted.
 * Maps to HTTP 429 in the global exception handler.
 *
 * <p>Carries the epoch-second timestamp at which the rate limit resets,
 * extracted from GitHub's {@code X-RateLimit-Reset} response header.
 */
public class GitHubRateLimitException extends RuntimeException {

    private final long resetEpochSeconds;

    public GitHubRateLimitException(String message, long resetEpochSeconds) {
        super(message);
        this.resetEpochSeconds = resetEpochSeconds;
    }

    /**
     * @return Unix epoch second at which the rate limit window resets.
     *         Zero if the reset time was not available in the response.
     */
    public long getResetEpochSeconds() {
        return resetEpochSeconds;
    }
}

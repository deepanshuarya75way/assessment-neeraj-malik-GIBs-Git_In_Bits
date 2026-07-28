package com.gitinbits.exception;

/**
 * Thrown when a requested GitHub resource (org, repo, branch, PR, etc.) does not exist.
 * Maps to HTTP 404 in the global exception handler.
 */
public class GitHubNotFoundException extends RuntimeException {

    private final String resource;

    public GitHubNotFoundException(String resource) {
        super("Resource not found: " + resource);
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }
}

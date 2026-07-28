/**
 * Provider integration stubs.
 *
 * <p>This package is reserved for future source-control provider integrations.
 * The {@code client.github} package serves as the reference implementation.
 * Additional providers (GitLab, Bitbucket, Azure DevOps, etc.) should follow
 * the same {@link com.gitinbits.client.github.GitHubClient} interface pattern,
 * allowing the service layer to remain provider-agnostic.
 *
 * <p>Planned structure:
 * <pre>
 *   provider/
 *   ├── gitlab/
 *   │   ├── GitLabClient.java       (extends a future ProviderClient interface)
 *   │   └── GitLabClientImpl.java
 *   └── bitbucket/
 *       └── BitbucketClient.java
 * </pre>
 */
package com.gitinbits.provider;

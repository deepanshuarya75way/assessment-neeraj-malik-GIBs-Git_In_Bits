package com.gitinbits.service.github;

import com.gitinbits.client.github.GitHubClient;
import com.gitinbits.client.github.raw.RawOrg;
import com.gitinbits.client.github.raw.RawUser;
import com.gitinbits.dto.response.auth.AuthUserDto;
import com.gitinbits.dto.response.org.OrgSummaryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for authenticated user operations.
 */
@Service
public class GitHubAuthService {

    private static final Logger log = LoggerFactory.getLogger(GitHubAuthService.class);

    private final GitHubClient gitHubClient;

    public GitHubAuthService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    public AuthUserDto getAuthenticatedUser() {
        log.debug("Fetching authenticated user profile");
        RawUser raw = gitHubClient.getCurrentUser();
        return toAuthUserDto(raw);
    }

    public List<OrgSummaryDto> getUserOrgs() {
        log.debug("Fetching organizations for authenticated user");
        return gitHubClient.getUserOrgs()
                .stream()
                .map(this::toOrgSummaryDto)
                .toList();
    }

    private AuthUserDto toAuthUserDto(RawUser raw) {
        return new AuthUserDto(
                raw.login(),
                raw.name(),
                raw.avatarUrl(),
                raw.htmlUrl(),
                raw.email()
        );
    }

    private OrgSummaryDto toOrgSummaryDto(RawOrg raw) {
        return new OrgSummaryDto(
                raw.login(),
                raw.description(),
                raw.avatarUrl(),
                raw.htmlUrl()
        );
    }
}

package com.gitinbits.service.github;

import com.gitinbits.client.github.GitHubClient;
import com.gitinbits.client.github.raw.RawOrg;
import com.gitinbits.dto.context.GitHubContext;
import com.gitinbits.dto.response.org.OrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for GitHub organization metadata.
 */
@Service
public class GitHubOrgService {

    private static final Logger log = LoggerFactory.getLogger(GitHubOrgService.class);

    private final GitHubClient gitHubClient;

    public GitHubOrgService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    public OrgDto getOrg(GitHubContext context) {
        log.debug("Fetching org details for: {}", context.accountName());
        RawOrg raw = gitHubClient.getOrganization(context.accountName());
        return toOrgDto(raw);
    }

    private OrgDto toOrgDto(RawOrg raw) {
        return new OrgDto(
                raw.name() != null ? raw.name() : raw.login(),
                raw.login(),
                raw.description(),
                raw.avatarUrl(),
                raw.htmlUrl(),
                raw.publicRepos()
        );
    }
}

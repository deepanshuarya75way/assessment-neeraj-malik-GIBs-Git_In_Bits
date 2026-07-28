package com.gitinbits.service.github;

import com.gitinbits.client.github.GitHubClient;
import com.gitinbits.client.github.raw.RawTeam;
import com.gitinbits.dto.context.GitHubContext;
import com.gitinbits.dto.response.team.TeamDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for GitHub organization team metadata.
 *
 * <p>GitHub's API does not return team members or team repository access inline
 * with the team listing. For each team, two additional API calls are made:
 * <ol>
 *   <li>GET /orgs/{org}/teams/{slug}/members</li>
 *   <li>GET /orgs/{org}/teams/{slug}/repos</li>
 * </ol>
 */
@Service
public class GitHubTeamService {

    private static final Logger log = LoggerFactory.getLogger(GitHubTeamService.class);

    private final GitHubClient gitHubClient;

    public GitHubTeamService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    public List<TeamDto> listTeams(GitHubContext context) {
        String org = context.accountName();
        log.debug("Listing teams for org: {}", org);

        List<RawTeam> rawTeams = gitHubClient.listTeams(org);
        log.debug("Found {} teams in org '{}'", rawTeams.size(), org);

        return rawTeams.stream()
                .map(team -> buildTeamDto(context, team))
                .toList();
    }

    private TeamDto buildTeamDto(GitHubContext context, RawTeam team) {
        log.debug("Enriching team '{}' with members and repos", team.slug());

        List<String> members = gitHubClient.listTeamMembers(context.accountName(), team.slug())
                .stream()
                .map(m -> m.login())
                .toList();

        List<TeamDto.TeamRepoAccess> repos = gitHubClient.listTeamRepos(context.accountName(), team.slug())
                .stream()
                .map(r -> new TeamDto.TeamRepoAccess(
                        r.name(),
                        r.permissions() != null && r.permissions().admin(),
                        r.permissions() != null && r.permissions().maintain(),
                        r.permissions() != null && r.permissions().push(),
                        r.permissions() != null && r.permissions().triage(),
                        r.permissions() != null && r.permissions().pull()
                ))
                .toList();

        return new TeamDto(
                team.name(),
                team.slug(),
                team.description(),
                team.privacy(),
                team.parent() != null ? team.parent().name() : null,
                members,
                repos
        );
    }
}

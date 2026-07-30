package com.gitinbits.ai;

import com.gitinbits.analysis.service.RepositoryAnalysisService;
import com.gitinbits.dto.context.GitHubContext;
import com.gitinbits.dto.response.analysis.RepositoryAnalysisReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

import com.gitinbits.ai.evidence.RepositoryEvidenceService;
import com.gitinbits.ai.evidence.RepositoryEvidence;
import com.gitinbits.persistence.repository.RepoRepository;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class RepositoryAnalysisTools {

    private static final Logger log = LoggerFactory.getLogger(RepositoryAnalysisTools.class);
    private final RepositoryAnalysisService analysisService;
    private final RepositoryEvidenceService evidenceService;
    private final RepoRepository repoRepository;

    public RepositoryAnalysisTools(RepositoryAnalysisService analysisService, RepositoryEvidenceService evidenceService, RepoRepository repoRepository) {
        this.analysisService = analysisService;
        this.evidenceService = evidenceService;
        this.repoRepository = repoRepository;
    }

    public record EmptyRequest() {}

    @Bean
    @Description("Get a list of all available repositories in the system. Use this to find the EXACT owner and repoName strings required by other tools, especially if the user provides an informal name (e.g. 'DSA- Github' -> 'Neeraj-Malik-12/DSA-Github').")
    public Function<EmptyRequest, List<String>> listAvailableRepositories() {
        return request -> {
            log.info("AI Orchestrator invoked listAvailableRepositories tool");
            return repoRepository.findAll().stream().map(repo -> repo.owner() + "/" + repo.repoName()).collect(Collectors.toList());
        };
    }

    public record RepositoryRequest(String owner, String repoName) {}

    @Bean
    @Description("Get semantic evidence for a specific GitHub repository. Use this to fetch meaningful engineering metrics (like top contributors, commit frequency, and recent commit messages) when a user asks about recent activity, what the commits suggest, or WHY a repository has a certain health score.")
    public Function<RepositoryRequest, RepositoryEvidence> getRepositoryEvidence() {
        return request -> {
            log.info("AI Orchestrator invoked getRepositoryEvidence tool for repo: {}/{}", request.owner(), request.repoName());
            return evidenceService.getEvidence(request.owner(), request.repoName());
        };
    }

    @Bean
    @Description("Get deterministic engineering analytics (Health, Risk, Stability, Trends) for a specific GitHub repository. Use this to fetch the actual backend-calculated metrics before answering questions about a repository's status.")
    public Function<RepositoryRequest, RepositoryAnalysisReport> getRepositoryAnalytics() {
        return request -> {
            log.info("AI Orchestrator invoked getRepositoryAnalytics tool for repo: {}/{}", request.owner(), request.repoName());
            GitHubContext context = new GitHubContext(com.gitinbits.dto.context.DataSourceType.PUBLIC_REPOSITORY, request.owner(), request.repoName());
            return analysisService.analyzeRepository(context, request.repoName());
        };
    }

    @Bean
    @Description("Get the historical timeline of the repository including its creation, first commit, and total lifetime commits. Use this when the user asks about the history or journey of the repository.")
    public Function<RepositoryRequest, com.gitinbits.ai.evidence.RepositoryTimeline> getRepositoryTimeline() {
        return request -> {
            log.info("AI Orchestrator invoked getRepositoryTimeline tool for repo: {}/{}", request.owner(), request.repoName());
            return evidenceService.getTimeline(request.owner(), request.repoName());
        };
    }
}

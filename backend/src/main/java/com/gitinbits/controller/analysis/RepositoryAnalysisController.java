package com.gitinbits.controller.analysis;

import com.gitinbits.analysis.repository.stability.RepositoryStabilityResult;
import com.gitinbits.analysis.service.RepositoryAnalysisService;
import com.gitinbits.sync.RepositorySynchronizer;
import com.gitinbits.dto.context.GitHubContext;
import com.gitinbits.dto.response.analysis.RepositoryAnalysisReport;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing the Repository Analysis Layer.
 *
 * <p>This controller provides the central endpoint for complete repository-level analysis.
 * Although only the {@code health} dimension is currently available in V1, future versions will extend
 * the returned {@link RepositoryAnalysisReport} with additional analysis modules (e.g., risk, stability,
 * trends, findings, recommendations, AI summary) without altering or fragmenting this API endpoint.
 */
@Validated
@RestController
@RequestMapping("/api/repos")
public class RepositoryAnalysisController {

    private static final Logger log = LoggerFactory.getLogger(RepositoryAnalysisController.class);

    private final RepositoryAnalysisService repositoryAnalysisService;
    private final RepositorySynchronizer repositorySynchronizer;

    public RepositoryAnalysisController(
            RepositoryAnalysisService repositoryAnalysisService,
            RepositorySynchronizer repositorySynchronizer) {
        this.repositoryAnalysisService = repositoryAnalysisService;
        this.repositorySynchronizer = repositorySynchronizer;
    }

    /**
     * Executes complete analysis across the requested repository.
     *
     * <p>This endpoint represents the entire Repository Analysis domain. Future analysis modules
     * will be added directly into the returned report object without endpoint redesign.
     *
     * @param context GitHub authentication and data source context
     * @param repo    Target repository name
     * @return 200 OK containing the combined {@link RepositoryAnalysisReport}
     */
    @GetMapping("/{repo}/analysis")
    public ResponseEntity<RepositoryAnalysisReport> analyzeRepository(
            GitHubContext context,
            @PathVariable @NotBlank String repo
    ) {
        String repoFullName = context.accountName() + "/" + repo;
        log.debug("Repository analysis requested for repo={} [organization/account={}]", repo, context.accountName());
        
        // Auto-sync for PoC if the repository has never been analyzed/synchronized
        repositorySynchronizer.syncIfNeeded(context, repoFullName);

        RepositoryAnalysisReport report = repositoryAnalysisService.analyzeRepository(context, repo);

        log.debug("Repository analysis completed for repo={} [organization/account={}]: overallHealthScore={}, healthLevel={}",
                repo, context.accountName(),
                report.health() != null ? report.health().overallScore() : "N/A",
                report.health() != null ? report.health().healthLevel() : "N/A");

        return ResponseEntity.ok(report);
    }

    /**
     * Executes repository stability analysis.
     *
     * @param context GitHub authentication and data source context
     * @param repo    Target repository name
     * @return 200 OK containing the {@link RepositoryStabilityResult}
     */
    @GetMapping("/{repo}/analysis/stability")
    public ResponseEntity<RepositoryStabilityResult> analyzeRepositoryStability(
            GitHubContext context,
            @PathVariable @NotBlank String repo
    ) {
        String repoFullName = context.accountName() + "/" + repo;
        log.debug("GET /api/repos/{}/analysis/stability", repo);
        
        // Auto-sync for PoC if the repository has never been analyzed/synchronized
        repositorySynchronizer.syncIfNeeded(context, repoFullName);

        RepositoryStabilityResult result = repositoryAnalysisService.analyzeRepositoryStability(context, repo);
        
        log.debug("Returning Repository Stability Result");
        return ResponseEntity.ok(result);
    }
}

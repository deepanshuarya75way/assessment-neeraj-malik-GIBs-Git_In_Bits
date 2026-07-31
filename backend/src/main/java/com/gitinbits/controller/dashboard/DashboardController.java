package com.gitinbits.controller.dashboard;

import com.gitinbits.ai.AiDashboardService;
import com.gitinbits.dto.response.repo.DeveloperActivitySummaryDto;
import com.gitinbits.persistence.document.OrganizationSummaryDoc;
import com.gitinbits.persistence.repository.OrganizationSummaryRepository;
import com.gitinbits.service.DeveloperEvidenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final OrganizationSummaryRepository summaryRepository;
    private final DeveloperEvidenceService devEvidenceService;
    private final AiDashboardService aiDashboardService;
    private final com.gitinbits.service.OrganizationEvidenceService orgEvidenceService;
    private final com.gitinbits.service.github.GitHubRepoService repoService;
    private final com.gitinbits.sync.RepositorySynchronizer repositorySynchronizer;

    public DashboardController(
            OrganizationSummaryRepository summaryRepository,
            DeveloperEvidenceService devEvidenceService,
            AiDashboardService aiDashboardService,
            com.gitinbits.service.OrganizationEvidenceService orgEvidenceService,
            com.gitinbits.service.github.GitHubRepoService repoService,
            com.gitinbits.sync.RepositorySynchronizer repositorySynchronizer
    ) {
        this.summaryRepository = summaryRepository;
        this.devEvidenceService = devEvidenceService;
        this.aiDashboardService = aiDashboardService;
        this.orgEvidenceService = orgEvidenceService;
        this.repoService = repoService;
        this.repositorySynchronizer = repositorySynchronizer;
    }

    @GetMapping("/summary")
    public ResponseEntity<OrganizationSummaryDoc> getOrgSummary(
            @RequestParam String owner,
            @RequestParam(defaultValue = "1_day") String timeframe
    ) {
        Optional<OrganizationSummaryDoc> doc = summaryRepository.findFirstByOwnerAndTimeframeOrderByGeneratedAtDesc(owner, timeframe);
        return doc.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PostMapping("/summary/generate")
    public ResponseEntity<Map<String, String>> triggerGenerateSummary(
            @RequestParam String owner,
            @RequestParam(defaultValue = "1_day") String timeframe
    ) {
        // Trigger background sync, but don't block AI generation
        syncAllReposAsync(owner);
        
        aiDashboardService.generateOrgBrief(owner, timeframe);
        return ResponseEntity.ok(Map.of("status", "Summary generated successfully"));
    }

    @GetMapping("/developers")
    public ResponseEntity<List<DeveloperActivitySummaryDto>> getDevelopers(@RequestParam String owner) {
        return ResponseEntity.ok(devEvidenceService.getTopDevelopers(owner));
    }

    @GetMapping("/developers/{authorName}/ai-report")
    public ResponseEntity<Map<String, String>> getDeveloperAiReport(
            @RequestParam String owner,
            @PathVariable String authorName,
            @RequestParam(defaultValue = "30_days") String timeframe
    ) {
        String report = aiDashboardService.generateDeveloperEfficiencyReport(owner, authorName, timeframe);
        return ResponseEntity.ok(Map.of("report", report));
    }
    
    @GetMapping("/developers/{authorName}/evidence")
    public ResponseEntity<com.gitinbits.service.DeveloperEvidenceService.DeveloperEvidence> getDeveloperEvidence(
            @RequestParam String owner,
            @PathVariable String authorName,
            @RequestParam(defaultValue = "30_days") String timeframe
    ) {
        int days = 30;
        if (timeframe.startsWith("1_")) days = 1;
        else if (timeframe.startsWith("3_")) days = 3;
        else if (timeframe.startsWith("7_")) days = 7;
        else if (timeframe.startsWith("10_")) days = 10;
        else if (timeframe.startsWith("30_")) days = 30;
        else if (timeframe.equals("lifetime")) days = 36500; // 100 years
        
        java.time.Instant until = java.time.Instant.now();
        java.time.Instant since = until.minus(java.time.Duration.ofDays(days));
        
        return ResponseEntity.ok(devEvidenceService.gatherEvidence(owner, authorName, since, until));
    }
    
    @GetMapping("/organization/evidence")
    public ResponseEntity<com.gitinbits.service.OrganizationEvidenceService.OrganizationEvidence> getOrganizationEvidence(
            @RequestParam String owner,
            @RequestParam(defaultValue = "1_day") String timeframe
    ) {
        // Trigger background sync, don't block the UI
        syncAllReposAsync(owner);

        int days = 1;
        if (timeframe.startsWith("3_")) days = 3;
        else if (timeframe.startsWith("7_")) days = 7;
        else if (timeframe.startsWith("10_")) days = 10;
        else if (timeframe.startsWith("30_")) days = 30;
        
        java.time.Instant until = java.time.Instant.now();
        java.time.Instant since = until.minus(java.time.Duration.ofDays(days));
        
        return ResponseEntity.ok(orgEvidenceService.gatherEvidence(owner, since, until));
    }

    private void syncAllRepos(String owner) {
        try {
            com.gitinbits.dto.context.GitHubContext context = new com.gitinbits.dto.context.GitHubContext(
                    com.gitinbits.dto.context.DataSourceType.AUTHENTICATED_ORGANIZATION, owner, "");
            List<com.gitinbits.dto.response.repo.RepoDto> repos = repoService.listRepos(context);
            for (com.gitinbits.dto.response.repo.RepoDto repo : repos) {
                repositorySynchronizer.syncIfNeeded(context, owner + "/" + repo.name());
            }
        } catch (Exception e) {
            // Ignore if sync fails, return whatever is in DB
        }
    }

    private void syncAllReposAsync(String owner) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        new Thread(() -> {
            org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
            try {
                syncAllRepos(owner);
            } finally {
                org.springframework.security.core.context.SecurityContextHolder.clearContext();
            }
        }).start();
    }
}

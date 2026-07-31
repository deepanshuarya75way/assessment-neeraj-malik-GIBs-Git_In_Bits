package com.gitinbits.ai;

import com.gitinbits.service.DeveloperEvidenceService;
import com.gitinbits.service.OrganizationEvidenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

@Configuration
public class SemanticAiTools {

    private static final Logger log = LoggerFactory.getLogger(SemanticAiTools.class);
    
    private final OrganizationEvidenceService orgEvidenceService;
    private final DeveloperEvidenceService devEvidenceService;

    public SemanticAiTools(OrganizationEvidenceService orgEvidenceService, DeveloperEvidenceService devEvidenceService) {
        this.orgEvidenceService = orgEvidenceService;
        this.devEvidenceService = devEvidenceService;
    }

    public record OrgOverviewRequest(String owner, int lastDays) {}

    @Bean
    @Description("Retrieves an overview of the organization's engineering activity over a specified number of days. Useful to see what the whole company or organization has been working on recently.")
    public Function<OrgOverviewRequest, OrganizationEvidenceService.OrganizationEvidence> getOrganizationOverview() {
        return request -> {
            log.info("Tool called: getOrganizationOverview for {} over last {} days", request.owner(), request.lastDays());
            Instant until = Instant.now();
            Instant since = until.minus(Duration.ofDays(request.lastDays()));
            return orgEvidenceService.gatherEvidence(request.owner(), since, until);
        };
    }

    public record DevEvidenceRequest(String owner, String developerUsername, int lastDays) {}

    @Bean
    @Description("Retrieves deep deterministic evidence about a specific developer's efficiency, pull requests, CI/CD health, and review cycles. Call this when you need to understand if a developer is productive, stuck, or facing bottlenecks.")
    public Function<DevEvidenceRequest, DeveloperEvidenceService.DeveloperEvidence> getDeveloperEvidence() {
        return request -> {
            log.info("Tool called: getDeveloperEvidence for {}/{} over last {} days", request.owner(), request.developerUsername(), request.lastDays());
            Instant until = Instant.now();
            Instant since = until.minus(Duration.ofDays(request.lastDays()));
            return devEvidenceService.gatherEvidence(request.owner(), request.developerUsername(), since, until);
        };
    }
}

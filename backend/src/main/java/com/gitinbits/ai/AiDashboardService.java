package com.gitinbits.ai;

import com.gitinbits.persistence.document.CommitDoc;
import com.gitinbits.persistence.document.OrganizationSummaryDoc;
import com.gitinbits.persistence.repository.CommitRepository;
import com.gitinbits.persistence.repository.OrganizationSummaryRepository;
import com.gitinbits.service.DeveloperEvidenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AiDashboardService {

    private static final Logger log = LoggerFactory.getLogger(AiDashboardService.class);

    private final ChatClient chatClient;
    private final CommitRepository commitRepository;
    private final OrganizationSummaryRepository summaryRepository;
    private final DeveloperEvidenceService devEvidenceService;
    private final com.gitinbits.service.OrganizationEvidenceService orgEvidenceService;

    public AiDashboardService(
            ChatClient.Builder chatClientBuilder,
            CommitRepository commitRepository,
            OrganizationSummaryRepository summaryRepository,
            DeveloperEvidenceService devEvidenceService,
            com.gitinbits.service.OrganizationEvidenceService orgEvidenceService
    ) {
        this.chatClient = chatClientBuilder.build();
        this.commitRepository = commitRepository;
        this.summaryRepository = summaryRepository;
        this.devEvidenceService = devEvidenceService;
        this.orgEvidenceService = orgEvidenceService;
    }

    public void generateOrgBrief(String owner, String timeframe) {
        log.info("Generating AI Brief for organization: {} over timeframe: {}", owner, timeframe);
        
        int days = 1;
        if (timeframe.startsWith("3_")) days = 3;
        else if (timeframe.startsWith("7_")) days = 7;
        else if (timeframe.startsWith("10_")) days = 10;
        else if (timeframe.startsWith("30_")) days = 30;
        
        Instant until = Instant.now();
        Instant since = until.minus(java.time.Duration.ofDays(days));
        
        com.gitinbits.service.OrganizationEvidenceService.OrganizationEvidence evidence = orgEvidenceService.gatherEvidence(owner, since, until);

        String prompt = "You are an Engineering Intelligence Analyst for a GitHub organization. Look at this deterministic evidence for the organization '" + owner + "' over the last " + days + " days:\n" +
                "- Active Workstreams: " + String.join(", ", evidence.activeWorkstreams()) + "\n" +
                "- Recently Completed: " + String.join(", ", evidence.recentlyCompleted()) + "\n" +
                "- Needs Attention: " + String.join(", ", evidence.needsAttention()) + "\n" +
                "- Total PRs Merged: " + evidence.totalPrsMerged() + "\n" +
                "- Total Issues Closed: " + evidence.totalIssuesClosed() + "\n" +
                "- Total Workflow Failures: " + evidence.totalWorkflowFailures() + "\n\n" +
                "Your job is to interpret this data and produce a concise, highly readable engineering report that tells a technical leader: What happened, what is going well, and what needs attention.\n" +
                "CRITICAL INSTRUCTIONS:\n" +
                "1. Structure the report EXACTLY with these markdown headers:\n" +
                "   ### 🚨 Overall Status\n" +
                "   ### 📊 What Happened\n" +
                "   ### 🟢 What Is Going Well\n" +
                "   ### 🟠 What Needs Attention\n" +
                "   ### 💡 Key Takeaway\n" +

                "2. Format the response entirely in Markdown. NEVER write a large dense paragraph. Prioritize insights over raw statistics. Use bullet points and short sentences.\n" +
                "3. If multiple different repositories were worked on, mention them by name and bold them.\n" +
                "4. The evidence uses [PR] and [Commit] tags to distinguish work types. DO NOT include these tags or 'RepoName:' prefixes in your final summary. Weave them naturally.\n" +
                "5. Write like a senior engineering manager giving a morning briefing (analytical, direct, professional). Make it scannable in 15 seconds." +
                "6. Keep it concise but deeply insightful." +
                "7. DO NOT write a single dense paragraph. Break it down so it is highly scannable and easy to read.\n" +
                "8. USe #### bold for subheadings";
        String aiSummary = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        OrganizationSummaryDoc doc = new OrganizationSummaryDoc(
                null,
                owner,
                timeframe,
                aiSummary,
                Instant.now()
        );
        summaryRepository.save(doc);
        log.info("Saved Org Brief for {} timeframe {}", owner, timeframe);
    }

    public String generateDeveloperEfficiencyReport(String owner, String authorName, String timeframe) {
        log.info("Generating efficiency report for developer: {} over timeframe: {}", authorName, timeframe);
        
        int days = 30;
        if (timeframe.startsWith("1_")) days = 1;
        else if (timeframe.startsWith("3_")) days = 3;
        else if (timeframe.startsWith("7_")) days = 7;
        else if (timeframe.startsWith("10_")) days = 10;
        else if (timeframe.startsWith("30_")) days = 30;
        else if (timeframe.equals("lifetime")) days = 36500;
        
        Instant until = Instant.now();
        Instant since = until.minus(java.time.Duration.ofDays(days));
        
        DeveloperEvidenceService.DeveloperEvidence evidence = devEvidenceService.gatherEvidence(owner, authorName, since, until);
        
        if (evidence.commitCount() == 0 && evidence.prsOpened() == 0) {
            return "No recent activity found for this developer.";
        }

        String timeframeStr = timeframe.equals("lifetime") ? "lifetime" : days + " days";
        String prompt = "You are an Engineering Manager. Here is the deterministic evidence for developer " + authorName + " over the last " + timeframeStr + ":\n" +
                "- Commits: " + evidence.commitCount() + " (Additions: " + evidence.totalAdditions() + " | Deletions: " + evidence.totalDeletions() + ")\n" +
                "- PRs: " + evidence.prsOpened() + " Opened, " + evidence.prsMerged() + " Merged. (Avg Time to Merge: " + evidence.avgMergeTime() + ").\n" +
                "- Code Reviews Conducted: " + evidence.reviewsConducted() + ".\n" +
                "- Issues Resolved/Assigned: " + evidence.issuesResolved() + ".\n" +
                "- CI/CD Health: " + evidence.workflowFailures() + " failures vs " + evidence.workflowSuccesses() + " successes.\n\n" +
                "Based ONLY on this evidence, explain:\n" +
                "1. What progress has occurred.\n" +
                "2. Are they highly efficient at shipping (e.g. good merge rate) or is there a bottleneck (e.g. slow merge times, high CI failures, or only superficial commit counts)?\n" +
                "Write a 1-paragraph highly insightful engineering review. Be constructive but honest. Format using markdown.";

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}

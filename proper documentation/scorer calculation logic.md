# Scorer Calculation Logic

This document outlines the detailed calculation logic used to determine the health scores for Commits and Pull Requests in the Git in Bits analysis engine. Whenever the scoring formulas are modified in the codebase, this document should be updated to reflect the latest engineering philosophy and weighting.

---

## 1. Commit Health Scorer
**Base Empty Score:** `30` (If a repository has 0 commits to analyze)

The Commit Health score evaluates the volume, scope, and diversity of a repository's commit history. It calculates a 0-100 score by summing five weighted metrics. Each individual metric is clamped to 0-100.

| Metric | Weight | Target | Calculation Logic |
|---|---|---|---|
| **Commit Frequency** | 30% | 50 commits | `(Total Commits / 50) * 100` |
| **Average Size** | 25% | <= 500 lines | `100 - ((Avg Lines Changed / 500) * 100)` |
| **Files Changed** | 15% | <= 10 files | `100 - ((Avg Files Changed / 10) * 100)` |
| **Verified Ratio** | 15% | 100% verified | `(Verified Commits / Total Commits) * 100` |
| **Contributors** | 15% | 5 authors | `(Unique Authors / 5) * 100` |

---

## 2. Pull Request Health Scorer
**Base Empty Score:** `50` (If a repository has 0 pull requests to analyze)

The Pull Request Health score evaluates engineering workflows by observing merging behavior, peer review participation, and PR scope. It calculates a 0-100 score by summing six weighted metrics. Each individual metric is clamped to 0-100.

### 2.1 Merge Rate (30% weight)
- Evaluates the ratio of merged PRs against closed PRs.
- **Formula:** `(Merged PRs / Closed PRs) * 100`
- **Edge Case:** If there are 0 closed PRs (only open PRs exist), the score defaults to `100` to avoid penalizing active workflows.

### 2.2 Review Participation (25% weight)
- Evaluates peer reviews per PR.
- **Target:** 3.0 average reviews per PR.
- **Formula:** `(Average Reviews / 3.0) * 100`

### 2.3 Discussion Quality (15% weight)
- Evaluates commenting and collaboration. 
- Designed as a capped function to reward healthy discussion but penalize overly bloated/argumentative PRs.
- **Ideal Range:** 2 to 10 average comments per PR (Scores `100`).
- **Low Comments (< 2):** Scales linearly from a baseline of `20` up to `100`.
- **High Comments (> 10):** Penalizes by dropping linearly from `100` down to `40`. The penalty maxes out at 20+ comments.

### 2.4 Pull Request Size (15% weight)
- Rewards smaller, focused PRs that are easy to review. Computed by averaging the penalties of lines changed and files changed.
- **Lines Changed:**
  - Target: 300 lines. Max Penalty Threshold: 1000 lines.
  - Exceeding 300 lines starts dropping the score from 100 down to a minimum of 40.
- **Files Changed:**
  - Target: 15 files. Max Penalty Threshold: 50 files.
  - Exceeding 15 files starts dropping the score from 100 down to a minimum of 40.

### 2.5 Draft Ratio (10% weight)
- Penalizes having a massive backlog of lingering draft PRs, but avoids overly penalizing legitimate usage.
- **Formula:** `100 - ((Drafts / Total PRs) * 70)` 
- **Example:** Having 100% drafts drops this metric to `30`, not 0.

### 2.6 Activity Freshness (5% weight)
- Evaluates the time since the most recent PR was created or updated.
- **<= 7 days ago:** `100`
- **<= 30 days ago:** `80`
- **<= 90 days ago:** `50`
- **> 90 days ago:** `20`

---

## 3. Issue Health Scorer
**Base Empty Score:** `50` (If a repository has 0 issues to analyze)

The Issue Health score evaluates how effectively a repository manages and resolves issues by looking at resolution rate, backlog, issue freshness, organization, and assignment coverage. Each individual metric is clamped to 0-100.

### 3.1 Resolution Rate (30% weight)
- Evaluates the ratio of closed issues against total issues.
- **Formula:** `(Closed Issues / Total Issues) * 100`

### 3.2 Open Issue Backlog (20% weight)
- Evaluates the percentage of issues that remain open.
- Small repositories (<10 open issues) have their backlog penalty reduced proportionally to avoid punishing small workloads.
- **Formula:** `100 - ((Open Ratio * 100) * Penalty Factor)`

### 3.3 Issue Freshness (20% weight)
- Evaluates how long issues remain unresolved.
- For open issues, counts time since creation/update (<=30 days = `100`, <=90 days = `80`, <=180 days = `50`, >180 days = `20`).
- For closed issues, counts time since closure, applying less severe aging curves.

### 3.4 Issue Organization (15% weight)
- Evaluates issue structure through labels and milestones.
- Applying a label adds `80` points. Assigning a milestone adds `20` points. Averaged across all issues.

### 3.5 Assignment Coverage (10% weight)
- Rewards clear issue ownership via assignees without massively penalizing teams that do not assign work.
- **Formula:** `50 + (Assignment Ratio * 50)`

### 3.6 Discussion Activity (5% weight)
- Evaluates active collaboration.
- Uses a capped function matching the PR comment logic, maxing out at `100` for 1-8 comments, scaling down to `40` for 25+ comments.

---

## 4. Review Health Scorer
**Base Empty Score:** `50` (If a repository has 0 reviews to analyze)

The Review Health score evaluates the quality, discipline, and maturity of the code review process. It calculates a 0-100 score by summing six weighted metrics. Each individual metric is clamped to 0-100.

### 4.1 Review Participation (30% weight)
- Evaluates raw review volume against an ideal baseline.
- **Formula:** `(Total Reviews / 30.0) * 100`

### 4.2 Approval Ratio (25% weight)
- Evaluates the ratio of APPROVED reviews to total completed reviews (APPROVED, CHANGES_REQUESTED, DISMISSED).
- Approvals between 60% and 95% are ideal (`100` score). 
- Approvals > 95% indicates potential rubber-stamping, slightly penalizing the score (`85`).
- Approvals < 60% scales linearly down to 0.

### 4.3 Change Request Quality (20% weight)
- Evaluates if reviews are enforcing quality.
- 0% change requests implies superficial reviews (`70` score).
- 5% to 30% change requests is the ideal sweet spot (`100` score).
- 30% to 50% change requests reflects unstable code (`80` score).
- > 50% change requests is a severely unstable codebase (`50` score).

### 4.4 Review Completion (10% weight)
- Penalizes having too many PENDING or DISMISSED reviews.
- **Formula:** `100 - ((Incomplete Reviews / Total Reviews) * 100)`

### 4.5 Review Timeliness (10% weight)
- Evaluates the time since the most recent review was submitted.
- **<= 7 days ago:** `100`
- **<= 30 days ago:** `80`
- **<= 90 days ago:** `50`
- **> 90 days ago:** `30`

### 4.6 Review Documentation (5% weight)
- Rewards reviews that have written, non-empty feedback bodies (>= 10 characters).
- **Formula:** `(Meaningful Reviews / Total Reviews) * 100`

---

## 5. Branch Health Scorer
**Base Empty Score:** `50` (If a repository has 0 branches to analyze)

The Branch Health score evaluates how safely and professionally branches are managed. It is an additive score based on configuration safety and branch hygiene, capped between 0 and 100.

### 5.1 Protected Branch Score (+60 max)
- Evaluates if important branches (especially the default branch) are protected.
- **Default branch protected:** +40 points
- **Additional protected branches:** +10 points each (up to +20 points max)

### 5.2 Review Requirements (+20 max)
- Rewards branches that enforce code review policies before merging.
- If **any** branch has `requiredReviews` enabled: +20 points

### 5.3 Force Push Safety (+10 max)
- Detects risky configurations where history can be rewritten.
- If **any** branch explicitly disables `forcePushAllowed`: +10 points

### 5.4 Deletion Safety (+10 max)
- Detects whether branches are protected from accidental deletion.
- If **any** branch explicitly disables `deletionAllowed`: +10 points

### 5.5 Stale Branch Penalty (-20 max)
- Penalizes poor branch hygiene and abandoned work.
- Evaluates `latestCommitTimestamp` on every branch.
- **Penalty:** -5 points for every branch inactive for >90 days (up to -20 points max).

---

## 6. Contributor Health Scorer
**Base Empty Score:** `50` (If a repository has 0 contributors to analyze)

The Contributor Health score evaluates the sustainability and collaboration quality of the repository's ecosystem. It calculates a 0-100 score by summing five weighted metrics, clamped between 0 and 100.

### 6.1 Active Contributor Count (30% weight)
- Evaluates raw community size.
- **Formula:** `(Total Contributors / 10.0) * 100`

### 6.2 Contribution Distribution (30% weight)
- Evaluates how evenly work is distributed among the team using the Herfindahl-Hirschman Index (HHI).
- HHI approaches 1.0 when one person monopolizes commits, and approaches 0 when commits are perfectly even.
- **Formula:** `(1.0 - HHI) * 125` (slightly boosted to allow small balanced teams to score 100).
- If there is only 1 contributor, the score defaults to a harsh `20`.

### 6.3 Bus Factor (20% weight)
- Evaluates dependency on the single largest contributor.
- If the largest contributor accounts for <= 30% of work, the score is `100`.
- If the largest contributor accounts for >= 90% of work, the score drops to `20`.
- Between 30% and 90%, the score interpolates linearly between `100` and `20`.

### 6.4 Contributor Engagement (10% weight)
- Evaluates the average activity level per contributor.
- **Target:** 30 commits per contributor on average.
- **Formula:** `20 + ((Average Contributions / 30.0) * 80)`

### 6.5 Community Diversity (10% weight)
- Evaluates the proportion of work coming from *outside* the single largest contributor.
- This favors ecosystems where many occasional contributors chip in.
- **Formula:** `(Non-Dominant Contributions / Total Contributions) / 0.50 * 100` (meaning if 50% of commits come from others, the ecosystem is 100% diverse).

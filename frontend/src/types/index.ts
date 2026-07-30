export type DataSourceType = 'AUTHENTICATED_ORGANIZATION' | 'PUBLIC_ORGANIZATION' | 'PUBLIC_REPOSITORY';

export const DataSourceType = {
  AUTHENTICATED_ORGANIZATION: 'AUTHENTICATED_ORGANIZATION',
  PUBLIC_ORGANIZATION: 'PUBLIC_ORGANIZATION',
  PUBLIC_REPOSITORY: 'PUBLIC_REPOSITORY'
} as const;

export interface OrgSummary {
  login: string;
  description: string;
  avatarUrl: string;
  htmlUrl: string;
}

export interface Repo {
  name: string;
  fullName: string;
  description: string;
  visibility: string;
  defaultBranch: string;
  language: string;
  htmlUrl: string;
  cloneUrl: string;
  sshUrl: string;
  fork: boolean;
  archived: boolean;
  disabled: boolean;
  license: string;
  homepage: string;
  topics: string[];
  openIssuesCount: number;
  forksCount: number;
  stargazersCount: number;
  watchersCount: number;
  networkCount: number;
  subscribersCount: number;
  size: number;
  createdAt: string;
  updatedAt: string;
  pushedAt: string;
}

export interface Branch {
  name: string;
  protected: boolean;
  sha: string;
  protection?: {
    requiredStatusChecks?: { strict?: boolean };
    requiredPullRequestReviews?: { dismissStaleReviews?: boolean };
    allowForcePushes?: { enabled?: boolean };
    allowDeletions?: { enabled?: boolean };
  };
}

export interface Commit {
  sha: string;
  authorName: string;
  authorEmail: string;
  committerName: string;
  committerEmail: string;
  message: string;
  timestamp: string;
  htmlUrl: string;
  parentShas: string[];
  verificationStatus: string;
  filesChangedCount: number;
  additions: number;
  deletions: number;
  totalChanges: number;
  changedFileNames: string[];
}

export interface PullRequest {
  number: number;
  title: string;
  body: string;
  author: string;
  state: string;
  createdAt: string;
  closedAt: string;
  mergedAt: string;
  baseRef: string;
  headRef: string;
  draft: boolean;
  locked: boolean;
  mergeCommitSha: string;
  mergeableState: string;
  commitsCount: number;
  changedFilesCount: number;
  reviewCount: number;
  commentCount: number;
  assignees: string[];
  requestedReviewers: string[];
  labels: string[];
  milestone: string;
}

export interface Review {
  reviewer: string;
  state: string;
  submittedAt: string;
  body: string;
  commitSha: string;
}

export interface ReviewComment {
  id: number;
  reviewer: string;
  body: string;
  path: string;
  line: number;
  originalLine: number;
  createdAt: string;
  updatedAt: string;
}

export interface Issue {
  number: number;
  title: string;
  body: string;
  labels: string[];
  assignees: string[];
  state: string;
  createdAt: string;
  closedAt: string;
  locked: boolean;
  commentCount: number;
  milestone: string;
}

export interface Contributor {
  login: string;
  avatarUrl: string;
  contributions: number;
  profileUrl: string;
}

export interface Release {
  version: string;
  tag: string;
  name: string;
  body: string;
  draft: boolean;
  prerelease: boolean;
  publishedAt: string;
  author: string;
}

export interface Team {
  name: string;
  slug: string;
  description: string;
  privacy: string;
  members: string[];
  repos: string[];
  parentTeam: string;
  repositoryPermissions: Record<string, boolean>;
}

export interface CommitComment {
  id: number;
  author: string;
  comment: string;
  createdAt: string;
  updatedAt: string;
}

export interface IssueComment {
  id: number;
  author: string;
  comment: string;
  createdAt: string;
  updatedAt: string;
}

export interface Label {
  name: string;
  description: string;
  color: string;
}

export interface Milestone {
  title: string;
  description: string;
  dueDate: string;
  state: string;
  openIssues: number;
  closedIssues: number;
}

export interface Workflow {
  name: string;
  state: string;
  path: string;
  createdAt: string;
  updatedAt: string;
}

export interface WorkflowRun {
  name: string;
  branch: string;
  status: string;
  conclusion: string;
  event: string;
  createdAt: string;
  updatedAt: string;
}

export interface Deployment {
  id: number;
  environment: string;
  state: string;
  creator: string;
  createdAt: string;
  updatedAt: string;
  repositoryUrl: string;
}

export interface Collaborator {
  username: string;
  avatarUrl: string;
  profileUrl: string;
  roleName: string;
  permissions?: {
    admin: boolean;
    maintain: boolean;
    push: boolean;
    triage: boolean;
    pull: boolean;
  };
}

export interface Ruleset {
  id: number;
  name: string;
  target: string;
  enforcement: string;
  conditions: any;
  rules: any[];
}

export type HealthLevel = 'EXCELLENT' | 'GOOD' | 'FAIR' | 'POOR' | 'CRITICAL';

export type TrendDirection = 'IMPROVING' | 'STABLE' | 'DECLINING';

export interface RepositoryHealthBreakdown {
  commitScore: number;
  pullRequestScore: number;
  issueScore: number;
  reviewScore: number;
  branchScore: number;
  contributorScore: number;
  overallScore: number;
}

export type FindingType = 'STRENGTH' | 'WARNING' | 'RISK' | 'RECOMMENDATION';

export type Severity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface HealthFinding {
  type: FindingType;
  title: string;
  description: string;
  severity: Severity;
  recommendation?: string;
}

export interface RepositoryHealthResult {
  repositoryName: string;
  overallScore: number;
  healthLevel: HealthLevel;
  breakdown: RepositoryHealthBreakdown;
  findings: HealthFinding[];
  evaluationTimestamp: string;
}

export interface RepositoryRiskBreakdown {
  commitRisk: number;
  pullRequestRisk: number;
  issueRisk: number;
  reviewRisk: number;
  branchRisk: number;
  releaseRisk: number;
  overallRisk: number;
}

export interface RepositoryRiskResult {
  breakdown: RepositoryRiskBreakdown;
  overallRisk: number;
  riskLevel: HealthLevel;
  evaluationTimestamp: string;
}

export interface RepositoryTrendBreakdown {
  commitTrend: number;
  pullRequestTrend: number;
  issueTrend: number;
  reviewTrend: number;
  contributorTrend: number;
  releaseTrend: number;
  overallTrend: number;
}

export interface RepositoryTrendResult {
  breakdown: RepositoryTrendBreakdown;
  overallTrend: number;
  direction: TrendDirection;
  evaluationTimestamp: string;
}

export interface RepositoryAnalysisReport {
  health?: RepositoryHealthResult;
  stability?: RepositoryStabilityResult;
  risk?: RepositoryRiskResult;
  trend?: RepositoryTrendResult;
}

export type StabilityLevel = 'VERY_STABLE' | 'STABLE' | 'MODERATE' | 'UNSTABLE' | 'CRITICAL';
export type InsightSeverity = 'INFO' | 'WARNING' | 'CRITICAL';

export interface RepositoryStabilityInsight {
  title: string;
  description: string;
  severity: InsightSeverity;
}

export interface RepositoryStabilityBreakdown {
  commitStabilityScore: number;
  releaseStabilityScore: number;
  branchStabilityScore: number;
  workflowStabilityScore: number;
  deploymentStabilityScore: number;
  contributorStabilityScore: number;
  overallScore: number;
}

export interface RepositoryStabilityResult {
  breakdown: RepositoryStabilityBreakdown;
  overallScore: number;
  stabilityLevel: StabilityLevel;
  strengths: RepositoryStabilityInsight[];
  concerns: RepositoryStabilityInsight[];
  recommendations: RepositoryStabilityInsight[];
}


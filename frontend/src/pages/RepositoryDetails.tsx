import { useParams, useNavigate } from 'react-router-dom';
import { EntityHeader } from '../components/common/EntityHeader';
import { TabLayout } from '../components/common/TabLayout';
import { Button } from '../components/ui/Button';
import { ArrowLeft, Book } from 'lucide-react';
import { OverviewTab } from './tabs/OverviewTab';
import { BranchesTab } from './tabs/BranchesTab';
import { CommitsTab } from './tabs/CommitsTab';
import { PullRequestsTab } from './tabs/PullRequestsTab';
import { ReviewsTab } from './tabs/ReviewsTab';
import { ReviewCommentsTab } from './tabs/ReviewCommentsTab';
import { IssuesTab } from './tabs/IssuesTab';
import { ContributorsTab } from './tabs/ContributorsTab';
import { ReleasesTab } from './tabs/ReleasesTab';
import { CommitCommentsTab } from './tabs/CommitCommentsTab';
import { IssueCommentsTab } from './tabs/IssueCommentsTab';
import { LabelsTab } from './tabs/LabelsTab';
import { MilestonesTab } from './tabs/MilestonesTab';
import { DeploymentsTab } from './tabs/DeploymentsTab';
import { WorkflowsTab } from './tabs/WorkflowsTab';
import { WorkflowRunsTab } from './tabs/WorkflowRunsTab';
import { CollaboratorsTab } from './tabs/CollaboratorsTab';
import { RulesetsTab } from './tabs/RulesetsTab';
import { MissingCapabilityCard } from '../components/common/MissingCapabilityCard';
import { useRepoDetails } from '../api/queries';
import { Spinner } from '../components/ui/Spinner';

export function RepositoryDetails() {
  const { repo } = useParams<{ repo: string }>();
  const navigate = useNavigate();
  const { data: repoData, isLoading } = useRepoDetails(repo || '');

  if (!repo) {
    return <div>Repository not found</div>;
  }

  if (isLoading) {
    return (
      <div className="flex justify-center p-12">
        <Spinner className="w-8 h-8" />
      </div>
    );
  }

  const tabs = [
    { id: 'overview', label: 'Overview', content: <OverviewTab repoName={repo} /> },
    { id: 'branches', label: 'Branches', content: <BranchesTab repoName={repo} /> },
    { id: 'commits', label: 'Commits', content: <CommitsTab repoName={repo} /> },
    { id: 'pulls', label: 'Pull Requests', content: <PullRequestsTab repoName={repo} /> },
    { id: 'reviews', label: 'Reviews', content: <ReviewsTab repoName={repo} /> },
    { id: 'comments', label: 'Review Comments', content: <ReviewCommentsTab repoName={repo} /> },
    { id: 'commit-comments', label: 'Commit Comments', content: <CommitCommentsTab repoName={repo} /> },
    { id: 'issues', label: 'Issues', content: <IssuesTab repoName={repo} /> },
    { id: 'issue-comments', label: 'Issue Comments', content: <IssueCommentsTab repoName={repo} /> },
    { id: 'contributors', label: 'Contributors', content: <ContributorsTab repoName={repo} /> },
    { id: 'releases', label: 'Releases', content: <ReleasesTab repoName={repo} /> },
    { id: 'languages', label: 'Languages', content: <div className="p-4"><MissingCapabilityCard capability="Languages" /></div> },
    { id: 'labels', label: 'Labels', content: <div className="p-4"><LabelsTab repoName={repo} /></div> },
    { id: 'milestones', label: 'Milestones', content: <div className="p-4"><MilestonesTab repoName={repo} /></div> },
    { id: 'deployments', label: 'Deployments', content: <div className="p-4"><DeploymentsTab repoName={repo} /></div> },
    { id: 'actions', label: 'Actions', content: <div className="p-4"><WorkflowsTab repoName={repo} /></div> },
    { id: 'workflows', label: 'Workflow Runs', content: <div className="p-4"><WorkflowRunsTab repoName={repo} /></div> },
    { id: 'branch-protection', label: 'Branch Protection', content: <div className="p-4"><MissingCapabilityCard capability="Branch Protection (See Branches Tab)" /></div> },
    { id: 'collaborators', label: 'Collaborators', content: <div className="p-4"><CollaboratorsTab repoName={repo} /></div> },
    { id: 'rulesets', label: 'Rulesets', content: <div className="p-4"><RulesetsTab repoName={repo} /></div> },
  ];

  return (
    <div className="space-y-6 pb-12">
      <div>
        <Button variant="ghost" size="sm" onClick={() => navigate('/repositories')} className="-ml-2 mb-4">
          <ArrowLeft className="mr-2 h-4 w-4" /> Back to Repositories
        </Button>
        <EntityHeader
          title={repo}
          avatarUrl={`https://github.com/${repo}.png?size=200`} // Just a fallback visual
          actions={<Button variant="outline"><Book className="mr-2 h-4 w-4" /> View on GitHub</Button>}
          rawData={repoData}
        />
      </div>
      
      <TabLayout tabs={tabs} defaultTab="overview" />
    </div>
  );
}

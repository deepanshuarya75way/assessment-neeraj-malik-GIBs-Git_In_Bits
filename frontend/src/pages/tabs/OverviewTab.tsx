import { useRepoDetails } from '../../api/queries';
import { DetailCard } from '../../components/common/DetailCard';
import { RepositoryAnalysisSection } from '../../components/analysis/RepositoryAnalysisSection';
import { RepositoryStabilitySection } from '../../components/analysis/RepositoryStabilitySection';
import { RepositoryRiskSection } from '../../components/analysis/RepositoryRiskSection';
import { RepositoryTrendSection } from '../../components/analysis/RepositoryTrendSection';

export function OverviewTab({ repoName }: { repoName: string }) {
  const { data: repo, isLoading } = useRepoDetails(repoName);

  if (isLoading) return <div>Loading overview...</div>;
  if (!repo) return <div>No data</div>;

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        <DetailCard
          title="Repository Details"
          items={[
            { label: 'Name', value: repo.name },
            { label: 'Full Name', value: repo.fullName },
            { label: 'Description', value: repo.description || 'N/A' },
            { label: 'Default Branch', value: repo.defaultBranch },
            { label: 'Language', value: repo.language || 'N/A' },
            { label: 'License', value: repo.license || 'N/A' },
            { label: 'Created At', value: new Date(repo.createdAt).toLocaleString() },
            { label: 'Last Updated', value: new Date(repo.updatedAt).toLocaleString() },
          ]}
        />
        <DetailCard
          title="Metrics & Activity"
          items={[
            { label: 'Stars', value: repo.stargazersCount?.toString() || '0' },
            { label: 'Forks', value: repo.forksCount?.toString() || '0' },
            { label: 'Watchers', value: repo.watchersCount?.toString() || '0' },
            { label: 'Subscribers', value: repo.subscribersCount?.toString() || '0' },
            { label: 'Network', value: repo.networkCount?.toString() || '0' },
            { label: 'Open Issues', value: repo.openIssuesCount?.toString() || '0' },
            { label: 'Size (KB)', value: repo.size?.toString() || '0' },
          ]}
        />
        <DetailCard
          title="Links, Settings & Topics"
          items={[
            { label: 'Visibility', value: repo.visibility },
            { label: 'Archived', value: repo.archived ? 'Yes' : 'No' },
            { label: 'Disabled', value: repo.disabled ? 'Yes' : 'No' },
            { label: 'Fork', value: repo.fork ? 'Yes' : 'No' },
            { label: 'HTML URL', value: <a href={repo.htmlUrl} target="_blank" rel="noreferrer" className="text-primary hover:underline truncate inline-block max-w-[200px]">View on GitHub</a> },
            { label: 'Homepage', value: repo.homepage ? <a href={repo.homepage} target="_blank" rel="noreferrer" className="text-primary hover:underline truncate inline-block max-w-[200px]">{repo.homepage}</a> : 'N/A' },
            { label: 'Topics', value: repo.topics?.length ? repo.topics.join(', ') : 'None' },
          ]}
        />
      </div>

      <RepositoryAnalysisSection repoName={repoName} />
      <RepositoryStabilitySection repoName={repoName} />
      <RepositoryRiskSection repoName={repoName} />
      <RepositoryTrendSection repoName={repoName} />
    </div>
  );
}

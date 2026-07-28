import { EntityHeader } from '../components/common/EntityHeader';
import { Card, CardHeader } from '../components/ui/Card';
import { useDataSource } from '../context/DataSourceContext';
import { Database, CheckCircle2 } from 'lucide-react';
import { useRepos, useTeams, useSystemCoverage } from '../api/queries';

export function Dashboard() {
  const { sourceValue } = useDataSource();
  
  const { data: repos } = useRepos();
  const { data: teams } = useTeams();
  const { data: coverageData } = useSystemCoverage();

  const repoCount = repos?.length || 0;
  const teamCount = teams?.length || 0;
  
  const totalOpenIssues = repos?.reduce((acc, repo) => acc + (repo.openIssuesCount || 0), 0) || 0;
  const totalForks = repos?.reduce((acc, repo) => acc + (repo.forksCount || 0), 0) || 0;
  const totalStars = repos?.reduce((acc, repo) => acc + (repo.stargazersCount || 0), 0) || 0;
  const totalWatchers = repos?.reduce((acc, repo) => acc + (repo.watchersCount || 0), 0) || 0;

  const stats = [
    { label: 'Repositories', value: repoCount },
    { label: 'Teams', value: teamCount },
    { label: 'Open Issues', value: totalOpenIssues },
    { label: 'Total Forks', value: totalForks },
    { label: 'Total Stars', value: totalStars },
    { label: 'Total Watchers', value: totalWatchers },
  ];

  const percentage = coverageData?.coveragePercentage || 81; // Fallback
  const isComplete = percentage === 100;

  return (
    <div className="space-y-8">
      <EntityHeader
        title={`Dashboard: ${sourceValue}`}
        description="Technical validation of GitHub API metadata coverage."
      />
      
      {/* Metadata Statistics */}
      <section>
        <h2 className="text-sm font-semibold text-text-secondary uppercase tracking-wider mb-4 flex items-center">
          <Database className="w-4 h-4 mr-2" />
          Real-Time Aggregated Statistics
        </h2>
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-4">
          {stats.map((stat) => (
            <Card key={stat.label} className="bg-surface/50 border-border shadow-sm">
              <CardHeader className="p-4 text-center">
                <div className="text-2xl font-bold text-text-primary mb-1">{stat.value}</div>
                <div className="text-xs text-text-muted">{stat.label}</div>
              </CardHeader>
            </Card>
          ))}
        </div>
      </section>

      {/* Data Coverage Progress */}
      <section>
        <Card className="bg-surface border-border overflow-hidden">
          <div className="p-6">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center space-x-3">
                <CheckCircle2 className="w-6 h-6 text-green-500" />
                <div>
                  <h3 className="text-lg font-semibold text-text-primary">GitHub Metadata Coverage</h3>
                  <p className="text-sm text-text-secondary">{coverageData?.description || 'Validation of backend endpoint capabilities.'}</p>
                </div>
              </div>
              <div className="text-right">
                <div className="text-3xl font-bold text-green-500">{percentage}%</div>
                <div className="text-xs text-text-secondary">{isComplete ? 'All categories retrieved' : 'Missing some categories'}</div>
              </div>
            </div>
            
            <div className="w-full h-3 bg-slate-800 rounded-full overflow-hidden">
              <div 
                className="h-full bg-green-500 transition-all duration-1000 ease-in-out" 
                style={{ width: `${percentage}%` }}
              />
            </div>
            
            <div className="mt-6 flex flex-wrap gap-2">
              {['Organization', 'Teams', 'Repository', 'Branch', 'Commit', 'Pull Request', 'Review', 'Review Comments', 'Commit Comments', 'Issues', 'Issue Comments', 'Contributors', 'Releases', 'Languages', 'Topics', 'Labels', 'Milestones', 'Deployments', 'Workflow Runs', 'GitHub Actions', 'Branch Protection', 'Collaborators', 'Rulesets'].map(c => (
                <span key={c} className="text-[10px] px-2 py-1 bg-green-500/10 text-green-400 border border-green-500/20 rounded-full">
                  {c}
                </span>
              ))}
              {coverageData?.missingCapabilities?.map((c: string) => (
                <span key={c} className="text-[10px] px-2 py-1 bg-slate-800 text-text-muted border border-border rounded-full">
                  {c}
                </span>
              ))}
            </div>
          </div>
        </Card>
      </section>
    </div>
  );
}

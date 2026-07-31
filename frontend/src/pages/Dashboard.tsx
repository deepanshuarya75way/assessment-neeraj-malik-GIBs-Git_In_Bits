import { useState } from 'react';
import { EntityHeader } from '../components/common/EntityHeader';
import { Card, CardHeader } from '../components/ui/Card';
import { useDataSource } from '../context/DataSourceContext';
import { Database, CheckCircle2, Sparkles, RefreshCw, Briefcase, CheckSquare, AlertTriangle } from 'lucide-react';
import { useRepos, useTeams } from '../api/queries';
import { useOrganizationSummary, useGenerateOrgSummary, useOrganizationEvidence } from '../api/dashboardService';
import { Spinner } from '../components/ui/Spinner';

export function Dashboard() {
  const { sourceValue } = useDataSource();
  const [timeframe, setTimeframe] = useState('30_days');
  
  const { data: repos } = useRepos();
  const { data: teams } = useTeams();
  
  const { data: orgSummary, isLoading: summaryLoading, refetch: refetchSummary } = useOrganizationSummary(sourceValue || '', timeframe);
  const { data: orgEvidence, isLoading: evidenceLoading } = useOrganizationEvidence(sourceValue || '', timeframe);
  const { mutate: generateSummary, isPending: generating } = useGenerateOrgSummary();

  const handleGenerate = () => {
    generateSummary({ owner: sourceValue || '', timeframe }, {
      onSuccess: () => refetchSummary()
    });
  };

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


  return (
    <div className="space-y-8">
      <EntityHeader
        title={`Dashboard: ${sourceValue}`}
        description="Technical validation of GitHub API metadata coverage."
      />
      
      {/* Level 1: AI Organization Dashboard */}
      <section>
        <Card className="bg-gradient-to-r from-blue-900/40 to-indigo-900/40 border-blue-800/50 overflow-hidden relative">
          <div className="absolute top-0 right-0 p-4 opacity-10">
            <Sparkles className="w-24 h-24" />
          </div>
          <div className="p-6 relative z-10">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center space-x-2">
                <Sparkles className="w-5 h-5 text-blue-400" />
                <h2 className="text-lg font-semibold text-blue-100">AI Daily Briefing</h2>
              </div>
              <div className="flex space-x-2">
                <select 
                  className="bg-slate-800 border border-slate-700 text-sm rounded px-2 py-1 text-slate-300 focus:outline-none focus:border-blue-500"
                  value={timeframe}
                  onChange={(e) => setTimeframe(e.target.value)}
                >
                  <option value="1_day">Yesterday</option>
                  <option value="3_days">Last 3 Days</option>
                  <option value="7_days">Last 7 Days</option>
                  <option value="10_days">Last 10 Days</option>
                  <option value="30_days">Last 30 Days</option>
                </select>
                <button 
                  onClick={handleGenerate}
                  disabled={generating}
                  className="bg-blue-600 hover:bg-blue-500 text-white px-3 py-1 rounded text-sm flex items-center transition-colors disabled:opacity-50"
                >
                  {generating ? <Spinner className="w-4 h-4 mr-2" /> : <RefreshCw className="w-4 h-4 mr-2" />}
                  Generate New
                </button>
              </div>
            </div>
            
            <div className="bg-slate-900/50 p-4 rounded-lg border border-slate-700/50 min-h-[100px] flex items-center mb-6">
              {summaryLoading ? (
                <div className="w-full text-center text-slate-400 flex items-center justify-center">
                  <Spinner className="w-5 h-5 mr-2" /> Loading latest briefing...
                </div>
              ) : orgSummary ? (
                <p className="text-slate-200 text-lg leading-relaxed font-light">
                  {orgSummary.summaryText}
                </p>
              ) : (
                <p className="text-slate-400 italic text-center w-full">
                  No briefing generated yet. Click "Generate New" to run the nightly AI summary task immediately.
                </p>
              )}
            </div>

            {/* Deterministic Organization Evidence */}
            {evidenceLoading ? (
               <div className="flex justify-center py-4"><Spinner className="w-6 h-6 text-blue-500" /></div>
            ) : orgEvidence ? (
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                
                {/* Active Work */}
                <div className="bg-slate-900/40 border border-slate-700 rounded-lg p-5">
                  <h3 className="text-sm font-semibold text-blue-400 uppercase tracking-wider mb-4 flex items-center">
                    <Briefcase className="w-4 h-4 mr-2" /> Active Work
                  </h3>
                  <ul className="space-y-3">
                    {orgEvidence.activeWorkstreams.length > 0 ? orgEvidence.activeWorkstreams.map((ws, i) => (
                      <li key={i} className="text-sm text-slate-300 flex items-start">
                        <span className="w-1.5 h-1.5 rounded-full bg-blue-500 mt-1.5 mr-2 flex-shrink-0" />
                        <span className="line-clamp-2">{ws}</span>
                      </li>
                    )) : (
                      <p className="text-slate-500 text-sm italic">No active workstreams detected.</p>
                    )}
                  </ul>
                </div>

                {/* Recently Completed */}
                <div className="bg-slate-900/40 border border-slate-700 rounded-lg p-5">
                  <h3 className="text-sm font-semibold text-emerald-400 uppercase tracking-wider mb-4 flex items-center">
                    <CheckSquare className="w-4 h-4 mr-2" /> Recently Completed
                  </h3>
                  <ul className="space-y-3">
                    {orgEvidence.recentlyCompleted.length > 0 ? orgEvidence.recentlyCompleted.map((rc, i) => (
                      <li key={i} className="text-sm text-slate-300 flex items-start">
                        <CheckCircle2 className="w-4 h-4 text-emerald-500 mr-2 flex-shrink-0 mt-0.5" />
                        <span className="line-clamp-2">{rc}</span>
                      </li>
                    )) : (
                      <p className="text-slate-500 text-sm italic">No PRs merged in this period.</p>
                    )}
                  </ul>
                  <div className="mt-4 pt-4 border-t border-slate-800 flex justify-between text-xs text-slate-400">
                    <span>Total PRs Merged: <strong className="text-slate-200">{orgEvidence.totalPrsMerged}</strong></span>
                    <span>Issues Closed: <strong className="text-slate-200">{orgEvidence.totalIssuesClosed}</strong></span>
                  </div>
                </div>

                {/* Needs Attention */}
                <div className="bg-slate-900/40 border border-rose-900/50 rounded-lg p-5 relative overflow-hidden">
                  <div className="absolute top-0 right-0 w-16 h-16 bg-rose-500/5 rounded-bl-full pointer-events-none" />
                  <h3 className="text-sm font-semibold text-rose-400 uppercase tracking-wider mb-4 flex items-center">
                    <AlertTriangle className="w-4 h-4 mr-2" /> Needs Attention
                  </h3>
                  <ul className="space-y-3">
                    {orgEvidence.needsAttention.length > 0 ? orgEvidence.needsAttention.map((na, i) => (
                      <li key={i} className="text-sm text-slate-300 flex items-start">
                        <span className="text-rose-500 mr-2 font-bold flex-shrink-0">⚠</span>
                        <span className="line-clamp-2">{na}</span>
                      </li>
                    )) : (
                      <p className="text-slate-500 text-sm italic">Looking good! No major issues detected.</p>
                    )}
                  </ul>
                  <div className="mt-4 pt-4 border-t border-slate-800 text-xs text-slate-400">
                    Total CI/CD Failures: <strong className="text-rose-400">{orgEvidence.totalWorkflowFailures}</strong>
                  </div>
                </div>

              </div>
            ) : null}

          </div>
        </Card>
      </section>

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

    </div>
  );
}

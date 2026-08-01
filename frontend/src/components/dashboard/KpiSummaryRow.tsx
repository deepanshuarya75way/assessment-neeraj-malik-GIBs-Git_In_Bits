import React from 'react';
import { GitCommit, GitMerge, Users, CheckCircle2, XCircle, GitPullRequest } from 'lucide-react';
import { Card } from '../ui/Card';

interface KpiProps {
  totalCommits: number;
  totalPrsMerged: number;
  totalIssuesClosed: number;
  totalWorkflowFailures: number;
  activePrCount: number;
  activeDeveloperCount: number;
  isLoading?: boolean;
}

interface KpiTileData {
  label: string;
  value: string | number;
  icon: React.ComponentType<{ className?: string }>;
  iconClass: string;
  chipClass: string;
  description: string;
}

function KpiTile({ label, value, icon: Icon, iconClass, chipClass, description, isLoading }: KpiTileData & { isLoading?: boolean }) {
  return (
    <Card className="bg-[#1E293B]/80 border-slate-700/50 p-4 shadow-sm backdrop-blur-sm flex flex-col gap-3 group hover:border-blue-500/30 hover:bg-[#1E293B] transition-all duration-200">
      <div className="flex items-center justify-between">
        <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">{label}</span>
        <div className={`flex items-center justify-center w-7 h-7 rounded-md ${chipClass}`}>
          <Icon className={`w-3.5 h-3.5 ${iconClass}`} />
        </div>
      </div>
      <div className={`text-3xl font-extrabold text-white tracking-tight transition-all ${isLoading ? 'opacity-30 animate-pulse' : ''}`}>
        {isLoading ? '--' : value}
      </div>
      <div className="text-[11px] text-slate-500 leading-tight">{description}</div>
    </Card>
  );
}

export function KpiSummaryRow({
  totalCommits,
  totalPrsMerged,
  totalIssuesClosed,
  totalWorkflowFailures,
  activePrCount,
  activeDeveloperCount,
  isLoading,
}: KpiProps) {
  const tiles: KpiTileData[] = [
    {
      label: 'Commits',
      value: totalCommits.toLocaleString(),
      icon: GitCommit,
      iconClass: 'text-blue-400',
      chipClass: 'bg-blue-500/10',
      description: 'Total commits in this period',
    },
    {
      label: 'PRs Merged',
      value: totalPrsMerged.toLocaleString(),
      icon: GitMerge,
      iconClass: 'text-emerald-400',
      chipClass: 'bg-emerald-500/10',
      description: 'Pull requests merged',
    },
    {
      label: 'Active PRs',
      value: activePrCount.toLocaleString(),
      icon: GitPullRequest,
      iconClass: 'text-amber-400',
      chipClass: 'bg-amber-500/10',
      description: 'Currently open pull requests',
    },
    {
      label: 'Issues Closed',
      value: totalIssuesClosed.toLocaleString(),
      icon: CheckCircle2,
      iconClass: 'text-teal-400',
      chipClass: 'bg-teal-500/10',
      description: 'Issues resolved this period',
    },
    {
      label: 'CI/CD Failures',
      value: totalWorkflowFailures.toLocaleString(),
      icon: XCircle,
      iconClass: totalWorkflowFailures > 0 ? 'text-rose-400' : 'text-slate-400',
      chipClass: totalWorkflowFailures > 0 ? 'bg-rose-500/10' : 'bg-slate-500/10',
      description: 'Failed workflow runs',
    },
    {
      label: 'Active Devs',
      value: activeDeveloperCount.toLocaleString(),
      icon: Users,
      iconClass: 'text-indigo-400',
      chipClass: 'bg-indigo-500/10',
      description: 'Contributors in the org',
    },
  ];

  return (
    <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
      {tiles.map((tile, idx) => (
        <KpiTile key={idx} {...tile} isLoading={isLoading} />
      ))}
    </div>
  );
}

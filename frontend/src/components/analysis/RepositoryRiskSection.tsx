import React from 'react';
import { useRepositoryAnalysis } from '../../api/repositoryAnalysisService';
import { Card, CardContent } from '../ui/Card';
import { Skeleton } from '../ui/Skeleton';
import { RepositoryRiskCard } from './RepositoryRiskCard';
import { RiskBreakdown } from './RiskBreakdown';

interface RepositoryRiskSectionProps {
  repoName: string;
}

export const RepositoryRiskSection: React.FC<RepositoryRiskSectionProps> = ({ repoName }) => {
  const { data: report, isLoading, isError, refetch } = useRepositoryAnalysis(repoName);

  if (isLoading) {
    return (
      <div className="mt-8 space-y-4">
        <h3 className="text-lg font-bold text-text-primary tracking-tight">Repository Risk</h3>
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-3 lg:grid-cols-3">
          <Skeleton className="h-44 w-full" />
          <Skeleton className="h-44 w-full sm:col-span-2" />
        </div>
      </div>
    );
  }

  const risk = report?.risk;

  if (isError || !risk) {
    return (
      <div className="mt-8 space-y-4">
        <h3 className="text-lg font-bold text-text-primary tracking-tight">Repository Risk</h3>
        <Card className="border-border bg-surface/50 p-6 text-center">
          <CardContent className="space-y-3 py-4 flex flex-col items-center">
            <p className="font-semibold text-text-primary">Risk analysis unavailable.</p>
            <p className="text-sm text-text-secondary">
              There was an error fetching the risk analysis for <span className="font-mono">{repoName}</span>.
            </p>
            <button
              onClick={() => refetch()}
              className="mt-2 px-4 py-2 bg-primary/10 hover:bg-primary/20 text-primary rounded-md text-sm font-medium transition-colors"
            >
              Retry
            </button>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="mt-8 space-y-6">
      <div className="border-b border-border/80 pb-3">
        <h3 className="text-lg font-bold text-text-primary tracking-tight flex items-center gap-2">
          <span>Repository Risk</span>
          <span className="text-xs font-normal text-text-secondary px-2 py-0.5 bg-border/40 rounded-full">
            Live Analysis Dashboard
          </span>
        </h3>
      </div>

      <div className="grid grid-cols-1 gap-6 sm:grid-cols-3 lg:grid-cols-3">
        <RepositoryRiskCard
          overallRisk={risk.overallRisk}
          riskLevel={risk.riskLevel}
        />
        <RiskBreakdown breakdown={risk.breakdown} />
      </div>
    </div>
  );
};

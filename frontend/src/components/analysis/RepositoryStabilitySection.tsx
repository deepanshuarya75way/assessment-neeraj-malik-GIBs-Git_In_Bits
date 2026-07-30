import React from 'react';
import { useRepositoryStability } from '../../api/repositoryAnalysisService';
import { Card, CardContent } from '../ui/Card';
import { Skeleton } from '../ui/Skeleton';
import { RepositoryStabilityCard } from './RepositoryStabilityCard';
import { StabilityBreakdown } from './StabilityBreakdown';
import { StabilityInsightsCard } from './StabilityInsightsCard';

interface RepositoryStabilitySectionProps {
  repoName: string;
}

export const RepositoryStabilitySection: React.FC<RepositoryStabilitySectionProps> = ({ repoName }) => {
  const { data: stability, isLoading, isError, refetch } = useRepositoryStability(repoName);

  if (isLoading) {
    return (
      <div className="mt-8 space-y-4">
        <h3 className="text-lg font-bold text-text-primary tracking-tight">Repository Stability</h3>
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-3 lg:grid-cols-3">
          <Skeleton className="h-44 w-full" />
          <Skeleton className="h-44 w-full sm:col-span-2" />
          <Skeleton className="h-40 w-full sm:col-span-3" />
        </div>
      </div>
    );
  }

  if (isError || !stability) {
    return (
      <div className="mt-8 space-y-4">
        <h3 className="text-lg font-bold text-text-primary tracking-tight">Repository Stability</h3>
        <Card className="border-border bg-surface/50 p-6 text-center">
          <CardContent className="space-y-3 py-4 flex flex-col items-center">
            <p className="font-semibold text-text-primary">Unable to load Repository Stability Analysis.</p>
            <p className="text-sm text-text-secondary">
              There was an error fetching the analysis for <span className="font-mono">{repoName}</span>.
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

  // Handle empty state gracefully
  if (stability.overallScore === 0 && !stability.strengths?.length && !stability.concerns?.length) {
    return (
      <div className="mt-8 space-y-4">
        <h3 className="text-lg font-bold text-text-primary tracking-tight">Repository Stability</h3>
        <Card className="border-border bg-surface/50 p-6 text-center">
          <CardContent className="space-y-2 py-4 flex flex-col items-center">
            <p className="font-semibold text-text-primary">Not enough repository history to calculate stability.</p>
            <p className="text-sm text-text-secondary">
              This repository may be completely empty, brand new, or missing enough data to derive stability insights.
            </p>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="mt-8 space-y-6">
      <div className="border-b border-border/80 pb-3">
        <h3 className="text-lg font-bold text-text-primary tracking-tight flex items-center gap-2">
          <span>Repository Stability</span>
          <span className="text-xs font-normal text-text-secondary px-2 py-0.5 bg-border/40 rounded-full">
            Live Analysis Dashboard
          </span>
        </h3>
      </div>

      <div className="grid grid-cols-1 gap-6 sm:grid-cols-3 lg:grid-cols-3">
        <RepositoryStabilityCard
          overallScore={stability.overallScore}
          stabilityLevel={stability.stabilityLevel}
        />
        <StabilityBreakdown breakdown={stability.breakdown} />
        <StabilityInsightsCard stability={stability} />
      </div>
    </div>
  );
};

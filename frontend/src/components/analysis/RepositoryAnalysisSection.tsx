import React from 'react';
import { useRepositoryAnalysis } from '../../api/repositoryAnalysisService';
import { Card, CardContent } from '../ui/Card';
import { Skeleton } from '../ui/Skeleton';
import { RepositoryHealthCard } from './RepositoryHealthCard';
import { HealthBreakdown } from './HealthBreakdown';
import { AnalysisSummaryCard } from './AnalysisSummaryCard';
import { ScoreRadarCard } from './ScoreRadarCard';

interface RepositoryAnalysisSectionProps {
  repoName: string;
}

export const RepositoryAnalysisSection: React.FC<RepositoryAnalysisSectionProps> = ({ repoName }) => {
  const { data: report, isLoading, isError } = useRepositoryAnalysis(repoName);

  if (isLoading) {
    return (
      <div className="mt-8 space-y-4">
        <h3 className="text-lg font-bold text-text-primary tracking-tight">Repository Analysis</h3>
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
          <Skeleton className="h-44 w-full" />
          <Skeleton className="h-44 w-full sm:col-span-2" />
          <Skeleton className="h-40 w-full sm:col-span-2" />
          <Skeleton className="h-40 w-full" />
        </div>
      </div>
    );
  }

  if (isError || !report || !report.health) {
    return (
      <div className="mt-8 space-y-4">
        <h3 className="text-lg font-bold text-text-primary tracking-tight">Repository Analysis</h3>
        <Card className="border-border bg-surface/50 p-6 text-center">
          <CardContent className="space-y-2 py-4">
            <p className="font-semibold text-text-primary">Analysis unavailable</p>
            <p className="text-sm text-text-secondary">
              Unable to calculate repository health for <span className="font-mono">{repoName}</span>.
            </p>
          </CardContent>
        </Card>
      </div>
    );
  }

  const { health } = report;

  return (
    <div className="mt-8 space-y-6">
      <div className="border-b border-border/80 pb-3">
        <h3 className="text-lg font-bold text-text-primary tracking-tight flex items-center gap-2">
          <span>Repository Analysis</span>
          <span className="text-xs font-normal text-text-secondary px-2 py-0.5 bg-border/40 rounded-full">
            Executive Dashboard
          </span>
        </h3>
      </div>

      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        <RepositoryHealthCard
          overallScore={health.overallScore}
          healthLevel={health.healthLevel}
        />
        <HealthBreakdown breakdown={health.breakdown} />
        <AnalysisSummaryCard
          healthLevel={health.healthLevel}
          findings={health.findings || []}
        />
        <ScoreRadarCard />
      </div>
    </div>
  );
};

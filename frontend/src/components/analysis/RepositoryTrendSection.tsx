import React from 'react';
import { useRepositoryAnalysis } from '../../api/repositoryAnalysisService';
import { Card, CardHeader, CardTitle, CardContent } from '../ui/Card';
import { Skeleton } from '../ui/Skeleton';
import { Badge } from '../ui/Badge';
import { TrendLineChart } from './TrendLineChart';
import { TrendBreakdown } from './TrendBreakdown';
import type { TrendDirection } from '../../types';

interface RepositoryTrendSectionProps {
  repoName: string;
}

export const RepositoryTrendSection: React.FC<RepositoryTrendSectionProps> = ({ repoName }) => {
  const { data: report, isLoading, isError, refetch } = useRepositoryAnalysis(repoName);

  if (isLoading) {
    return (
      <div className="mt-12 space-y-6">
        <h3 className="text-xl font-bold text-text-primary tracking-tight">Repository Trend</h3>
        <Skeleton className="h-96 w-full rounded-xl" />
      </div>
    );
  }

  const trend = report?.trend;

  if (isError || !trend) {
    return (
      <div className="mt-12 space-y-6">
        <h3 className="text-xl font-bold text-text-primary tracking-tight">Repository Trend</h3>
        <Card className="border-border bg-surface/50 p-8 text-center rounded-xl">
          <CardContent className="space-y-4 py-6 flex flex-col items-center">
            <p className="text-lg font-semibold text-text-primary">Trend analysis unavailable.</p>
            <p className="text-sm text-text-secondary max-w-md text-center">
              There was an error fetching the historical trend analysis for <span className="font-mono">{repoName}</span>.
            </p>
            <button
              onClick={() => refetch()}
              className="mt-4 px-5 py-2.5 bg-primary/10 hover:bg-primary/20 text-primary rounded-lg text-sm font-medium transition-colors"
            >
              Retry
            </button>
          </CardContent>
        </Card>
      </div>
    );
  }

  const getBadgeVariant = (dir: TrendDirection) => {
    switch (dir) {
      case 'IMPROVING': return 'success';
      case 'STABLE': return 'warning';
      case 'DECLINING': return 'danger';
      default: return 'outline';
    }
  };

  const getTrendIcon = (dir: TrendDirection) => {
    switch (dir) {
      case 'IMPROVING': return '⬆';
      case 'STABLE': return '➡';
      case 'DECLINING': return '⬇';
      default: return '';
    }
  };

  const getTrendDescription = (dir: TrendDirection) => {
    switch (dir) {
      case 'IMPROVING': return 'Engineering activity has steadily improved over recent iterations.';
      case 'STABLE': return 'Engineering activity remains stable and consistent over time.';
      case 'DECLINING': return 'Engineering activity has shown a notable decline recently.';
      default: return 'No trend data available.';
    }
  };

  // Mock generic insights (Backend deterministic insights placeholder)
  const getTrendDrivers = (dir: TrendDirection) => {
    if (dir === 'IMPROVING') {
      return [
        "Merge velocity has steadily improved",
        "Code review participation is increasing",
        "Contributor activity is healthy and distributed"
      ];
    }
    if (dir === 'DECLINING') {
      return [
        "Issue backlog is growing faster than resolution",
        "Merge velocity has slowed down",
        "Review participation dropped recently"
      ];
    }
    return [
      "Merge and issue velocity remain balanced",
      "Release cadence is highly consistent"
    ];
  };

  const drivers = getTrendDrivers(trend.direction);

  return (
    <div className="mt-12 space-y-6">
      <div className="border-b border-border/80 pb-4">
        <h3 className="text-xl font-bold text-text-primary tracking-tight flex items-center gap-3">
          <span>Repository Trend</span>
          <span className="text-xs font-normal text-text-secondary px-2.5 py-1 bg-border/40 rounded-full">
            Time-Series Analytics
          </span>
        </h3>
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-3 gap-6">
        
        {/* Left Column: Overview Header & Chart (Spans 2 columns on XL) */}
        <div className="xl:col-span-2 space-y-6 flex flex-col">
          
          <Card className="bg-gradient-to-br from-surface via-surface to-primary/5 border-border/80 shadow-md">
            <CardContent className="p-6">
              <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div className="flex items-center gap-4">
                  <div className="flex items-baseline gap-2">
                    <span className="text-6xl font-extrabold tracking-tight text-text-primary">
                      {trend.overallTrend}
                    </span>
                    <span className="text-xl font-medium text-text-secondary">/ 100</span>
                  </div>
                  <div className="hidden sm:block h-12 w-px bg-border/60 mx-2"></div>
                  <div>
                    <div className="flex items-center gap-2 mb-1">
                      <Badge variant={getBadgeVariant(trend.direction)} className="text-sm px-2 py-0.5 shadow-sm">
                        {getTrendIcon(trend.direction)} {trend.direction}
                      </Badge>
                    </div>
                    <p className="text-sm text-text-secondary">
                      {getTrendDescription(trend.direction)}
                    </p>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="flex-1 shadow-sm border-border/80 bg-surface">
            <CardHeader className="pb-3 border-b border-border/40">
              <CardTitle className="text-sm font-semibold tracking-wide uppercase text-text-secondary">
                Overall Trend Line
              </CardTitle>
            </CardHeader>
            <CardContent className="pt-6">
              {/* Empty array passed intentionally as backend does not yet supply historical snapshots */}
              <TrendLineChart data={[]} />
            </CardContent>
          </Card>
          
        </div>

        {/* Right Column: Breakdown & Drivers (Spans 1 column on XL) */}
        <div className="xl:col-span-1 space-y-6 flex flex-col">
          
          <TrendBreakdown breakdown={trend.breakdown} />
          
          <Card className="bg-surface shadow-sm border-border/80">
            <CardHeader className="pb-3 border-b border-border/40">
              <CardTitle className="text-sm font-semibold tracking-wide uppercase text-text-secondary">
                What's Driving the Trend
              </CardTitle>
            </CardHeader>
            <CardContent className="pt-4">
              {drivers.length === 0 ? (
                <p className="text-sm text-text-secondary italic">Not enough historical insights yet.</p>
              ) : (
                <ul className="space-y-3">
                  {drivers.map((driver, idx) => (
                    <li key={idx} className="flex items-start gap-3">
                      <span className="text-green-500 shrink-0 mt-0.5">✓</span>
                      <span className="text-sm text-text-primary leading-tight">{driver}</span>
                    </li>
                  ))}
                </ul>
              )}
            </CardContent>
          </Card>

        </div>
      </div>
    </div>
  );
};

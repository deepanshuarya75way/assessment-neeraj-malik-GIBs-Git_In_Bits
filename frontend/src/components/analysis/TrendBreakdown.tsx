import React from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '../ui/Card';
import { TrendMetricRow } from './TrendMetricRow';
import type { RepositoryTrendBreakdown as BreakdownType } from '../../types';

interface TrendBreakdownProps {
  breakdown: BreakdownType;
}

export const TrendBreakdown: React.FC<TrendBreakdownProps> = ({ breakdown }) => {
  return (
    <Card className="flex flex-col h-full bg-surface shadow-sm">
      <CardHeader className="pb-3 border-b border-border/40">
        <CardTitle className="text-sm font-semibold tracking-wide uppercase text-text-secondary">
          Engineering Activity Trends
        </CardTitle>
      </CardHeader>
      <CardContent className="pt-4 space-y-2">
        <TrendMetricRow 
          label="Commit Activity" 
          score={breakdown.commitTrend} 
          tooltip="Indicates whether development activity is increasing or decreasing over time." 
        />
        <TrendMetricRow 
          label="Pull Request Activity" 
          score={breakdown.pullRequestTrend} 
          tooltip="Reflects changes in pull request throughput and merge activity." 
        />
        <TrendMetricRow 
          label="Issue Trend" 
          score={breakdown.issueTrend} 
          tooltip="Shows whether the issue backlog is improving or growing." 
        />
        <TrendMetricRow 
          label="Review Participation" 
          score={breakdown.reviewTrend} 
          tooltip="Indicates how code review participation has evolved." 
        />
        <TrendMetricRow 
          label="Contributor Activity" 
          score={breakdown.contributorTrend} 
          tooltip="Reflects changes in contributor engagement and collaboration." 
        />
        <TrendMetricRow 
          label="Release Cadence" 
          score={breakdown.releaseTrend} 
          tooltip="Measures whether release cadence is becoming more consistent." 
        />
      </CardContent>
    </Card>
  );
};

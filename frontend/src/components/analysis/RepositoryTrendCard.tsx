import React from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '../ui/Card';
import { Badge } from '../ui/Badge';
import type { TrendDirection } from '../../types';

interface RepositoryTrendCardProps {
  overallTrend: number;
  direction: TrendDirection;
}

export const RepositoryTrendCard: React.FC<RepositoryTrendCardProps> = ({
  overallTrend,
  direction,
}) => {
  const getBadgeVariant = (dir: TrendDirection) => {
    switch (dir) {
      case 'IMPROVING':
        return 'success';
      case 'STABLE':
        return 'warning';
      case 'DECLINING':
        return 'danger';
      default:
        return 'outline';
    }
  };

  const getCustomBadgeClass = (dir: TrendDirection) => {
    if (dir === 'STABLE') {
      return 'border-transparent bg-yellow-500/10 text-yellow-500 hover:bg-yellow-500/20';
    }
    return '';
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
      case 'IMPROVING': return 'Engineering activity is improving over time.';
      case 'STABLE': return 'Engineering activity is stable.';
      case 'DECLINING': return 'Engineering activity is declining over time.';
      default: return 'No trend data available.';
    }
  };

  return (
    <Card className="flex flex-col justify-between overflow-hidden bg-gradient-to-br from-surface via-surface to-primary/5 border-border/80 shadow-md h-full">
      <CardHeader className="pb-2">
        <div className="flex items-center justify-between">
          <CardTitle className="text-base font-semibold text-text-secondary uppercase tracking-wider">
            Overall Trend
          </CardTitle>
          <Badge variant={getBadgeVariant(direction)} className={getCustomBadgeClass(direction)}>
            {getTrendIcon(direction)} {direction}
          </Badge>
        </div>
      </CardHeader>
      <CardContent className="pt-4 pb-6 flex flex-col gap-2">
        <div className="flex items-baseline gap-2">
          <span className="text-5xl font-extrabold tracking-tight text-text-primary">
            {overallTrend}
          </span>
          <span className="text-xl font-medium text-text-secondary">/ 100</span>
        </div>
        <p className="text-sm text-text-secondary mt-1">
          {getTrendDescription(direction)}
        </p>
      </CardContent>
    </Card>
  );
};

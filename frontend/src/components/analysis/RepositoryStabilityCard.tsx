import React from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '../ui/Card';
import { Badge } from '../ui/Badge';
import type { StabilityLevel } from '../../types';

interface RepositoryStabilityCardProps {
  overallScore: number;
  stabilityLevel: StabilityLevel;
}

export const RepositoryStabilityCard: React.FC<RepositoryStabilityCardProps> = ({
  overallScore,
  stabilityLevel,
}) => {
  const getBadgeVariant = (level: StabilityLevel) => {
    switch (level) {
      case 'VERY_STABLE':
        return 'success';
      case 'STABLE':
        return 'default'; // Or custom blue
      case 'MODERATE':
        return 'warning';
      case 'CRITICAL':
        return 'danger';
      default:
        return 'outline';
    }
  };

  const getCustomBadgeClass = (level: StabilityLevel) => {
    if (level === 'UNSTABLE') {
      return 'border-transparent bg-orange-500/10 text-orange-500 hover:bg-orange-500/20';
    }
    if (level === 'STABLE') {
      return 'border-transparent bg-blue-500/10 text-blue-500 hover:bg-blue-500/20';
    }
    return '';
  };

  return (
    <Card className="flex flex-col justify-between overflow-hidden bg-gradient-to-br from-surface via-surface to-primary/5 border-border/80 shadow-md">
      <CardHeader className="pb-2">
        <div className="flex items-center justify-between">
          <CardTitle className="text-base font-semibold text-text-secondary uppercase tracking-wider">
            Overall Stability
          </CardTitle>
          <Badge variant={getBadgeVariant(stabilityLevel)} className={getCustomBadgeClass(stabilityLevel)}>
            {stabilityLevel}
          </Badge>
        </div>
      </CardHeader>
      <CardContent className="pt-4 pb-6 flex items-baseline gap-2">
        <span className="text-5xl font-extrabold tracking-tight text-text-primary">
          {overallScore}
        </span>
        <span className="text-xl font-medium text-text-secondary">/ 100</span>
      </CardContent>
    </Card>
  );
};

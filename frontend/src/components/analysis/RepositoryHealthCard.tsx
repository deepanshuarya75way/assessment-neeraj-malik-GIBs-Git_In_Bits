import React from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '../ui/Card';
import { Badge } from '../ui/Badge';
import type { HealthLevel } from '../../types';

interface RepositoryHealthCardProps {
  overallScore: number;
  healthLevel: HealthLevel;
}

export const RepositoryHealthCard: React.FC<RepositoryHealthCardProps> = ({
  overallScore,
  healthLevel,
}) => {
  const getBadgeVariant = (level: HealthLevel) => {
    switch (level) {
      case 'EXCELLENT':
      case 'GOOD':
        return 'success';
      case 'FAIR':
        return 'warning';
      case 'CRITICAL':
        return 'danger';
      default:
        return 'outline';
    }
  };

  const getCustomBadgeClass = (level: HealthLevel) => {
    if (level === 'POOR') {
      return 'border-transparent bg-orange-500/10 text-orange-500 hover:bg-orange-500/20';
    }
    return '';
  };

  return (
    <Card className="flex flex-col justify-between overflow-hidden bg-gradient-to-br from-surface via-surface to-primary/5 border-border/80 shadow-md">
      <CardHeader className="pb-2">
        <div className="flex items-center justify-between">
          <CardTitle className="text-base font-semibold text-text-secondary uppercase tracking-wider">
            Overall Health Score
          </CardTitle>
          <Badge variant={getBadgeVariant(healthLevel)} className={getCustomBadgeClass(healthLevel)}>
            {healthLevel}
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

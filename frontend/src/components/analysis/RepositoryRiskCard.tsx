import React from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '../ui/Card';
import { Badge } from '../ui/Badge';
import type { HealthLevel } from '../../types';

interface RepositoryRiskCardProps {
  overallRisk: number;
  riskLevel: HealthLevel;
}

export const RepositoryRiskCard: React.FC<RepositoryRiskCardProps> = ({
  overallRisk,
  riskLevel,
}) => {
  // Risk acts inverse to Health. High risk -> Red, Low risk -> Green
  const getBadgeVariant = (level: HealthLevel) => {
    switch (level) {
      case 'EXCELLENT':
        return 'success'; // 0-20 Very Low Risk
      case 'GOOD':
        return 'success'; // 21-40 Low Risk
      case 'FAIR':
        return 'warning'; // 41-60 Moderate Risk
      case 'POOR':
        return 'warning'; // 61-80 High Risk (custom mapped below)
      case 'CRITICAL':
        return 'danger'; // 81-100 Critical Risk
      default:
        return 'outline';
    }
  };

  const getCustomBadgeClass = (level: HealthLevel) => {
    if (level === 'POOR') {
      return 'border-transparent bg-orange-500/10 text-orange-500 hover:bg-orange-500/20';
    }
    if (level === 'GOOD') {
      return 'border-transparent bg-green-400/10 text-green-400 hover:bg-green-400/20'; // Light green
    }
    return '';
  };

  const getRiskLabel = (level: HealthLevel) => {
    switch (level) {
      case 'EXCELLENT': return 'VERY LOW RISK';
      case 'GOOD': return 'LOW RISK';
      case 'FAIR': return 'MODERATE RISK';
      case 'POOR': return 'HIGH RISK';
      case 'CRITICAL': return 'CRITICAL RISK';
      default: return level;
    }
  };

  return (
    <Card className="flex flex-col justify-between overflow-hidden bg-gradient-to-br from-surface via-surface to-primary/5 border-border/80 shadow-md h-full">
      <CardHeader className="pb-2">
        <div className="flex items-center justify-between">
          <CardTitle className="text-base font-semibold text-text-secondary uppercase tracking-wider">
            Overall Risk
          </CardTitle>
          <Badge variant={getBadgeVariant(riskLevel)} className={getCustomBadgeClass(riskLevel)}>
            {getRiskLabel(riskLevel)}
          </Badge>
        </div>
      </CardHeader>
      <CardContent className="pt-4 pb-6 flex items-baseline gap-2">
        <span className="text-5xl font-extrabold tracking-tight text-text-primary">
          {overallRisk}
        </span>
        <span className="text-xl font-medium text-text-secondary">/ 100</span>
      </CardContent>
    </Card>
  );
};

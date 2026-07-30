import React from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '../ui/Card';
import { HealthProgressBar } from './HealthProgressBar';
import type { RepositoryRiskBreakdown as BreakdownType } from '../../types';

interface RiskBreakdownProps {
  breakdown: BreakdownType;
}

export const RiskBreakdown: React.FC<RiskBreakdownProps> = ({ breakdown }) => {
  return (
    <Card className="col-span-1 sm:col-span-2 flex flex-col justify-between h-full">
      <CardHeader>
        <CardTitle className="text-base font-semibold">Risk Breakdown</CardTitle>
      </CardHeader>
      <CardContent className="space-y-5">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-4">
          <div title="Estimates engineering risk based on commit activity, size, and verification.">
            <HealthProgressBar label="Commit Risk" score={breakdown.commitRisk} inverseColors={true} />
          </div>
          <div title="Reflects repository protection and branch governance.">
            <HealthProgressBar label="Branch Risk" score={breakdown.branchRisk} inverseColors={true} />
          </div>
          <div title="Estimates risk introduced by open, stale, or oversized pull requests.">
            <HealthProgressBar label="Pull Request Risk" score={breakdown.pullRequestRisk} inverseColors={true} />
          </div>
          <div title="Indicates the level of review discipline within the repository.">
            <HealthProgressBar label="Review Risk" score={breakdown.reviewRisk} inverseColors={true} />
          </div>
          <div title="Measures engineering risk created by unresolved issues.">
            <HealthProgressBar label="Issue Risk" score={breakdown.issueRisk} inverseColors={true} />
          </div>
          <div title="Indicates release maturity and cadence.">
            <HealthProgressBar label="Release Risk" score={breakdown.releaseRisk} inverseColors={true} />
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

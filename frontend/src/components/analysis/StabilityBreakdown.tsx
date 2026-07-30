import React from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '../ui/Card';
import { HealthProgressBar } from './HealthProgressBar';
import type { RepositoryStabilityBreakdown as BreakdownType } from '../../types';

interface StabilityBreakdownProps {
  breakdown: BreakdownType;
}

export const StabilityBreakdown: React.FC<StabilityBreakdownProps> = ({ breakdown }) => {
  return (
    <Card className="col-span-1 sm:col-span-2 flex flex-col justify-between">
      <CardHeader>
        <CardTitle className="text-base font-semibold">Stability Breakdown</CardTitle>
      </CardHeader>
      <CardContent className="space-y-5">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-4">
          <HealthProgressBar label="Commit Stability" score={breakdown.commitStabilityScore} />
          <HealthProgressBar label="Branch Stability" score={breakdown.branchStabilityScore} />
          <HealthProgressBar label="Release Stability" score={breakdown.releaseStabilityScore} />
          <HealthProgressBar label="Workflow Stability" score={breakdown.workflowStabilityScore} />
          <HealthProgressBar label="Deployment Stability" score={breakdown.deploymentStabilityScore} />
          <HealthProgressBar label="Contributor Stability" score={breakdown.contributorStabilityScore} />
        </div>
      </CardContent>
    </Card>
  );
};

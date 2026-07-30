import React from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { HealthProgressBar } from './HealthProgressBar';
import type { RepositoryHealthBreakdown as BreakdownType } from '../../types';

interface HealthBreakdownProps {
  breakdown: BreakdownType;
}

export const HealthBreakdown: React.FC<HealthBreakdownProps> = ({ breakdown }) => {
  const statusChips: string[] = [];

  if (breakdown.overallScore >= 75) {
    statusChips.push('Repository Healthy');
  } else if (breakdown.overallScore >= 60) {
    statusChips.push('Repository Stable');
  } else {
    statusChips.push('Attention Required');
  }

  if (breakdown.reviewScore >= 80) {
    statusChips.push('Review Process Strong');
  } else if (breakdown.reviewScore < 60) {
    statusChips.push('Review Process Needs Work');
  }

  if (breakdown.branchScore >= 80) {
    statusChips.push('Protected Branches');
  } else if (breakdown.branchScore < 60) {
    statusChips.push('Branch Protection Low');
  }

  if (breakdown.contributorScore >= 75) {
    statusChips.push('Contributor Activity Good');
  } else if (breakdown.contributorScore < 50) {
    statusChips.push('Low Engagement');
  }

  if (breakdown.pullRequestScore >= 80) {
    statusChips.push('Healthy PR Flow');
  }

  return (
    <Card className="col-span-1 sm:col-span-2 flex flex-col justify-between">
      <CardHeader>
        <CardTitle className="text-base font-semibold">Health Breakdown</CardTitle>
      </CardHeader>
      <CardContent className="space-y-5">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-4">
          <HealthProgressBar label="Commit Health" score={breakdown.commitScore} />
          <HealthProgressBar label="PR Health" score={breakdown.pullRequestScore} />
          <HealthProgressBar label="Issue Health" score={breakdown.issueScore} />
          <HealthProgressBar label="Review Health" score={breakdown.reviewScore} />
          <HealthProgressBar label="Branch Health" score={breakdown.branchScore} />
          <HealthProgressBar label="Contributor Health" score={breakdown.contributorScore} />
        </div>

        {statusChips.length > 0 && (
          <div className="pt-4 border-t border-border/50 flex flex-wrap gap-2 items-center">
            <span className="text-xs font-medium text-text-secondary mr-1">Status:</span>
            {statusChips.map((chip, idx) => (
              <Badge
                key={idx}
                variant="secondary"
                className="bg-primary/10 text-primary border-primary/20 font-normal py-1 px-2.5"
              >
                {chip}
              </Badge>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  );
};

import React from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '../ui/Card';

export const ScoreRadarCard: React.FC = () => {
  return (
    <Card className="flex flex-col justify-between border-dashed border-border/70 bg-surface/50">
      <CardHeader>
        <CardTitle className="text-base font-semibold text-text-primary">Score Radar</CardTitle>
      </CardHeader>
      <CardContent className="flex-1 flex flex-col items-center justify-center text-center py-8 space-y-3">
        <div className="w-16 h-16 rounded-full bg-primary/10 border border-primary/20 flex items-center justify-center text-primary font-bold text-lg">
          📊
        </div>
        <div className="space-y-1">
          <p className="font-medium text-text-primary">Radar visualization</p>
          <p className="text-xs text-text-secondary">Coming in next phase.</p>
        </div>
        <p className="text-[11px] text-text-secondary max-w-[200px]">
          Later Commit, Review, Issues, Contributors, Branches, and PR will populate this multi-axis chart.
        </p>
      </CardContent>
    </Card>
  );
};

import React from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '../ui/Card';
import { CheckCircle2, AlertTriangle, Lightbulb } from 'lucide-react';
import type { RepositoryStabilityResult } from '../../types';

interface StabilityInsightsCardProps {
  stability: RepositoryStabilityResult;
}

export const StabilityInsightsCard: React.FC<StabilityInsightsCardProps> = ({ stability }) => {
  return (
    <Card className="col-span-1 sm:col-span-3 flex flex-col justify-between">
      <CardHeader>
        <CardTitle className="text-base font-semibold">Stability Insights</CardTitle>
      </CardHeader>
      <CardContent className="space-y-6">
        
        {stability.strengths && stability.strengths.length > 0 && (
          <div className="space-y-2">
            <h4 className="text-sm font-semibold text-text-primary flex items-center gap-2">
              Strengths
            </h4>
            <div className="space-y-2">
              {stability.strengths.map((insight, idx) => (
                <div key={idx} className="flex items-start gap-2 bg-green-500/5 p-3 rounded-md border border-green-500/10">
                  <CheckCircle2 className="w-4 h-4 text-green-500 mt-0.5 flex-shrink-0" />
                  <div>
                    <p className="text-sm font-medium text-text-primary">{insight.title}</p>
                    <p className="text-xs text-text-secondary mt-0.5">{insight.description}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {stability.concerns && stability.concerns.length > 0 && (
          <div className="space-y-2">
            <h4 className="text-sm font-semibold text-text-primary flex items-center gap-2">
              Concerns
            </h4>
            <div className="space-y-2">
              {stability.concerns.map((insight, idx) => (
                <div key={idx} className="flex items-start gap-2 bg-red-500/5 p-3 rounded-md border border-red-500/10">
                  <AlertTriangle className="w-4 h-4 text-red-500 mt-0.5 flex-shrink-0" />
                  <div>
                    <p className="text-sm font-medium text-text-primary">{insight.title}</p>
                    <p className="text-xs text-text-secondary mt-0.5">{insight.description}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {stability.recommendations && stability.recommendations.length > 0 && (
          <div className="space-y-2">
            <h4 className="text-sm font-semibold text-text-primary flex items-center gap-2">
              Recommendations
            </h4>
            <div className="space-y-2">
              {stability.recommendations.map((insight, idx) => (
                <div key={idx} className="flex items-start gap-2 bg-blue-500/5 p-3 rounded-md border border-blue-500/10">
                  <Lightbulb className="w-4 h-4 text-blue-500 mt-0.5 flex-shrink-0" />
                  <div>
                    <p className="text-sm font-medium text-text-primary">{insight.title}</p>
                    <p className="text-xs text-text-secondary mt-0.5">{insight.description}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  );
};

import React from 'react';
import { Card, CardContent } from '../ui/Card';
import { Badge } from '../ui/Badge';

export const FutureAnalysisCard: React.FC = () => {
  const futureModules = [
    { title: 'Risk Analysis', desc: 'Predictive vulnerability and code churn risk scoring.' },
    { title: 'Stability Analysis', desc: 'Build pass rates, release frequency, and regression metrics.' },
    { title: 'AI Summary', desc: 'Automated executive narrative generated from deep repository insights.' },
    { title: 'Repository Trends', desc: 'Historical velocity trajectories and team health evolution over time.' },
  ];

  return (
    <div className="space-y-3 pt-2">
      <h4 className="text-sm font-semibold text-text-secondary uppercase tracking-wider">
        Future Intelligence
      </h4>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {futureModules.map((mod, idx) => (
          <Card
            key={idx}
            className="opacity-70 bg-surface/40 border-border/50 hover:opacity-90 transition-opacity cursor-not-allowed"
          >
            <CardContent className="p-4 flex flex-col justify-between h-full space-y-3">
              <div className="flex items-start justify-between gap-2">
                <span className="font-medium text-sm text-text-primary">{mod.title}</span>
                <Badge variant="outline" className="text-[10px] px-2 py-0 border-border bg-background/50">
                  Coming Soon
                </Badge>
              </div>
              <p className="text-xs text-text-secondary leading-normal">{mod.desc}</p>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
};

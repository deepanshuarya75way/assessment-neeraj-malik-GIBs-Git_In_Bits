import React from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '../ui/Card';
import { CheckCircle2, AlertTriangle, ArrowRight } from 'lucide-react';
import type { HealthLevel, HealthFinding } from '../../types';

interface AnalysisSummaryCardProps {
  healthLevel: HealthLevel;
  findings: HealthFinding[];
}

export const AnalysisSummaryCard: React.FC<AnalysisSummaryCardProps> = ({
  findings,
}) => {
  const strengths = findings.filter(f => f.type === 'STRENGTH');
  const warnings = findings.filter(f => f.type === 'WARNING' || f.type === 'RISK');
  const recommendations = findings
    .filter(f => f.recommendation)
    .map(f => f.recommendation!);

  return (
    <Card className="col-span-1 sm:col-span-2 bg-surface border-border flex flex-col h-full max-h-[500px] overflow-hidden">
      <CardHeader className="shrink-0 pb-2">
        <CardTitle className="text-base font-semibold">Analysis Findings</CardTitle>
      </CardHeader>
      <CardContent className="space-y-5 text-sm text-text-primary leading-relaxed overflow-y-auto pr-2 custom-scrollbar pb-6">
        {strengths.length > 0 && (
          <div>
            <h4 className="flex items-center gap-2 font-semibold text-green-600 mb-2 sticky top-0 bg-surface py-1">
              <CheckCircle2 className="w-4 h-4" /> Strengths
            </h4>
            <ul className="space-y-3">
              {strengths.map((s, idx) => (
                <li key={idx} className="pl-6 text-text-secondary">
                  <span className="font-medium text-text-primary block">{s.title}</span>
                  {s.description}
                </li>
              ))}
            </ul>
          </div>
        )}

        {warnings.length > 0 && (
          <div>
            <h4 className="flex items-center gap-2 font-semibold text-amber-500 mb-2 sticky top-0 bg-surface py-1">
              <AlertTriangle className="w-4 h-4" /> Attention Required
            </h4>
            <ul className="space-y-3">
              {warnings.map((w, idx) => (
                <li key={idx} className="pl-6 text-text-secondary">
                  <span className="font-medium text-text-primary block">{w.title}</span>
                  {w.description}
                </li>
              ))}
            </ul>
          </div>
        )}

        {recommendations.length > 0 && (
          <div>
            <h4 className="flex items-center gap-2 font-semibold text-blue-500 mb-2 sticky top-0 bg-surface py-1">
              <ArrowRight className="w-4 h-4" /> Recommended Actions
            </h4>
            <ul className="space-y-1.5 list-disc list-inside text-text-secondary pl-2">
              {Array.from(new Set(recommendations)).map((r, idx) => (
                <li key={idx}>{r}</li>
              ))}
            </ul>
          </div>
        )}

        {findings.length === 0 && (
          <p className="text-text-secondary italic">No significant findings detected.</p>
        )}
      </CardContent>
    </Card>
  );
};


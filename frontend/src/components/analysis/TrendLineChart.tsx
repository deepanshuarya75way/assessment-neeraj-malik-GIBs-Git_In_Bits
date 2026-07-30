import React from 'react';

interface TrendLineChartProps {
  data: { timestamp: string; score: number }[];
}

export const TrendLineChart: React.FC<TrendLineChartProps> = ({ data }) => {
  if (!data || data.length === 0) {
    return (
      <div className="w-full h-64 border border-dashed border-border/60 rounded-lg flex flex-col items-center justify-center p-6 bg-surface/30">
        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" className="text-text-secondary/50 mb-3">
          <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline>
        </svg>
        <p className="text-sm font-semibold text-text-primary mb-1">Not enough historical data to display trends yet.</p>
        <p className="text-xs text-text-secondary text-center max-w-sm">
          Trend history will appear as more repository activity is collected over time.
        </p>
      </div>
    );
  }

  // Future implementation: render real SVG line chart using data array
  return (
    <div className="w-full h-64 border border-border/40 rounded-lg bg-surface/50">
      {/* Chart logic will go here when data is available */}
    </div>
  );
};

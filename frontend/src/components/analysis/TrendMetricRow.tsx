import React from 'react';
import { TrendSparkline } from './TrendSparkline';

interface TrendMetricRowProps {
  label: string;
  score: number;
  history?: number[];
  tooltip: string;
}

export const TrendMetricRow: React.FC<TrendMetricRowProps> = ({
  label,
  score,
  history = [],
  tooltip
}) => {
  const getTrendColor = (val: number) => {
    if (val > 70) return 'text-green-500';
    if (val > 40) return 'text-yellow-500';
    return 'text-red-500';
  };

  const getTrendIcon = (val: number) => {
    if (val > 70) return '⬆';
    if (val > 40) return '➡';
    return '⬇';
  };

  return (
    <div className="flex items-center justify-between p-3 rounded-md bg-surface/30 border border-border/40 hover:bg-surface/50 transition-colors" title={tooltip}>
      <span className="text-sm font-medium text-text-secondary w-1/3">{label}</span>
      <div className="flex-1 flex justify-center px-4 opacity-70">
        <TrendSparkline data={history} colorClass={getTrendColor(score)} />
      </div>
      <div className="flex items-center justify-end gap-3 w-1/4">
        <span className="font-mono text-lg font-bold text-text-primary">{score}</span>
        <span className={`text-sm font-medium flex items-center ${getTrendColor(score)}`}>
          {getTrendIcon(score)}
        </span>
      </div>
    </div>
  );
};

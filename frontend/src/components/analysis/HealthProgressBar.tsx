import React from 'react';

interface HealthProgressBarProps {
  label: string;
  score: number;
  inverseColors?: boolean;
  trendColors?: boolean;
}

export const HealthProgressBar: React.FC<HealthProgressBarProps> = ({ label, score, inverseColors = false, trendColors = false }) => {
  const getBarColor = (val: number) => {
    if (trendColors) {
      if (val > 70) return 'bg-green-500';
      if (val > 40) return 'bg-yellow-500';
      return 'bg-red-500';
    }
    if (inverseColors) {
      if (val <= 20) return 'bg-green-500';
      if (val <= 40) return 'bg-green-400';
      if (val <= 60) return 'bg-yellow-500';
      if (val <= 80) return 'bg-orange-500';
      return 'bg-red-500';
    }
    if (val >= 75) return 'bg-green-500';
    if (val >= 60) return 'bg-yellow-500';
    if (val >= 40) return 'bg-orange-500';
    return 'bg-red-500';
  };

  return (
    <div className="space-y-1.5">
      <div className="flex justify-between items-center text-sm">
        <span className="font-medium text-text-secondary">{label}</span>
        <span className="font-semibold text-text-primary">{score}</span>
      </div>
      <div className="w-full h-2.5 bg-border/40 rounded-full overflow-hidden">
        <div
          className={`h-full rounded-full transition-all duration-700 ease-out ${getBarColor(score)}`}
          style={{ width: `${Math.max(0, Math.min(100, score))}%` }}
        />
      </div>
    </div>
  );
};

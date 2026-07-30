import React from 'react';

interface TrendSparklineProps {
  data: number[];
  colorClass: string;
}

export const TrendSparkline: React.FC<TrendSparklineProps> = ({ data, colorClass }) => {
  if (!data || data.length < 2) {
    return (
      <svg className="w-24 h-8" viewBox="0 0 100 32" preserveAspectRatio="none">
        <line x1="0" y1="16" x2="100" y2="16" stroke="currentColor" strokeWidth="2" strokeDasharray="4 4" className="text-border" />
      </svg>
    );
  }

  // Find min and max for scaling
  const min = Math.min(...data);
  const max = Math.max(...data);
  const range = max - min === 0 ? 1 : max - min;
  const step = 100 / (data.length - 1);

  // Generate SVG path
  const points = data.map((val, idx) => {
    const x = idx * step;
    const y = 32 - ((val - min) / range) * 28 - 2; // pad top/bottom
    return `${x},${y}`;
  });

  return (
    <svg className="w-24 h-8 overflow-visible" viewBox="0 0 100 32" preserveAspectRatio="none">
      <polyline
        fill="none"
        stroke="currentColor"
        strokeWidth="2.5"
        strokeLinecap="round"
        strokeLinejoin="round"
        points={points.join(' ')}
        className={colorClass}
      />
    </svg>
  );
};

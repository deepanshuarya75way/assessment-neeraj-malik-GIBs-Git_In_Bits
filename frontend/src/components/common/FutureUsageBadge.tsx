import { Lightbulb } from 'lucide-react';
import { cn } from '../../utils/cn';

interface FutureUsageBadgeProps {
  usage: string;
  className?: string;
}

export function FutureUsageBadge({ usage, className }: FutureUsageBadgeProps) {
  return (
    <div 
      className={cn(
        "inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-primary/10 text-primary border border-primary/20",
        className
      )}
      title="Future Git in Bits Platform Usage"
    >
      <Lightbulb className="w-3 h-3 mr-1" />
      {usage}
    </div>
  );
}

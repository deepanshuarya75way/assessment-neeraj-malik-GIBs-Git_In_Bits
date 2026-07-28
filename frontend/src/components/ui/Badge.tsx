import { cn } from '../../utils/cn';

export interface BadgeProps extends React.HTMLAttributes<HTMLDivElement> {
  variant?: 'default' | 'secondary' | 'outline' | 'success' | 'warning' | 'danger';
}

export function Badge({ className, variant = 'default', ...props }: BadgeProps) {
  return (
    <div
      className={cn(
        'inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2',
        {
          'border-transparent bg-primary text-text-primary hover:bg-primary/80': variant === 'default',
          'border-transparent bg-border text-text-primary hover:bg-border/80': variant === 'secondary',
          'text-text-primary border-border': variant === 'outline',
          'border-transparent bg-green-500/10 text-green-500 hover:bg-green-500/20': variant === 'success',
          'border-transparent bg-yellow-500/10 text-yellow-500 hover:bg-yellow-500/20': variant === 'warning',
          'border-transparent bg-red-500/10 text-red-500 hover:bg-red-500/20': variant === 'danger',
        },
        className
      )}
      {...props}
    />
  );
}

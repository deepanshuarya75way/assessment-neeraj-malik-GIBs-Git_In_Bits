import React from 'react';
import { Badge } from '../ui/Badge';
import { RawJsonViewer } from './RawJsonViewer';

interface EntityHeaderProps {
  title: string;
  description?: string;
  badges?: { label: string; variant?: React.ComponentProps<typeof Badge>['variant'] }[];
  avatarUrl?: string;
  actions?: React.ReactNode;
  rawData?: any; // New prop for raw JSON viewer
}

export function EntityHeader({ title, description, badges, avatarUrl, actions, rawData }: EntityHeaderProps) {
  return (
    <div className="flex flex-col space-y-4 md:flex-row md:items-start md:justify-between md:space-y-0 pb-6 border-b border-border">
      <div className="flex items-start space-x-4">
        {avatarUrl && (
          <img src={avatarUrl} alt={title} className="h-16 w-16 rounded-md border border-border object-cover bg-surface" />
        )}
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-text-primary flex items-center space-x-3">
            <span>{title}</span>
            {badges && badges.length > 0 && (
              <span className="flex space-x-2">
                {badges.map((b, idx) => (
                  <Badge key={idx} variant={b.variant || 'default'}>
                    {b.label}
                  </Badge>
                ))}
              </span>
            )}
          </h1>
          {description && <p className="mt-2 text-text-secondary max-w-2xl">{description}</p>}
        </div>
      </div>
      <div className="flex shrink-0 space-x-3 items-center">
        {rawData && <RawJsonViewer data={rawData} title={`${title} Raw JSON`} />}
        {actions}
      </div>
    </div>
  );
}

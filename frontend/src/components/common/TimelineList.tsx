import React from 'react';
import { Skeleton } from '../ui/Skeleton';

interface TimelineItem {
  id: string | number;
  title: React.ReactNode;
  description?: React.ReactNode;
  icon?: React.ReactNode;
  timestamp?: string | React.ReactNode;
}

interface TimelineListProps {
  items?: TimelineItem[];
  isLoading?: boolean;
  emptyMessage?: string;
}

export function TimelineList({ items, isLoading, emptyMessage = 'No activities found' }: TimelineListProps) {
  if (isLoading) {
    return (
      <div className="space-y-6">
        {Array.from({ length: 3 }).map((_, idx) => (
          <div key={idx} className="flex space-x-4">
            <Skeleton className="h-8 w-8 rounded-full" />
            <div className="space-y-2 flex-1">
              <Skeleton className="h-4 w-1/3" />
              <Skeleton className="h-3 w-1/4" />
            </div>
          </div>
        ))}
      </div>
    );
  }

  if (!items || items.length === 0) {
    return <div className="py-8 text-center text-text-secondary">{emptyMessage}</div>;
  }

  return (
    <div className="flow-root">
      <ul role="list" className="-mb-8">
        {items.map((item, itemIdx) => (
          <li key={item.id}>
            <div className="relative pb-8">
              {itemIdx !== items.length - 1 ? (
                <span className="absolute left-4 top-4 -ml-px h-full w-0.5 bg-border" aria-hidden="true" />
              ) : null}
              <div className="relative flex space-x-3">
                <div>
                  <span className="h-8 w-8 rounded-full bg-surface border border-border flex items-center justify-center ring-8 ring-background">
                    {item.icon || <div className="h-2 w-2 rounded-full bg-text-secondary" />}
                  </span>
                </div>
                <div className="flex min-w-0 flex-1 justify-between space-x-4 pt-1.5">
                  <div>
                    <p className="text-sm text-text-primary">{item.title}</p>
                    {item.description && (
                      <div className="mt-1 text-sm text-text-secondary">{item.description}</div>
                    )}
                  </div>
                  {item.timestamp && (
                    <div className="whitespace-nowrap text-right text-xs text-text-secondary">
                      {item.timestamp}
                    </div>
                  )}
                </div>
              </div>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}

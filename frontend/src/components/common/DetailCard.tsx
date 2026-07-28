import React from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '../ui/Card';

interface DetailItem {
  label: string;
  value: React.ReactNode;
}

interface DetailCardProps {
  title: string;
  items: DetailItem[];
}

export function DetailCard({ title, items }: DetailCardProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>{title}</CardTitle>
      </CardHeader>
      <CardContent>
        <dl className="grid grid-cols-1 gap-x-4 gap-y-6 sm:grid-cols-2">
          {items.map((item, idx) => (
            <div key={idx} className="sm:col-span-1">
              <dt className="text-sm font-medium text-text-secondary">{item.label}</dt>
              <dd className="mt-1 text-sm text-text-primary break-all">{item.value || '—'}</dd>
            </div>
          ))}
        </dl>
      </CardContent>
    </Card>
  );
}

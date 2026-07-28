import { useReleases } from '../../api/queries';
import { TimelineList } from '../../components/common/TimelineList';
import { Tag } from 'lucide-react';
import { Badge } from '../../components/ui/Badge';

export function ReleasesTab({ repoName }: { repoName: string }) {
  const { data: releases, isLoading } = useReleases(repoName);

  const items = releases?.map((r) => ({
    id: r.version || r.tag,
    title: <span className="font-bold text-lg text-text-primary">{r.name || r.tag}</span>,
    description: (
      <div className="mt-2">
        <div className="flex space-x-2 mb-3">
          <Badge variant="outline">{r.tag}</Badge>
          {r.prerelease && <Badge variant="warning">Pre-release</Badge>}
          {r.draft && <Badge variant="secondary">Draft</Badge>}
        </div>
        <div className="text-sm bg-surface p-4 rounded-md border border-border whitespace-pre-wrap max-h-96 overflow-y-auto">
          {r.body || 'No release notes provided.'}
        </div>
      </div>
    ),
    timestamp: new Date(r.publishedAt).toLocaleDateString(),
    icon: <Tag className="h-4 w-4 text-primary" />,
  }));

  return <TimelineList items={items} isLoading={isLoading} emptyMessage="No releases found." />;
}

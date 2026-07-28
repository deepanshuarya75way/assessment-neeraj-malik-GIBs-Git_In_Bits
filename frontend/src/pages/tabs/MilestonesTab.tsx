import { useMilestones } from '../../api/queries';
import { Card } from '../../components/ui/Card';
import { Spinner } from '../../components/ui/Spinner';
import type { Milestone } from '../../types';

export function MilestonesTab({ repoName }: { repoName: string }) {
  const { data: milestones, isLoading, error } = useMilestones(repoName);

  if (isLoading) return <div className="p-8 text-center"><Spinner className="w-6 h-6 mx-auto" /></div>;
  if (error) return <div className="text-red-500 p-4">Failed to load milestones</div>;
  if (!milestones?.length) return <div className="text-text-secondary p-4">No milestones found.</div>;

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
      {milestones.map((m: Milestone) => (
        <Card key={m.title} className="p-4 bg-surface/50 border-border">
          <div className="flex justify-between items-start mb-2">
            <h3 className="font-semibold text-text-primary">{m.title}</h3>
            <span className={`px-2 py-1 rounded-full text-xs ${m.state === 'open' ? 'bg-green-500/10 text-green-400' : 'bg-red-500/10 text-red-400'}`}>
              {m.state}
            </span>
          </div>
          <p className="text-sm text-text-secondary mb-3">{m.description || 'No description provided.'}</p>
          <div className="flex space-x-4 text-xs text-text-muted">
            <span>Open Issues: {m.openIssues}</span>
            <span>Closed Issues: {m.closedIssues}</span>
          </div>
        </Card>
      ))}
    </div>
  );
}

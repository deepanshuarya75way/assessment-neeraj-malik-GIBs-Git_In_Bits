import { useWorkflowRuns } from '../../api/queries';
import { Card } from '../../components/ui/Card';
import { Spinner } from '../../components/ui/Spinner';
import type { WorkflowRun } from '../../types';

export function WorkflowRunsTab({ repoName }: { repoName: string }) {
  const { data: runs, isLoading, error } = useWorkflowRuns(repoName);

  if (isLoading) return <div className="p-8 text-center"><Spinner className="w-6 h-6 mx-auto" /></div>;
  if (error) return <div className="text-red-500 p-4">Failed to load workflow runs</div>;
  if (!runs?.length) return <div className="text-text-secondary p-4">No workflow runs found.</div>;

  return (
    <div className="space-y-4">
      {runs.map((r: WorkflowRun) => (
        <Card key={`${r.name}-${r.createdAt}`} className="p-4 bg-surface/50 border-border flex items-center justify-between">
          <div>
            <h3 className="font-semibold text-text-primary">{r.name}</h3>
            <p className="text-sm text-text-secondary">Branch: <span className="font-mono text-xs">{r.branch}</span></p>
            <p className="text-xs text-text-muted mt-1">Event: {r.event} • {new Date(r.createdAt).toLocaleString()}</p>
          </div>
          <div className="text-right">
            <span className={`px-2 py-1 rounded-full text-xs capitalize ${
              r.conclusion === 'success' ? 'bg-green-500/10 text-green-400' :
              r.conclusion === 'failure' ? 'bg-red-500/10 text-red-400' :
              'bg-slate-800 text-text-muted border border-border'
            }`}>
              {r.conclusion || r.status}
            </span>
          </div>
        </Card>
      ))}
    </div>
  );
}

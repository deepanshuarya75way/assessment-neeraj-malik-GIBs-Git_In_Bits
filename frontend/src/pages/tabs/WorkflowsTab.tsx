import { useWorkflows } from '../../api/queries';
import { Card } from '../../components/ui/Card';
import { Spinner } from '../../components/ui/Spinner';
import type { Workflow } from '../../types';

export function WorkflowsTab({ repoName }: { repoName: string }) {
  const { data: workflows, isLoading, error } = useWorkflows(repoName);

  if (isLoading) return <div className="p-8 text-center"><Spinner className="w-6 h-6 mx-auto" /></div>;
  if (error) return <div className="text-red-500 p-4">Failed to load workflows</div>;
  if (!workflows?.length) return <div className="text-text-secondary p-4">No workflows found.</div>;

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
      {workflows.map((w: Workflow) => (
        <Card key={w.name} className="p-4 bg-surface/50 border-border">
          <div className="flex justify-between items-start mb-2">
            <h3 className="font-semibold text-text-primary">{w.name}</h3>
            <span className={`px-2 py-1 rounded-full text-xs ${w.state === 'active' ? 'bg-green-500/10 text-green-400' : 'bg-slate-800 text-text-muted border border-border'}`}>
              {w.state}
            </span>
          </div>
          <p className="text-sm text-text-secondary font-mono text-xs">{w.path}</p>
          <p className="text-xs text-text-muted mt-2">Created: {new Date(w.createdAt).toLocaleString()}</p>
        </Card>
      ))}
    </div>
  );
}

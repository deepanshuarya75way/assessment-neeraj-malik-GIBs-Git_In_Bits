import { useDeployments } from '../../api/queries';
import { Card } from '../../components/ui/Card';
import { Spinner } from '../../components/ui/Spinner';
import type { Deployment } from '../../types';

export function DeploymentsTab({ repoName }: { repoName: string }) {
  const { data: deployments, isLoading, error } = useDeployments(repoName);

  if (isLoading) return <div className="p-8 text-center"><Spinner className="w-6 h-6 mx-auto" /></div>;
  if (error) return <div className="text-red-500 p-4">Failed to load deployments</div>;
  if (!deployments?.length) return <div className="text-text-secondary p-4">No deployments found.</div>;

  return (
    <div className="space-y-4">
      {deployments.map((d: Deployment) => (
        <Card key={d.id} className="p-4 bg-surface/50 border-border">
          <div className="flex justify-between items-start mb-2">
            <h3 className="font-semibold text-text-primary capitalize">{d.environment}</h3>
            <span className="px-2 py-1 bg-slate-800 text-text-muted rounded-full text-xs border border-border">
              {d.state || 'Pending'}
            </span>
          </div>
          <p className="text-sm text-text-secondary">Creator: {d.creator}</p>
          <p className="text-xs text-text-muted mt-2">Deployed At: {new Date(d.createdAt).toLocaleString()}</p>
        </Card>
      ))}
    </div>
  );
}

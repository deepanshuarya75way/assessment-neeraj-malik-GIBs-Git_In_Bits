import { useCollaborators } from '../../api/queries';
import { Card } from '../../components/ui/Card';
import { Spinner } from '../../components/ui/Spinner';
import type { Collaborator } from '../../types';

export function CollaboratorsTab({ repoName }: { repoName: string }) {
  const { data: collaborators, isLoading, error } = useCollaborators(repoName);

  if (isLoading) return <div className="p-8 text-center"><Spinner className="w-6 h-6 mx-auto" /></div>;
  if (error) return <div className="text-red-500 p-4">Failed to load collaborators</div>;
  if (!collaborators?.length) return <div className="text-text-secondary p-4">No collaborators found.</div>;

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
      {collaborators.map((c: Collaborator) => (
        <Card key={c.username} className="p-4 bg-surface/50 border-border flex items-center space-x-4">
          <img src={c.avatarUrl} alt={c.username} className="w-10 h-10 rounded-full" />
          <div>
            <h3 className="font-semibold text-text-primary">{c.username}</h3>
            <p className="text-xs text-text-muted capitalize">{c.roleName}</p>
          </div>
        </Card>
      ))}
    </div>
  );
}

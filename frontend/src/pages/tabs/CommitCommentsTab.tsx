import { useCommitComments } from '../../api/queries';
import { Card } from '../../components/ui/Card';
import { Spinner } from '../../components/ui/Spinner';
import type { CommitComment } from '../../types';

export function CommitCommentsTab({ repoName }: { repoName: string }) {
  const { data: comments, isLoading, error } = useCommitComments(repoName);

  if (isLoading) return <div className="p-8 text-center"><Spinner className="w-6 h-6 mx-auto" /></div>;
  if (error) return <div className="text-red-500 p-4">Failed to load commit comments</div>;
  if (!comments?.length) return <div className="text-text-secondary p-4">No commit comments found.</div>;

  return (
    <div className="space-y-4">
      {comments.map((comment: CommitComment) => (
        <Card key={comment.id} className="p-4 bg-surface/50 border-border">
          <div className="flex justify-between items-start mb-2">
            <span className="font-medium text-text-primary">{comment.author || 'Unknown'}</span>
            <span className="text-xs text-text-muted">{new Date(comment.createdAt).toLocaleString()}</span>
          </div>
          <p className="text-sm text-text-secondary">{comment.comment}</p>
        </Card>
      ))}
    </div>
  );
}

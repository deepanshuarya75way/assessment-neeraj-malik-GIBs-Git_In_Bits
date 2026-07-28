import { useIssueComments } from '../../api/queries';
import { Card } from '../../components/ui/Card';
import { Spinner } from '../../components/ui/Spinner';
import type { IssueComment } from '../../types';

export function IssueCommentsTab({ repoName }: { repoName: string }) {
  const { data: comments, isLoading, error } = useIssueComments(repoName);

  if (isLoading) return <div className="p-8 text-center"><Spinner className="w-6 h-6 mx-auto" /></div>;
  if (error) return <div className="text-red-500 p-4">Failed to load issue comments</div>;
  if (!comments?.length) return <div className="text-text-secondary p-4">No issue comments found.</div>;

  return (
    <div className="space-y-4">
      {comments.map((comment: IssueComment) => (
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

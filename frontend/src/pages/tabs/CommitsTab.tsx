import { useCommits } from '../../api/queries';
import { TimelineList } from '../../components/common/TimelineList';
import { GitCommit } from 'lucide-react';

export function CommitsTab({ repoName }: { repoName: string }) {
  const { data: commits, isLoading } = useCommits(repoName);

  const items = commits?.map((c) => ({
    id: c.sha,
    title: <span className="font-medium text-text-primary">{c.message.split('\n')[0]}</span>,
    description: (
      <div className="flex flex-col gap-1 mt-1">
        <span>
          Authored by <span className="font-medium">{c.authorName || c.committerName || 'Unknown'}</span> 
          {' '} (<code className="text-xs">{c.sha.substring(0, 7)}</code>)
        </span>
        <div className="flex items-center gap-3 text-xs text-text-muted">
          {c.verificationStatus && (
            <span className={`px-1.5 rounded ${c.verificationStatus === 'verified' ? 'bg-green-500/10 text-green-400' : 'bg-slate-800'}`}>
              {c.verificationStatus}
            </span>
          )}
          {c.totalChanges !== undefined && (
            <span>
              <span className="text-green-400">+{c.additions}</span>{' '}
              <span className="text-red-400">-{c.deletions}</span>{' '}
              ({c.filesChangedCount} files)
            </span>
          )}
        </div>
      </div>
    ),
    timestamp: new Date(c.timestamp).toLocaleString(),
    icon: <GitCommit className="h-4 w-4 text-primary" />,
  }));

  return <TimelineList items={items} isLoading={isLoading} emptyMessage="No commits found." />;
}

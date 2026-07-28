import { useState } from 'react';
import { useReviewComments } from '../../api/queries';
import { TimelineList } from '../../components/common/TimelineList';
import { Input } from '../../components/ui/Input';
import { Button } from '../../components/ui/Button';
import { MessageCircle } from 'lucide-react';

export function ReviewCommentsTab({ repoName }: { repoName: string }) {
  const [prNumber, setPrNumber] = useState<number>(1);
  const [inputVal, setInputVal] = useState('1');

  const { data: comments, isLoading, isFetching } = useReviewComments(repoName, prNumber);

  const items = comments?.map((c) => ({
    id: c.id,
    title: <span className="font-medium text-text-primary">{c.reviewer} commented on <code className="bg-slate-800 px-1 rounded">{c.path}:{c.line || c.originalLine}</code></span>,
    description: <div className="mt-1 text-sm bg-surface border border-border p-3 rounded-md">{c.body}</div>,
    timestamp: new Date(c.createdAt).toLocaleString(),
    icon: <MessageCircle className="h-4 w-4 text-primary" />,
  }));

  return (
    <div className="space-y-6">
      <div className="flex items-end space-x-4 max-w-sm">
        <div className="flex-1 space-y-1">
          <label className="text-xs font-medium text-text-secondary">Pull Request Number</label>
          <Input type="number" value={inputVal} onChange={(e) => setInputVal(e.target.value)} />
        </div>
        <Button onClick={() => setPrNumber(parseInt(inputVal, 10) || 1)}>Fetch Comments</Button>
      </div>
      
      <TimelineList items={items} isLoading={isLoading || isFetching} emptyMessage={`No review comments found for PR #${prNumber}.`} />
    </div>
  );
}

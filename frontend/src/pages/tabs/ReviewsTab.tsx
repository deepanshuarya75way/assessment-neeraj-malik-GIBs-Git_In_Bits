import { useState } from 'react';
import { useReviews } from '../../api/queries';
import { TimelineList } from '../../components/common/TimelineList';
import { Input } from '../../components/ui/Input';
import { Button } from '../../components/ui/Button';
import { MessageSquare } from 'lucide-react';

export function ReviewsTab({ repoName }: { repoName: string }) {
  const [prNumber, setPrNumber] = useState<number>(1);
  const [inputVal, setInputVal] = useState('1');

  const { data: reviews, isLoading, isFetching } = useReviews(repoName, prNumber);

  const items = reviews?.map((r, idx) => ({
    id: idx,
    title: <span className="font-medium text-text-primary">{r.reviewer}</span>,
    description: (
      <div>
        <span className={`inline-block px-2 py-0.5 rounded text-xs font-medium ${r.state === 'APPROVED' ? 'bg-green-500/10 text-green-500' : 'bg-slate-800 text-text-secondary'}`}>
          {r.state}
        </span>
        <div className="mt-2 text-sm">{r.body || 'No comment provided.'}</div>
      </div>
    ),
    timestamp: new Date(r.submittedAt).toLocaleString(),
    icon: <MessageSquare className="h-4 w-4 text-primary" />,
  }));

  return (
    <div className="space-y-6">
      <div className="flex items-end space-x-4 max-w-sm">
        <div className="flex-1 space-y-1">
          <label className="text-xs font-medium text-text-secondary">Pull Request Number</label>
          <Input type="number" value={inputVal} onChange={(e) => setInputVal(e.target.value)} />
        </div>
        <Button onClick={() => setPrNumber(parseInt(inputVal, 10) || 1)}>Fetch Reviews</Button>
      </div>
      
      <TimelineList items={items} isLoading={isLoading || isFetching} emptyMessage={`No reviews found for PR #${prNumber}.`} />
    </div>
  );
}

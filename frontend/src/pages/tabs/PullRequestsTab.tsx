import { usePullRequests } from '../../api/queries';
import { DataTable, type Column } from '../../components/common/DataTable';
import { Badge } from '../../components/ui/Badge';
import type { PullRequest } from '../../types';

export function PullRequestsTab({ repoName }: { repoName: string }) {
  const { data: prs, isLoading } = usePullRequests(repoName);

  const columns: Column<PullRequest>[] = [
    { header: 'PR', accessorKey: (row) => `#${row.number}` },
    { header: 'Title', accessorKey: 'title', className: 'font-medium' },
    { header: 'Author', accessorKey: 'author' },
    { 
      header: 'State', 
      accessorKey: (row) => (
        <Badge variant={row.state === 'open' ? 'success' : row.state === 'closed' ? 'danger' : 'secondary'}>
          {row.state}
        </Badge>
      ) 
    },
    { header: 'Base', accessorKey: 'baseRef' },
    { header: 'Head', accessorKey: 'headRef' },
    { header: 'Created', accessorKey: (row) => new Date(row.createdAt).toLocaleDateString() },
  ];

  return <DataTable data={prs} columns={columns} isLoading={isLoading} emptyMessage="No pull requests found." />;
}

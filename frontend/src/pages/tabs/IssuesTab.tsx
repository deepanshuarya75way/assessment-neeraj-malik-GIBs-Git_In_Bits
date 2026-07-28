import { useIssues } from '../../api/queries';
import { DataTable, type Column } from '../../components/common/DataTable';
import { Badge } from '../../components/ui/Badge';
import type { Issue } from '../../types';

export function IssuesTab({ repoName }: { repoName: string }) {
  const { data: issues, isLoading } = useIssues(repoName);

  const columns: Column<Issue>[] = [
    { header: 'Issue', accessorKey: (row) => `#${row.number}` },
    { header: 'Title', accessorKey: 'title', className: 'font-medium max-w-md truncate' },
    { 
      header: 'State', 
      accessorKey: (row) => (
        <Badge variant={row.state === 'open' ? 'success' : 'secondary'}>
          {row.state}
        </Badge>
      ) 
    },
    { 
      header: 'Labels', 
      accessorKey: (row) => (
        <div className="flex flex-wrap gap-1">
          {row.labels?.map((l) => (
            <span key={l} className="px-2 py-0.5 text-xs bg-slate-800 rounded-full border border-border">{l}</span>
          ))}
        </div>
      ) 
    },
    { 
      header: 'Assignees', 
      accessorKey: (row) => row.assignees?.join(', ') || 'Unassigned' 
    },
    { header: 'Created', accessorKey: (row) => new Date(row.createdAt).toLocaleDateString() },
  ];

  return <DataTable data={issues} columns={columns} isLoading={isLoading} emptyMessage="No issues found." />;
}

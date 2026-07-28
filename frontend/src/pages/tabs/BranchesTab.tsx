import { useBranches } from '../../api/queries';
import { DataTable, type Column } from '../../components/common/DataTable';
import { Badge } from '../../components/ui/Badge';
import type { Branch } from '../../types';

export function BranchesTab({ repoName }: { repoName: string }) {
  const { data: branches, isLoading } = useBranches(repoName);

  const columns: Column<Branch>[] = [
    { header: 'Branch Name', accessorKey: 'name', className: 'font-medium' },
    { 
      header: 'Protected', 
      accessorKey: (row) => row.protected ? <Badge variant="success">Yes</Badge> : <Badge variant="secondary">No</Badge> 
    },
    { 
      header: 'Protection Rules', 
      accessorKey: (row) => {
        if (!row.protection) return <span className="text-xs text-text-muted">None</span>;
        const rules = [];
        if (row.protection.requiredStatusChecks?.strict) rules.push('Status Checks');
        if (row.protection.requiredPullRequestReviews) rules.push('PR Reviews');
        if (row.protection.allowForcePushes?.enabled) rules.push('Force Pushes');
        return rules.length ? (
          <div className="flex flex-wrap gap-1 max-w-xs">
            {rules.map(r => <span key={r} className="text-[10px] bg-slate-800 px-1.5 py-0.5 rounded border border-border">{r}</span>)}
          </div>
        ) : <span className="text-xs text-text-muted">No rules</span>;
      }
    },
    { header: 'Latest SHA', accessorKey: (row) => <code className="text-xs bg-slate-800 px-2 py-1 rounded">{row.sha?.substring(0, 7) || 'N/A'}</code> },
  ];

  return <DataTable data={branches} columns={columns} isLoading={isLoading} emptyMessage="No branches found." />;
}

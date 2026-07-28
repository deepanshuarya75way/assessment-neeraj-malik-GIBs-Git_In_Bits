import { useContributors } from '../../api/queries';
import { DataTable, type Column } from '../../components/common/DataTable';
import type { Contributor } from '../../types';

export function ContributorsTab({ repoName }: { repoName: string }) {
  const { data: contributors, isLoading } = useContributors(repoName);

  const columns: Column<Contributor>[] = [
    { 
      header: 'User', 
      accessorKey: (row) => (
        <div className="flex items-center space-x-3">
          <img src={row.avatarUrl} alt={row.login} className="h-8 w-8 rounded-full bg-surface border border-border" />
          <span className="font-medium text-text-primary">{row.login}</span>
        </div>
      ) 
    },
    { header: 'Contributions', accessorKey: 'contributions' },
  ];

  return <DataTable data={contributors} columns={columns} isLoading={isLoading} emptyMessage="No contributors found." />;
}

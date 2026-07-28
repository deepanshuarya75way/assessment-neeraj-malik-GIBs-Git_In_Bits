import { useNavigate } from 'react-router-dom';
import { useRepos } from '../api/queries';
import { DataTable, type Column } from '../components/common/DataTable';
import { EntityHeader } from '../components/common/EntityHeader';
import { Badge } from '../components/ui/Badge';
import type { Repo } from '../types';

export function Repositories() {
  const { data: repos, isLoading } = useRepos();
  const navigate = useNavigate();

  const columns: Column<Repo>[] = [
    {
      header: 'Name',
      accessorKey: (row) => (
        <div>
          <div className="font-medium text-text-primary">{row.name}</div>
          {row.description && <div className="text-xs text-text-secondary mt-1">{row.description}</div>}
        </div>
      ),
    },
    {
      header: 'Visibility',
      accessorKey: (row) => (
        <Badge variant={row.visibility === 'public' ? 'success' : 'secondary'}>
          {row.visibility}
        </Badge>
      ),
    },
    {
      header: 'Language',
      accessorKey: 'language',
    },
    {
      header: 'Stars',
      accessorKey: 'stargazersCount',
    },
    {
      header: 'Updated',
      accessorKey: (row) => new Date(row.updatedAt).toLocaleDateString(),
    },
  ];

  return (
    <div className="space-y-6">
      <EntityHeader
        title="Repositories"
        description="A complete list of repositories for the current organization."
      />
      <DataTable
        data={repos}
        columns={columns}
        isLoading={isLoading}
        onRowClick={(row) => navigate(`/repositories/${row.name}`)}
        emptyMessage="No repositories found."
      />
    </div>
  );
}

import { useLabels } from '../../api/queries';
import { Spinner } from '../../components/ui/Spinner';
import type { Label } from '../../types';

export function LabelsTab({ repoName }: { repoName: string }) {
  const { data: labels, isLoading, error } = useLabels(repoName);

  if (isLoading) return <div className="p-8 text-center"><Spinner className="w-6 h-6 mx-auto" /></div>;
  if (error) return <div className="text-red-500 p-4">Failed to load labels</div>;
  if (!labels?.length) return <div className="text-text-secondary p-4">No labels found.</div>;

  return (
    <div className="flex flex-wrap gap-2">
      {labels.map((label: Label) => (
        <span 
          key={label.name} 
          className="px-3 py-1 rounded-full text-xs font-semibold"
          style={{ backgroundColor: `#${label.color}`, color: '#000' }}
        >
          {label.name}
        </span>
      ))}
    </div>
  );
}

import { EntityHeader } from '../components/common/EntityHeader';
import { Card } from '../components/ui/Card';
import { CheckCircle2 } from 'lucide-react';
import { useCapabilities } from '../api/queries';
import { Spinner } from '../components/ui/Spinner';

export function CapabilityReport() {
  const { data: capData, isLoading } = useCapabilities();

  // If the backend returns a simple structure, we can map it. The backend currently returns:
  // { capabilities: string[], futureUsage: string }
  // So we will just render these dynamically.
  
  if (isLoading) return <div className="p-8 text-center"><Spinner className="w-8 h-8 mx-auto" /></div>;

  return (
    <div className="space-y-6">
      <EntityHeader
        title="Capability Report"
        description="Technical validation of GitHub API metadata coverage."
      />
      
      <Card className="bg-surface border-border overflow-x-auto p-6">
        <h3 className="text-lg font-semibold text-text-primary mb-4">Supported Capabilities</h3>
        <ul className="space-y-3 mb-6">
          {capData?.capabilities?.map((cap: string, i: number) => (
            <li key={i} className="flex items-center text-sm text-text-secondary">
              <CheckCircle2 className="w-5 h-5 text-green-500 mr-3" />
              {cap}
            </li>
          ))}
        </ul>
        
        <h3 className="text-lg font-semibold text-text-primary mb-2">Future Usage</h3>
        <p className="text-sm text-text-secondary leading-relaxed bg-slate-800/30 p-4 rounded-md border border-border">
          {capData?.futureUsage}
        </p>
      </Card>
    </div>
  );
}

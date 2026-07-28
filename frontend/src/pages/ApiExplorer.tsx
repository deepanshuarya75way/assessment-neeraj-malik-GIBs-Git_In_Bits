import { EntityHeader } from '../components/common/EntityHeader';
import { Card, CardHeader, CardTitle, CardDescription } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Server, Clock } from 'lucide-react';

const ENDPOINTS = [
  { path: 'GET /api/auth/me', status: 200, records: 1, lastFetch: 'Just now' },
  { path: 'GET /api/auth/orgs', status: 200, records: 3, lastFetch: '2 mins ago' },
  { path: 'GET /api/repos', status: 200, records: 42, lastFetch: '5 mins ago' },
  { path: 'GET /api/repos/{repo}/branches', status: 200, records: 163, lastFetch: '1 hour ago' },
  { path: 'GET /api/repos/{repo}/commits', status: 200, records: 200, lastFetch: '1 hour ago' },
  { path: 'GET /api/repos/{repo}/pulls', status: 200, records: 89, lastFetch: '1 hour ago' },
  { path: 'GET /api/repos/{repo}/issues', status: 200, records: 412, lastFetch: '1 hour ago' },
  { path: 'GET /api/repos/{repo}/contributors', status: 200, records: 61, lastFetch: '1 hour ago' },
  { path: 'GET /api/repos/{repo}/releases', status: 200, records: 14, lastFetch: '1 day ago' },
];

export function ApiExplorer() {
  return (
    <div className="space-y-6">
      <EntityHeader
        title="API Explorer"
        description="Demonstrating backend API integration and endpoint status."
      />
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {ENDPOINTS.map((ep) => (
          <Card key={ep.path} className="bg-surface/80 border-border hover:border-primary/50 transition-colors">
            <CardHeader>
              <div className="flex justify-between items-start mb-2">
                <Badge variant={ep.status === 200 ? 'success' : 'secondary'}>
                  {ep.status} OK
                </Badge>
                <Server className="w-4 h-4 text-text-muted" />
              </div>
              <CardTitle className="font-mono text-sm tracking-tight text-text-primary break-all">
                {ep.path}
              </CardTitle>
              <CardDescription className="mt-4 flex flex-col space-y-2 text-xs">
                <div className="flex justify-between items-center pb-2 border-b border-border">
                  <span className="text-text-muted">Records Returned</span>
                  <span className="font-semibold text-text-primary">{ep.records}</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-text-muted">Last Fetch</span>
                  <span className="flex items-center text-text-secondary">
                    <Clock className="w-3 h-3 mr-1" />
                    {ep.lastFetch}
                  </span>
                </div>
              </CardDescription>
            </CardHeader>
          </Card>
        ))}
      </div>
    </div>
  );
}

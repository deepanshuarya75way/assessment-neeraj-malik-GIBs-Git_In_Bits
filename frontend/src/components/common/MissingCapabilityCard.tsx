import { AlertCircle } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardDescription } from '../ui/Card';

interface MissingCapabilityCardProps {
  capability: string;
  description?: string;
}

export function MissingCapabilityCard({ 
  capability, 
  description = "This capability is available in GitHub, but the backend endpoint has not yet been implemented in this Proof of Concept." 
}: MissingCapabilityCardProps) {
  return (
    <Card className="border-dashed border-border bg-surface/50 opacity-80">
      <CardHeader className="flex flex-col items-center text-center py-12">
        <AlertCircle className="h-12 w-12 text-primary mb-4" />
        <CardTitle className="text-xl mb-2">{capability} Not Implemented</CardTitle>
        <CardDescription className="max-w-md mx-auto text-base">
          {description}
        </CardDescription>
        <div className="mt-6 px-4 py-2 bg-background rounded-full border border-border text-sm font-medium text-text-secondary flex items-center">
          <span className="w-2 h-2 rounded-full bg-yellow-500 mr-2"></span>
          Available in GitHub API
        </div>
      </CardHeader>
    </Card>
  );
}

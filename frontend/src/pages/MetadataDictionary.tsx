import { EntityHeader } from '../components/common/EntityHeader';
import { Card, CardHeader, CardTitle, CardDescription } from '../components/ui/Card';
import { FutureUsageBadge } from '../components/common/FutureUsageBadge';
import { useMetadataDictionary } from '../api/queries';
import { Spinner } from '../components/ui/Spinner';

export function MetadataDictionary() {
  const { data: dictionary, isLoading } = useMetadataDictionary();

  if (isLoading) return <div className="p-8 text-center"><Spinner className="w-8 h-8 mx-auto" /></div>;

  return (
    <div className="space-y-6">
      <EntityHeader
        title="Metadata Dictionary"
        description="Living technical specification detailing future usage of each GitHub field."
      />
      
      <div className="space-y-4">
        {dictionary?.map((item: any) => (
          <Card key={item.field} className="bg-surface/80 border-border">
            <CardHeader>
              <div className="flex justify-between items-start">
                <CardTitle className="text-sm font-semibold text-text-primary mb-2 capitalize">
                  {item.field}
                </CardTitle>
                <FutureUsageBadge usage={item.futureUsage} />
              </div>
              <CardDescription className="text-text-secondary text-sm">
                {item.description}
              </CardDescription>
            </CardHeader>
          </Card>
        ))}
        {(!dictionary || dictionary.length === 0) && (
          <div className="text-text-secondary p-4">No metadata dictionary terms found.</div>
        )}
      </div>
    </div>
  );
}

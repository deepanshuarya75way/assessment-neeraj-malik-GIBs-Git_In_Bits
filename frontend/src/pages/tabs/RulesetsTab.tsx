import { useRulesets } from '../../api/queries';
import { Card } from '../../components/ui/Card';
import { Spinner } from '../../components/ui/Spinner';
import type { Ruleset } from '../../types';

export function RulesetsTab({ repoName }: { repoName: string }) {
  const { data: rulesets, isLoading, error } = useRulesets(repoName);

  if (isLoading) return <div className="p-8 text-center"><Spinner className="w-6 h-6 mx-auto" /></div>;
  if (error) return <div className="text-red-500 p-4">Failed to load rulesets</div>;
  if (!rulesets?.length) return <div className="text-text-secondary p-4">No rulesets found.</div>;

  return (
    <div className="space-y-4">
      {rulesets.map((r: Ruleset) => (
        <Card key={r.id} className="p-4 bg-surface/50 border-border">
          <div className="flex justify-between items-start mb-2">
            <h3 className="font-semibold text-text-primary">{r.name}</h3>
            <span className={`px-2 py-1 rounded-full text-xs ${
              r.enforcement === 'active' ? 'bg-green-500/10 text-green-400' : 'bg-slate-800 text-text-muted border border-border'
            }`}>
              {r.enforcement}
            </span>
          </div>
          <p className="text-sm text-text-secondary">Target: {r.target}</p>
          <div className="mt-3">
            <h4 className="text-xs font-semibold text-text-muted uppercase tracking-wider mb-2">Rules</h4>
            <div className="flex flex-wrap gap-2">
              {r.rules && r.rules.map((rule: any, i: number) => (
                <span key={i} className="px-2 py-1 bg-slate-800 border border-border text-xs rounded-md text-text-secondary">
                  {rule.type || 'Unknown Rule'}
                </span>
              ))}
              {(!r.rules || r.rules.length === 0) && <span className="text-xs text-text-muted">No rules defined.</span>}
            </div>
          </div>
        </Card>
      ))}
    </div>
  );
}

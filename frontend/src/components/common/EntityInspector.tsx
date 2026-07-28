import { Drawer } from '../ui/Drawer';

interface EntityInspectorProps {
  isOpen: boolean;
  onClose: () => void;
  entityType: string;
  data: any;
}

export function EntityInspector({ isOpen, onClose, entityType, data }: EntityInspectorProps) {
  if (!data) return null;

  return (
    <Drawer isOpen={isOpen} onClose={onClose} title={`${entityType} Inspector`}>
      <div className="space-y-6">
        <div className="text-sm text-text-secondary mb-4">
          Complete metadata properties retrieved from GitHub for this {entityType.toLowerCase()}.
        </div>
        
        <div className="space-y-4">
          {Object.entries(data).map(([key, value]) => (
            <div key={key} className="border-b border-border pb-3 last:border-0">
              <div className="text-xs font-mono text-text-secondary mb-1">{key}</div>
              <div className="text-sm text-text-primary break-words">
                {value === null ? (
                  <span className="text-text-muted italic">null</span>
                ) : typeof value === 'object' ? (
                  <pre className="bg-[#1e1e1e] p-2 rounded text-xs mt-1 overflow-x-auto text-[#d4d4d4]">
                    {JSON.stringify(value, null, 2)}
                  </pre>
                ) : typeof value === 'boolean' ? (
                  <span className={value ? "text-green-400" : "text-red-400"}>
                    {value.toString()}
                  </span>
                ) : (
                  String(value)
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
    </Drawer>
  );
}

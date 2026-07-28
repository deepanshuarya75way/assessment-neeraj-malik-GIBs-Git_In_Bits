import { EntityHeader } from '../components/common/EntityHeader';
import { Card, CardTitle } from '../components/ui/Card';
import { RawJsonViewer } from '../components/common/RawJsonViewer';
import { ChevronDown } from 'lucide-react';
import { useState } from 'react';
import { cn } from '../utils/cn';

const METADATA_CATEGORIES = [
  {
    name: 'Repository Metadata',
    data: {
      "Repository Name": "gitinbits-poc",
      "Owner": "Neeraj-Malik-12",
      "Language": "Java",
      "Topics": ["spring-boot", "react", "poc"],
      "License": "MIT",
      "Visibility": "public",
      "Created": "2026-07-24T10:00:00Z",
      "Updated": "2026-07-25T14:30:00Z",
      "Forks": 0,
      "Stars": 0,
      "Watchers": 1,
      "Archived": false,
      "Disabled": false
    }
  },
  {
    name: 'Commit Metadata',
    data: {
      "SHA": "a1b2c3d4e5f6g7h8i9j0",
      "Parents": ["z9y8x7w6v5u4t3s2r1q0"],
      "Author": "Neeraj Malik",
      "Committer": "Neeraj Malik",
      "Timestamp": "2026-07-25T14:30:00Z",
      "Verification": {
        "verified": true,
        "reason": "valid"
      },
      "Files Changed": 5,
      "Additions": 120,
      "Deletions": 10,
      "Changed Files": ["src/App.tsx", "src/pages/Dashboard.tsx"]
    }
  }
];

export function MetadataExplorer() {
  const [expandedCategories, setExpandedCategories] = useState<string[]>(['Repository Metadata']);

  const toggleCategory = (name: string) => {
    setExpandedCategories(prev => 
      prev.includes(name) ? prev.filter(n => n !== name) : [...prev, name]
    );
  };

  return (
    <div className="space-y-6">
      <EntityHeader
        title="GitHub Metadata Explorer"
        description="Displaying complete raw metadata categories retrieved from GitHub."
      />
      
      <div className="space-y-4">
        {METADATA_CATEGORIES.map((category) => {
          const isExpanded = expandedCategories.includes(category.name);
          
          return (
            <Card key={category.name} className="bg-surface overflow-hidden border-border transition-all duration-200">
              <div 
                className="flex items-center justify-between p-4 cursor-pointer hover:bg-slate-800/30 transition-colors"
                onClick={() => toggleCategory(category.name)}
              >
                <CardTitle className="text-lg font-medium text-text-primary select-none flex items-center">
                  <ChevronDown className={cn("w-5 h-5 mr-3 transition-transform text-text-secondary", !isExpanded && "-rotate-90")} />
                  {category.name}
                </CardTitle>
                
                <div onClick={e => e.stopPropagation()}>
                  <RawJsonViewer data={category.data} title={category.name} />
                </div>
              </div>
              
              {isExpanded && (
                <div className="px-6 pb-6 pt-2 border-t border-border/50">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-x-12 gap-y-4 mt-4">
                    {Object.entries(category.data).map(([key, value]) => (
                      <div key={key} className="flex justify-between items-center py-2 border-b border-border/30 last:border-0">
                        <span className="text-sm font-medium text-text-secondary">{key}</span>
                        <span className="text-sm text-text-primary text-right break-words max-w-[50%]">
                          {typeof value === 'object' ? JSON.stringify(value) : String(value)}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </Card>
          );
        })}
      </div>
    </div>
  );
}

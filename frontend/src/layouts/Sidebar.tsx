import { NavLink } from 'react-router-dom';
import { 
  LayoutDashboard, Book, Users, GitBranch, Database, ShieldCheck, 
  Settings, Network, BookOpen, Activity, HeartPulse, ShieldAlert,
  BrainCircuit, LineChart, CheckCircle2, UserCheck, Clock, Share2, Building
} from 'lucide-react';
import { cn } from '../utils/cn';

const navigation = [
  { name: 'Overview', href: '/dashboard', icon: LayoutDashboard },
  { name: 'Repositories', href: '/repositories', icon: Book },
  { name: 'Organizations', href: '#', icon: Building }, // Not implemented in route yet
  { name: 'Teams', href: '#', icon: Users }, // Not implemented in route yet
  { name: 'GitHub Metadata', href: '/metadata', icon: Database },
  { name: 'Capability Report', href: '/capabilities', icon: ShieldCheck },
  { name: 'API Explorer', href: '/api-explorer', icon: Network },
  { name: 'Metadata Dictionary', href: '/dictionary', icon: BookOpen },
  { name: 'Settings', href: '#', icon: Settings },
];

const futureModules = [
  { name: 'Feature Intelligence', icon: BrainCircuit },
  { name: 'Health', icon: HeartPulse },
  { name: 'Stability', icon: Activity },
  { name: 'Risk', icon: ShieldAlert },
  { name: 'AI Summary', icon: BrainCircuit },
  { name: 'Engineering Insights', icon: LineChart },
  { name: 'Developer Consistency', icon: CheckCircle2 },
  { name: 'Ownership', icon: UserCheck },
  { name: 'Timeline', icon: Clock },
  { name: 'Knowledge Graph', icon: Share2 },
];

export function Sidebar() {
  return (
    <div className="flex w-64 flex-col bg-surface border-r border-border h-full">
      <div className="flex h-16 shrink-0 items-center px-6 border-b border-border">
        <GitBranch className="h-8 w-8 text-primary" />
        <span className="ml-3 text-lg font-bold text-text-primary">Git in Bits</span>
      </div>
      <div className="flex flex-1 flex-col overflow-y-auto pt-5 pb-4 custom-scrollbar">
        <nav className="mt-2 space-y-1 px-3">
          <div className="text-xs font-semibold text-text-secondary uppercase tracking-wider mb-2 px-3">
            Exploration
          </div>
          {navigation.map((item) => (
            <NavLink
              key={item.name}
              to={item.href}
              className={({ isActive }) =>
                cn(
                  isActive && item.href !== '#' ? 'bg-slate-800 text-text-primary' : 'text-text-secondary hover:bg-slate-800/50 hover:text-text-primary',
                  'group flex items-center px-3 py-2 text-sm font-medium rounded-md transition-colors',
                  item.href === '#' && 'cursor-not-allowed opacity-60'
                )
              }
              onClick={(e) => item.href === '#' && e.preventDefault()}
            >
              <item.icon className="mr-3 h-5 w-5 shrink-0" aria-hidden="true" />
              {item.name}
            </NavLink>
          ))}
        </nav>

        <nav className="mt-8 space-y-1 px-3">
          <div className="text-xs font-semibold text-text-muted uppercase tracking-wider mb-2 px-3 flex items-center justify-between">
            Future Intelligence
            <span className="text-[10px] bg-slate-800 px-1.5 py-0.5 rounded text-text-muted">ROADMAP</span>
          </div>
          {futureModules.map((item) => (
            <div
              key={item.name}
              className="group flex items-center px-3 py-2 text-sm font-medium rounded-md text-text-muted opacity-50 cursor-not-allowed grayscale"
              title="Coming in Future Analytics Layer"
            >
              <item.icon className="mr-3 h-5 w-5 shrink-0" aria-hidden="true" />
              {item.name}
            </div>
          ))}
        </nav>
      </div>
    </div>
  );
}

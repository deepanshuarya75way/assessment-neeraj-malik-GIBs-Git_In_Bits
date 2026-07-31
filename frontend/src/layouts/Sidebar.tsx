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
  { name: 'Developers', href: '/developers', icon: Users },
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
      </div>
    </div>
  );
}

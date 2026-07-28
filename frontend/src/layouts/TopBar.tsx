import { useAuth } from '../context/AuthContext';
import { useDataSource } from '../context/DataSourceContext';
import { LogOut, Building } from 'lucide-react';
import { Button } from '../components/ui/Button';

export function TopBar() {
  const { user, logout } = useAuth();
  const { sourceValue, clearDataSource } = useDataSource();

  const handleSwitchDataSource = () => {
    clearDataSource();
    window.location.href = '/data-source-selection';
  };

  return (
    <header className="sticky top-0 z-10 flex h-16 shrink-0 bg-surface border-b border-border shadow-sm">
      <div className="flex flex-1 items-center justify-between px-4 sm:px-6 lg:px-8">
        <div className="flex items-center space-x-4">
          {sourceValue && (
            <div className="flex items-center space-x-2 text-sm text-text-secondary bg-background px-3 py-1.5 rounded-md border border-border">
              <Building className="h-4 w-4" />
              <span className="font-medium text-text-primary">{sourceValue}</span>
              <button onClick={handleSwitchDataSource} className="ml-2 text-xs text-primary hover:underline">
                Change Source
              </button>
            </div>
          )}
        </div>
        <div className="flex items-center space-x-4">
          {user && (
            <div className="flex items-center space-x-3">
              <span className="text-sm font-medium text-text-primary">{user.name || user.login}</span>
              <img
                className="h-8 w-8 rounded-full bg-background border border-border"
                src={user.avatarUrl}
                alt=""
              />
              <Button variant="ghost" size="sm" onClick={logout} className="ml-2" title="Log out">
                <LogOut className="h-4 w-4" />
              </Button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}

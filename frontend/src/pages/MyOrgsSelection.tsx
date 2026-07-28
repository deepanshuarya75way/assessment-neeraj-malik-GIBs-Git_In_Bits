import { useNavigate } from 'react-router-dom';
import { useUserOrgs } from '../api/queries';
import { useDataSource } from '../context/DataSourceContext';
import { useAuth } from '../context/AuthContext';
import { DataSourceType } from '../types';
import { Card, CardHeader, CardTitle, CardDescription } from '../components/ui/Card';
import { Spinner } from '../components/ui/Spinner';
import { Building } from 'lucide-react';

export function MyOrgsSelection() {
  const { data: orgs, isLoading, error } = useUserOrgs();
  const { setDataSource } = useDataSource();
  const { user } = useAuth();
  const navigate = useNavigate();

  const handleSelectOrg = (orgLogin: string) => {
    setDataSource(DataSourceType.AUTHENTICATED_ORGANIZATION, orgLogin);
    navigate('/dashboard');
  };

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-background">
        <Spinner className="h-10 w-10" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-background">
        <div className="text-red-500">Failed to load organizations. Please try again.</div>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen flex-col items-center bg-background py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md text-center">
        <Building className="mx-auto h-12 w-12 text-primary" />
        <h2 className="mt-6 text-3xl font-extrabold text-text-primary">Select Organization</h2>
        <p className="mt-2 text-sm text-text-secondary">
          Choose a GitHub organization to explore
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-3xl">
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          {/* Always show the user's personal account as an option */}
          <Card 
            className="cursor-pointer transition-colors border-primary bg-slate-800/50 hover:bg-slate-800"
            onClick={() => handleSelectOrg(user?.login || '')}
          >
            <CardHeader className="flex flex-row items-center space-x-4">
              <img src={user?.avatarUrl} alt={user?.login} className="h-12 w-12 rounded-md bg-surface border border-border" />
              <div>
                <CardTitle className="text-lg">{user?.login} (Personal)</CardTitle>
                <CardDescription className="mt-1 line-clamp-1">Personal Repositories</CardDescription>
              </div>
            </CardHeader>
          </Card>

          {/* Show Organizations if any */}
          {orgs?.map((org) => (
            <Card 
              key={org.login} 
              className="cursor-pointer transition-colors hover:border-primary hover:bg-slate-800/50"
              onClick={() => handleSelectOrg(org.login)}
            >
              <CardHeader className="flex flex-row items-center space-x-4">
                <img src={org.avatarUrl} alt={org.login} className="h-12 w-12 rounded-md bg-surface border border-border" />
                <div>
                  <CardTitle className="text-lg">{org.login}</CardTitle>
                  {org.description && (
                    <CardDescription className="mt-1 line-clamp-1">{org.description}</CardDescription>
                  )}
                </div>
              </CardHeader>
            </Card>
          ))}
        </div>
      </div>
    </div>
  );
}

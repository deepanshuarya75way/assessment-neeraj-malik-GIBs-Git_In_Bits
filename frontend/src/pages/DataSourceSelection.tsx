import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDataSource } from '../context/DataSourceContext';
import { Building2, Globe, GitPullRequest } from 'lucide-react';
import { DataSourceType } from '../types';

export function DataSourceSelection() {
  const navigate = useNavigate();
  const { setDataSource } = useDataSource();

  const [orgInput, setOrgInput] = useState('');
  const [error, setError] = useState<string | null>(null);

  const handleMyOrgs = () => {
    navigate('/my-orgs');
  };

  const handlePublicOrg = () => {
    if (!orgInput.trim()) {
      setError('Organization name is required.');
      return;
    }
    setError(null);
    setDataSource(DataSourceType.PUBLIC_ORGANIZATION, orgInput.trim());
    navigate('/dashboard');
  };

  return (
    <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8 flex flex-col items-center">
      <div className="max-w-7xl w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Choose Data Source
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Select where you want to pull GitHub metadata from.
          </p>
        </div>

        {error && (
          <div className="rounded-md bg-red-50 p-4 mb-4 max-w-3xl mx-auto w-full">
            <div className="flex">
              <div className="ml-3">
                <h3 className="text-sm font-medium text-red-800">{error}</h3>
              </div>
            </div>
          </div>
        )}

        <div className="mt-8 grid grid-cols-1 gap-8 md:grid-cols-2 max-w-4xl mx-auto">
          {/* Card 1: My Organizations */}
          <div className="bg-white overflow-hidden shadow rounded-lg flex flex-col h-full border border-gray-200">
            <div className="p-6 flex-grow">
              <div className="flex items-center justify-center h-12 w-12 rounded-md bg-indigo-500 text-white mx-auto mb-4">
                <Building2 className="h-6 w-6" />
              </div>
              <h3 className="text-lg leading-6 font-medium text-gray-900 text-center mb-2">My Organizations</h3>
              <p className="text-sm text-gray-500 text-center mb-6">
                Use organizations available to your authenticated GitHub account.
              </p>
            </div>
            <div className="bg-gray-50 px-6 py-4">
              <button
                onClick={handleMyOrgs}
                className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                Continue
              </button>
            </div>
          </div>

          {/* Card 2: Public Organization */}
          <div className="bg-white overflow-hidden shadow rounded-lg flex flex-col h-full border border-gray-200">
            <div className="p-6 flex-grow">
              <div className="flex items-center justify-center h-12 w-12 rounded-md bg-emerald-500 text-white mx-auto mb-4">
                <Globe className="h-6 w-6" />
              </div>
              <h3 className="text-lg leading-6 font-medium text-gray-900 text-center mb-2">Public Organization</h3>
              <p className="text-sm text-gray-500 text-center mb-6">
                Explore any public GitHub organization by name.
              </p>
              <div>
                <label htmlFor="org-input" className="sr-only">Organization Name</label>
                <input
                  type="text"
                  id="org-input"
                  className="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-md p-2 border"
                  placeholder="spring-projects"
                  value={orgInput}
                  onChange={(e) => setOrgInput(e.target.value)}
                  onKeyDown={(e) => e.key === 'Enter' && handlePublicOrg()}
                />
              </div>
            </div>
            <div className="bg-gray-50 px-6 py-4">
              <button
                onClick={handlePublicOrg}
                className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-emerald-600 hover:bg-emerald-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500"
              >
                Load Organization
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

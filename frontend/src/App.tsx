import { BrowserRouter, Routes, Route, Navigate, Outlet } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider, useAuth } from './context/AuthContext';
import { DataSourceProvider } from './context/DataSourceContext';

import { Login } from './pages/Login';
import { AuthCallback } from './pages/AuthCallback';
import { DataSourceSelection } from './pages/DataSourceSelection';
import { MyOrgsSelection } from './pages/MyOrgsSelection';
import { DashboardLayout } from './layouts/DashboardLayout';
import { Dashboard } from './pages/Dashboard';
import { Repositories } from './pages/Repositories';
import { RepositoryDetails } from './pages/RepositoryDetails';
import { MetadataExplorer } from './pages/MetadataExplorer';
import { CapabilityReport } from './pages/CapabilityReport';
import { ApiExplorer } from './pages/ApiExplorer';
import { MetadataDictionary } from './pages/MetadataDictionary';
import { Spinner } from './components/ui/Spinner';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

function ProtectedRoute() {
  const { user, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <Spinner className="h-8 w-8" />
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <DataSourceProvider>
          <BrowserRouter>
            <Routes>
              {/* Public Routes */}
              <Route path="/login" element={<Login />} />
              <Route path="/auth/callback" element={<AuthCallback />} />

              {/* Protected Routes */}
              <Route element={<ProtectedRoute />}>
                <Route path="/data-source-selection" element={<DataSourceSelection />} />
                <Route path="/my-orgs" element={<MyOrgsSelection />} />
                
                <Route element={<DashboardLayout />}>
                  <Route path="/" element={<Navigate to="/dashboard" replace />} />
                  <Route path="/dashboard" element={<Dashboard />} />
                  <Route path="/repositories" element={<Repositories />} />
                  <Route path="/repositories/:repo" element={<RepositoryDetails />} />
                  <Route path="/metadata" element={<MetadataExplorer />} />
                  <Route path="/capabilities" element={<CapabilityReport />} />
                  <Route path="/api-explorer" element={<ApiExplorer />} />
                  <Route path="/dictionary" element={<MetadataDictionary />} />
                </Route>
              </Route>
            </Routes>
          </BrowserRouter>
        </DataSourceProvider>
      </AuthProvider>
    </QueryClientProvider>
  );
}

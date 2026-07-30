import { Outlet, Navigate } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { TopBar } from './TopBar';
import { useDataSource } from '../context/DataSourceContext';
import { AiManagerChat } from '../components/ai/AiManagerChat';

export function DashboardLayout() {
  const { sourceType } = useDataSource();

  // If trying to access dashboard routes without a data source, redirect to selection
  if (!sourceType) {
    return <Navigate to="/data-source-selection" replace />;
  }

  return (
    <div className="flex h-screen overflow-hidden bg-background">
      <Sidebar />
      <div className="flex flex-1 flex-col overflow-hidden">
        <TopBar />
        <main className="flex-1 overflow-y-auto focus:outline-none p-6 lg:p-8">
          <Outlet />
        </main>
      </div>
      <AiManagerChat />
    </div>
  );
}

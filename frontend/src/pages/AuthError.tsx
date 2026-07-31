import { Link } from 'react-router-dom';

export function AuthError() {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-slate-950 text-white p-4">
      <h1 className="text-3xl font-bold text-red-500 mb-4">Authentication Failed</h1>
      <p className="text-slate-300 mb-6 max-w-lg text-center">
        We couldn't authenticate you with GitHub. This is likely because your GitHub API rate limit has been exceeded due to the earlier data generation, or the application lacks necessary permissions.
      </p>
      <Link to="/login" className="bg-blue-600 hover:bg-blue-700 px-6 py-2 rounded font-semibold transition">
        Try Again
      </Link>
    </div>
  );
}

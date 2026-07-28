import { useAuth } from '../context/AuthContext';
import { Button } from '../components/ui/Button';
import { Code } from 'lucide-react';
import { Navigate } from 'react-router-dom';

export function Login() {
  const { user, login } = useAuth();

  if (user) {
    return <Navigate to="/dashboard" replace />;
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-background px-4 sm:px-6 lg:px-8">
      <div className="w-full max-w-md space-y-8 text-center">
        <div>
          <h2 className="mt-6 text-3xl font-extrabold tracking-tight text-text-primary">
            Welcome to Git in Bits
          </h2>
          <p className="mt-2 text-sm text-text-secondary">
            GitHub Data Explorer (Proof of Concept)
          </p>
        </div>
        <div className="mt-8 bg-surface py-8 px-4 shadow sm:rounded-lg sm:px-10 border border-border">
          <Button onClick={login} className="w-full flex items-center justify-center" size="lg">
            <Code className="mr-2 h-5 w-5" />
            Continue with GitHub
          </Button>
          <p className="mt-4 text-xs text-text-secondary">
            By authenticating, you allow Git in Bits to read your organization and repository metadata.
          </p>
        </div>
      </div>
    </div>
  );
}

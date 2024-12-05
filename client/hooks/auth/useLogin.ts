// app/hooks/auth/useLogin.ts
import { useState } from 'react';
import apiClient from '@/app/apiClient';
import { useSession } from '@/app/ctx';

export const useLogin = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { signIn } = useSession(); // This should now work correctly

  const login = async (username: string, password: string) => {
    setLoading(true);
    setError(null);

    try {
      const response = await apiClient.post('/auth/login', { username, password });
      const { accessToken } = response.data;

      signIn(accessToken);

      return { accessToken };
    } catch (err: any) {
      setError(err.response?.data?.message || 'An error occurred during login.');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return { login, loading, error };
};

// app/hooks/auth/useLogin.ts
import { useState } from 'react';
import apiClient, { setAccessToken } from '@/app/apiClient';
import { useSession } from '@/app/ctx';
import { router } from 'expo-router';

export const useLogin = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { signIn } = useSession();

  const login = async (username: string, password: string) => {
    setLoading(true);
    setError(null);

    try {
      const response = await apiClient.post('/auth/login', { username, password });
      const { accessToken } = response.data.data;

      // Store tokens securely in context
      signIn(accessToken);

      // Set token in apiClient
      setAccessToken(accessToken);
      router.push('/(main)');

      return { accessToken };
    } catch (err: any) {
      setError(err.response?.data?.message || 'An error occurred during login.');
   
    } finally {
      setLoading(false);
    }
  };

  return { login, loading, error };
};

// hooks/auth/useLogin.ts
import { useState } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { BASE_URL } from '@/config';
import { LoginResponse, LoginResponseDTO } from '@/types/auth/LoginResponse ';

export const useLogin = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const login = async (username: string, password: string): Promise<LoginResponseDTO> => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(`${BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
      });

      if (!response.ok) {
        const isJson = response.headers.get('content-type')?.includes('application/json');
        const errorData = isJson ? await response.json() : { message: await response.text() };
        throw new Error(errorData.message || 'Login failed');
        console.log('errorData', errorData);
      }

      const data: LoginResponse = await response.json();

      if (!data.success || !data.data) {
        throw new Error(data.message || 'Login failed');
        console.log('data', data);
      }

      // Store tokens securely
      await AsyncStorage.setItem('accessToken', data.data.accessToken);
      await AsyncStorage.setItem('refreshToken', data.data.refreshToken);

      return data.data;
    } catch (err: any) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return { login, loading, error };
};

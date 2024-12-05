// app/hooks/auth/useLogout.ts
import apiClient from '@/app/apiClient';
import { useSession } from '@/app/ctx';

export const useLogout = () => {
  const { signOut } = useSession();

  const logout = async () => {
    try {
      await apiClient.post('/auth/logout');
    } catch (err: any) {
      console.warn('Logout failed:', err.message);
    } finally {
      signOut();
    }
  };

  return { logout };
};

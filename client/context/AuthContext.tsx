import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import * as SecureStore from 'expo-secure-store';
import { useLogin } from '@/hooks/auth/useLogin';
import { useLogout } from '@/hooks/auth/useLogout';
import { Platform } from 'react-native';

type AuthContextType = {
  isSignedIn: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  loading: boolean;
  error: string | null;
};

const AuthContext = createContext<AuthContextType>({
  isSignedIn: false,
  login: async () => {},
  logout: async () => {},
  loading: false,
  error: null,
});

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [isSignedIn, setIsSignedIn] = useState(false);
  const { login: performLogin, loading, error } = useLogin();
  const { logout: performLogout } = useLogout();

  // Restore session from secure storage
  useEffect(() => {
    const restoreSession = async () => {
      if (Platform.OS === 'web') {
        
      } else {
      const token = await SecureStore.getItemAsync('accessToken');
      setIsSignedIn(!!token);}
    };

    restoreSession();
  }, []);

  const login = useCallback(
    async (username: string, password: string) => {
      const data = await performLogin(username, password);
      if (data.accessToken) {
        setIsSignedIn(true);
      }
    },
    [performLogin]
  );

  const logout = useCallback(async () => {
    await performLogout();
    setIsSignedIn(false);
  }, [performLogout]);

  return (
    <AuthContext.Provider value={{ isSignedIn, login, logout, loading, error }}>
      {children}
    </AuthContext.Provider>
  );
};

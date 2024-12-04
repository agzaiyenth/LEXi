import React, { createContext, useContext, useState, useEffect } from 'react';

type AuthContextType = {
  isSignedIn: boolean;
  login: () => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextType>({
  isSignedIn: false,
  login: () => {},
  logout: () => {},
});

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [isSignedIn, setIsSignedIn] = useState(false);

  useEffect(() => {
    // TODO:Simulate an auth state check (replace with real logic)
    const checkAuth = async () => {
      const user = await new Promise((resolve) =>
        setTimeout(() => resolve(null), 1000)
      );
      setIsSignedIn(!!user);
    };

    checkAuth();
  }, []);

  const login = () => setIsSignedIn(true);
  const logout = () => setIsSignedIn(false);

  return (
    <AuthContext.Provider value={{ isSignedIn, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

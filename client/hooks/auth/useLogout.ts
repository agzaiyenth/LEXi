import * as SecureStore from 'expo-secure-store';

export const useLogout = () => {
  const logout = async () => {
    // Clear tokens from secure storage
    await SecureStore.deleteItemAsync('accessToken');
    await SecureStore.deleteItemAsync('refreshToken');
  };

  return { logout };
};

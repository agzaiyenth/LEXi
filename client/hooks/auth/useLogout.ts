// app/hooks/auth/useLogout.js
import AsyncStorage from '@react-native-async-storage/async-storage';

export const useLogout = () => {
  const logout = async () => {
    // Clear tokens from storage
    await AsyncStorage.removeItem('accessToken');
    await AsyncStorage.removeItem('refreshToken');
  };

  return { logout };
};

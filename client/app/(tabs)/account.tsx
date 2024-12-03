// app/(unprotected)/landing.tsx
import { useAuth } from '@/context/AuthContext';
import { View, Text, Button } from 'react-native';

export default function AccountScreen() {
  const { logout } = useAuth();
  return (
    <View className="flex-1 items-center justify-center">
      <Text>Account Screen</Text>
      <Button title="Logout" onPress={logout} />
    </View>
  );
}
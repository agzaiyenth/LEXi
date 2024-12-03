// app/(unprotected)/landing.tsx

import React from 'react';
import { View, Button, Text } from 'react-native';
import { useAuth } from '@/context/AuthContext';

export default function LandingPage() {
  const { login } = useAuth();

  return (
    <View>
      <Text >Welcome to the Landing Page!</Text>
      <Button title="Login" onPress={login} />
    </View>
  );
}

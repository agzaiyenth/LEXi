// app/(tabs)/account.tsx
import React from 'react';
import { View, Text, Button, StyleSheet } from 'react-native';
import { useAuth } from '@/context/AuthContext';

export default function AccountScreen() {
  const { logout } = useAuth();

  console.log('Rendering Account Screen'); // Debug log

  return (
    <View style={styles.container}>
      <Text style={styles.text}>Account Screen</Text>
      <Button title="Logout" onPress={logout} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  text: {
    fontSize: 18,
  },
});

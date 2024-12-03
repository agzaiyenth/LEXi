// app/(unprotected)/_layout.tsx
import React from 'react';
import { Stack } from 'expo-router';

export default function UnprotectedLayout() {
  return (
    <Stack screenOptions={{ headerShown: false }}>
      {/* Include a Slot to render child routes */}
      <Stack.Screen name="landing" />
      <Stack.Screen name="auth/page" />
      {/* You can add more unprotected screens here */}
    </Stack>
  );
}

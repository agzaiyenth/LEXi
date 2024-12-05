// app/(main)/_layout.tsx
import React from 'react';
import { Redirect, Stack, Tabs } from 'expo-router';
import { Text } from 'react-native';
import { useSession } from '../ctx';

export default function AppLayout() {
  const { session, isLoading } = useSession();

  if (isLoading) {
    return <Text>Loading...</Text>;
  }

  if (!session) {
    return <Redirect href="/LandingScreen" />;
  }

  return (
    <Tabs>
      <Tabs.Screen name="HomeScreen" options={{ title: 'Home' }} />
      <Tabs.Screen name="LearnScreen" options={{ title: 'Learn' }} />
      <Tabs.Screen name="PlayScreen" options={{ title: 'Play' }} />
      <Tabs.Screen name="ExploreScreen" options={{ title: 'Explore' }} />
      <Tabs.Screen name="AccountScreen" options={{ title: 'Account' }} />
    </Tabs>
  );
}
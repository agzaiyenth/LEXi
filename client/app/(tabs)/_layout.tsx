// app/(tabs)/_layout.tsx
import React, { useEffect, useRef, useState } from 'react';
import { Tabs, useRouter } from 'expo-router';
import { View, Text, TouchableOpacity, Animated } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { theme } from '../theme';
import { useAuth } from '@/context/AuthContext';

export default function TabLayout() {
  const animatedValue = useRef(new Animated.Value(0)).current;
  const { isLoggedIn } = useAuth();

  if (!isLoggedIn) {
    return null; // Optionally, display a loading indicator or placeholder
  }

  return (
    <View style={{ flex: 1, backgroundColor: theme.colors.background.offWhite }}>
      <Tabs
        screenOptions={{
          headerShown: false,
        }}
        tabBar={({ navigation, state, descriptors }) => (
          <View
            style={{
              flexDirection: 'row',
              padding: theme.spacing.medium,
              position: 'absolute',
              bottom: 0,
              paddingVertical: theme.spacing.small,
              backgroundColor: theme.colors.primary.light2,
              borderTopLeftRadius: theme.spacing.large,
              borderTopRightRadius: theme.spacing.large,
            }}
          >
            {state.routes.map((route, index) => {
              const { options } = descriptors[route.key];
              const isFocused = state.index === index;

              const onPress = () => {
                const event = navigation.emit({
                  type: 'tabPress',
                  target: route.key,
                  canPreventDefault: true,
                });

                if (!isFocused && !event.defaultPrevented) {
                  navigation.navigate(route.name);
                }
              };

              const getIcon = () => {
                switch (route.name) {
                  case 'index':
                    return (props:any) => <MaterialIcons name="home" {...props} />;
                  case 'learn':
                    return (props:any) => <MaterialIcons name="apps" {...props} />;
                  case 'play':
                    return (props:any) => <MaterialIcons name="sports-esports" {...props} />;
                  case 'explore':
                    return (props:any) => <MaterialIcons name="public" {...props} />;
                  case 'account':
                    return (props:any) => <MaterialIcons name="account-circle" {...props} />;
                  default:
                    return (props:any) => <MaterialIcons name="home" {...props} />;
                }
              };

              const getLabel = () => {
                switch (route.name) {
                  case 'index':
                    return 'Home';
                  case 'learn':
                    return 'LearnZone';
                  case 'play':
                    return 'PlaySpace';
                  case 'explore':
                    return 'Explore+';
                  case 'account':
                    return 'Account';
                  default:
                    return route.name;
                }
              };

              return (
                <CustomTabBarButton
                  key={route.key}
                  label={getLabel()}
                  icon={getIcon()}
                  isFocused={isFocused}
                  onPress={onPress}
                  index={index}
                  animatedValue={animatedValue}
                />
              );
            })}
          </View>
        )}
      >
        <Tabs.Screen name="index" options={{ title: 'Home' }}/>
        <Tabs.Screen name="learn" />
        <Tabs.Screen name="play" />
        <Tabs.Screen name="explore" />
        <Tabs.Screen name="account" options={{ title: 'Account' }}/>
      </Tabs>
    </View>
  );
}

const CustomTabBarButton = ({
  label,
  icon: Icon,
  isFocused,
  onPress,
  index,
  animatedValue,
}: {
  label: string;
  icon: React.ComponentType<any>;
  isFocused: boolean;
  onPress: () => void;
  index: number;
  animatedValue: Animated.Value;
}) => (
  <TouchableOpacity
    onPress={onPress}
    style={{
      alignItems: 'center',
      justifyContent: 'center',
      flex: 1,
      paddingVertical: theme.spacing.small,
      backgroundColor: isFocused ? theme.colors.primary.medium2 : 'transparent',
      borderRadius: theme.spacing.medium,
     
    }}
  >
    <Icon
      size={24}
      color={isFocused ? theme.colors.background.offWhite : theme.colors.primary.dark1}
    />
    <Text
      style={{
        marginTop: 2,
        fontSize: 10,
        fontFamily: theme.fonts.regular,
        color: isFocused ? theme.colors.background.offWhite : theme.colors.primary.dark1,
      }}
    >
      {label}
    </Text>
  </TouchableOpacity>
);
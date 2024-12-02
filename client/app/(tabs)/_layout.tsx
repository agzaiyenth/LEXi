import React, { useRef } from 'react';
import { Tabs } from 'expo-router';
import { View, Text, TouchableOpacity, Animated } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { theme } from '../theme'; // Import your theme

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

export default function TabLayout() {
  const animatedValue = useRef(new Animated.Value(0)).current;

  const handleTabPress = (index: number) => {
    Animated.timing(animatedValue, {
      toValue: index,
      duration: 300,
      useNativeDriver: false,
    }).start();
  };

  return (
    <View style={{ flex: 1, backgroundColor: theme.colors.background.offWhite }}>
      <Tabs
        screenOptions={{
          headerShown: false,
        }}
        tabBar={({ navigation, state, descriptors }) => (
          <View
            style={{
              position: 'absolute',
              bottom: 0,
              left: 0,
              right: 0,
              paddingVertical: theme.spacing.small,
              backgroundColor: theme.colors.primary.light2,
              borderTopLeftRadius: theme.spacing.large,
              borderTopRightRadius: theme.spacing.large,
              flexDirection: 'row',
            }}
          >
            <Animated.View
              style={{
                position: 'absolute',
                height: '100%',
                width: `${100 / state.routes.length}%`,
                backgroundColor: theme.colors.primary.medium2,
                borderRadius: theme.spacing.medium,
                transform: [
                  {
                    translateX: animatedValue.interpolate({
                      inputRange: state.routes.map((_, i) => i),
                      outputRange: state.routes.map(
                        (_, i) => `${i * (100 / state.routes.length)}%`
                      ),
                    }),
                  },
                ],
              }}
            />
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
                  handleTabPress(index);
                }
              };

              const getIcon = () => {
                switch (route.name) {
                  case 'index':
                    return (props: any) => <MaterialIcons name="home" {...props} />;
                  case 'learn':
                    return (props: any) => <MaterialIcons name="apps" {...props} />;
                  case 'play':
                    return (props: any) => <MaterialIcons name="sports-esports" {...props} />;
                  case 'explore':
                    return (props: any) => <MaterialIcons name="public" {...props} />;
                  case 'account':
                    return (props: any) => <MaterialIcons name="account-circle" {...props} />;
                  default:
                    return (props: any) => <MaterialIcons name="home" {...props} />;
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
                  animatedValue={animatedValue}
                  index={index}
                />
              );
            })}
          </View>
        )}
      >
        <Tabs.Screen name="index" />
        <Tabs.Screen name="learn" />
        <Tabs.Screen name="play" />
        <Tabs.Screen name="explore" />
        <Tabs.Screen name="account" />
      </Tabs>
    </View>
  );
}

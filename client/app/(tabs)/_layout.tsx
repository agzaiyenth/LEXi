import { Tabs } from 'expo-router';
import { View, Text, TouchableOpacity } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { theme } from '../theme'; 

// Custom Tab Bar Button Component
const CustomTabBarButton = ({
  label,
  icon: Icon,
  isFocused,
  onPress,
}: {
  label: string;
  icon: React.ComponentType<any>;
  isFocused: boolean;
  onPress: () => void;
}) => (
  <TouchableOpacity
    onPress={onPress}
    className={`flex-1 items-center justify-center py-2 ${
      isFocused ? 'bg-emerald-100 rounded-lg' : ''
    }`}
    style={{
      backgroundColor: isFocused
        ? theme.colors.accent + '20' 
        : undefined,
    }}
  >
    <Icon
      size={24}
      color={isFocused ? theme.colors.primary : theme.colors.secondary}
    />
    <Text
      style={{
        fontSize: theme.fonts.small * theme.accessibility.fontScale,
        color: isFocused ? theme.colors.primary : theme.colors.text,
      }}
    >
      {label}
    </Text>
  </TouchableOpacity>
);

export default function TabLayout() {
  return (
    <Tabs
      screenOptions={{
        headerShown: false,
        tabBarStyle: {
          backgroundColor: theme.colors.background,
          position: 'absolute',
          bottom: 16,
          left: 16,
          right: 16,
          borderRadius: 9999,
          height: 70,
          paddingVertical: 10,
          paddingHorizontal: 8,
        },
      }}
      tabBar={({ navigation, state, descriptors }) => (
        <View
          style={{
            flexDirection: 'row',
            backgroundColor: theme.colors.background,
            padding: 8,
            marginHorizontal: 16,
            marginBottom: 16,
            borderRadius: 9999,
            position: 'absolute',
            bottom: 0,
            left: 0,
            right: 0,
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
                  return (props) => <MaterialIcons name="home" {...props} />;
                case 'learn':
                  return (props) => <MaterialIcons name="school" {...props} />;
                case 'play':
                  return (props) => <MaterialIcons name="sports-esports" {...props} />;
                case 'explore':
                  return (props) => <MaterialIcons name="public" {...props} />;
                case 'account':
                  return (props) => <MaterialIcons name="account-circle" {...props} />;
                default:
                  return (props) => <MaterialIcons name="home" {...props} />;
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
  );
}

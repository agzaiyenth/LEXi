import { Tabs } from 'expo-router';
import { View, Text, TouchableOpacity } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { theme } from '../theme';


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
    style={{
      alignItems: 'center',
      justifyContent: 'center',
      paddingVertical: theme.spacing.small,
      flex: 1,
      backgroundColor: isFocused ? theme.colors.primary.medium2 : 'transparent',
      borderRadius: theme.spacing.medium, // Rounded buttons for active tab
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
    {isFocused && (
      <View
        style={{
          position: 'absolute',
          bottom: -4, // Add the line slightly below the button
          height: 2,
          width: '70%',
          backgroundColor: theme.colors.primary.medium2,
          borderRadius: theme.spacing.small,
        }}
      />
    )}
  </TouchableOpacity>
);

export default function TabLayout() {
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
              backgroundColor: theme.colors.primary.light2,
              padding: theme.spacing.small,
              paddingLeft: theme.spacing.medium,
              paddingRight: theme.spacing.medium,
      
              borderTopLeftRadius: theme.spacing.large,
              borderTopRightRadius: theme.spacing.large,
              position: 'absolute',
              bottom: 0,
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
                    return (props: any) => <MaterialIcons name="home" {...props} />;
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

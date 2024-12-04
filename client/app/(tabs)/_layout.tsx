// // app/(tabs)/_layout.tsx
// import { CustomTabBarButton } from '@/components/TabBarButton';
// import { AuthGuard } from '@/hooks/auth/AuthGuard';
// import { MaterialIcons } from '@expo/vector-icons';
// import { Tabs } from 'expo-router';
// import React, { useRef } from 'react';
// import { Animated, View } from 'react-native';
// import { theme } from '../theme';

// export default function TabLayout() {
//   const animatedValue = useRef(new Animated.Value(0)).current;

//   return (
//     <AuthGuard>
//     <View style={{ flex: 1, backgroundColor: theme.colors.background.offWhite }}>
//       <Tabs
//         screenOptions={{
//           headerShown: false,
//         }}
//         tabBar={({ navigation, state, descriptors }) => (
//           <View
//             style={{
//               flexDirection: 'row',
//               padding: theme.spacing.medium,
//               position: 'absolute',
//               bottom: 0,
//               paddingVertical: theme.spacing.small,
//               backgroundColor: theme.colors.primary.light2,
//               borderTopLeftRadius: theme.spacing.large,
//               borderTopRightRadius: theme.spacing.large,
//               width: '100%',
//             }}
//           >
//             {state.routes.map((route, index) => {
//               const { options } = descriptors[route.key];
//               const isFocused = state.index === index;

//               const onPress = () => {
//                 const event = navigation.emit({
//                   type: 'tabPress',
//                   target: route.key,
//                   canPreventDefault: true,
//                 });

//                 if (!isFocused && !event.defaultPrevented) {
//                   navigation.navigate(route.name);
//                 }
//               };

//               const getIcon = () => {
//                 switch (route.name) {
//                   case 'index':
//                     return (props:any) => <MaterialIcons name="home" {...props} />;
//                   case 'learn':
//                     return (props:any) => <MaterialIcons name="apps" {...props} />;
//                   case 'play':
//                     return (props:any) => <MaterialIcons name="sports-esports" {...props} />;
//                   case 'explore':
//                     return (props:any) => <MaterialIcons name="public" {...props} />;
//                   case 'account':
//                     return (props:any) => <MaterialIcons name="account-circle" {...props} />;
//                   default:
//                     return (props:any) => <MaterialIcons name="home" {...props} />;
//                 }
//               };

//               const getLabel = () => {
//                 switch (route.name) {
//                   case 'index':
//                     return 'Home';
//                   case 'learn':
//                     return 'LearnZone';
//                   case 'play':
//                     return 'PlaySpace';
//                   case 'explore':
//                     return 'Explore+';
//                   case 'account':
//                     return 'Account';
//                   default:
//                     return route.name;
//                 }
//               };

//               return (
//                 <CustomTabBarButton
//                   key={route.key}
//                   label={getLabel()}
//                   icon={getIcon()}
//                   isFocused={isFocused}
//                   onPress={onPress}
//                 />
//               );
//             })}
//           </View>
//         )}
//       >
//         <Tabs.Screen name="index" options={{ title: 'Home' }}/>
//         <Tabs.Screen name="learn" />
//         <Tabs.Screen name="play" />
//         <Tabs.Screen name="explore" />
//         <Tabs.Screen name="account" options={{ title: 'Account' }}/>
//       </Tabs>
//     </View>
//     </AuthGuard>
//   );
// }
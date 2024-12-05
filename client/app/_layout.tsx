// app/_layout.tsx
import React from 'react'
import { AuthProvider, useAuth } from '@/context/AuthContext'
import { createStackNavigator } from '@react-navigation/stack'
import { SplashScreen } from 'expo-router'
import AppTabs from './AppTabs'
import AuthStack from './AuthStack'
import { SafeAreaProvider } from 'react-native-safe-area-context'

const Stack = createStackNavigator()

export {
  // Catch any errors thrown by the Layout component.
  ErrorBoundary
} from 'expo-router'

// Prevent the splash screen from auto-hiding before asset loading is complete.
SplashScreen.preventAutoHideAsync()

export default function RootLayout() {
    // const [loaded, error] = useFonts({
  //   SpaceMono: require('../assets/fonts/SpaceMono-Regular.ttf'),
  //   ...FontAwesome.font,
  // })

  // // Handle font loading errors.
  // useEffect(() => {
  //   if (error) throw error
  // }, [error])

  // // Hide the splash screen when fonts are loaded.
  // useEffect(() => {
  //   if (loaded) {
  //     SplashScreen.hideAsync()
  //   }
  // }, [loaded])

  // if (!loaded) {
  //   return null
  // }

  return (
    <AuthProvider>
      {/* <NavigationContainer> */}
      <SafeAreaProvider>
        <RootNavigator />
        </SafeAreaProvider>
      {/* </NavigationContainer> */}
    </AuthProvider>
  )
}

function RootNavigator() {
  const { isSignedIn } = useAuth()

  // !Manulay set isSignedIn to true for testing
  // const isSignedIn = true

  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      {isSignedIn ? (
        <Stack.Screen name="App" component={AppTabs} />
      ) : (
        <Stack.Screen name="Auth" component={AuthStack} />
      )}
    </Stack.Navigator>
  )
}

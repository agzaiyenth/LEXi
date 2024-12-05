// app/_layout.tsx
import { createStackNavigator } from '@react-navigation/stack'
import React from 'react'
// import { SplashScreen } from 'expo-router'
import { Redirect, Slot } from 'expo-router'
import { SafeAreaProvider } from 'react-native-safe-area-context'
import { SessionProvider, useSession } from './ctx'
import { Text } from 'react-native'
const Stack = createStackNavigator();
const { session, isLoading } = useSession();
export {
  // Catch any errors thrown by the Layout component.
  ErrorBoundary
} from 'expo-router'

// Prevent the splash screen from auto-hiding before asset loading is complete.
// SplashScreen.preventAutoHideAsync()

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
  if (isLoading) {
    return <Text>Loading...</Text>; // Show loading state while checking session
  }

  if (!session) {
    console.log('No Session Redirecting to auth')
    return <Redirect href="./(auth)" />; // Redirect unauthenticated users
  }
  return (
    <SessionProvider>
    <SafeAreaProvider>
     
        <Slot />
      
    </SafeAreaProvider>
    </SessionProvider>
  )
}


import { AntDesign, Feather } from '@expo/vector-icons';
import React, { useState } from 'react';
import {
  ActivityIndicator,
  Image,
  SafeAreaView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';
import theme from '../../../src/theme';
import { Link, useRouter } from 'expo-router';
import { showToast } from '@/utils/notifications';

export default function SignUpScreen() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const handleSignUp = async () => {
    // handle sign up logic
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.avatarContainer}>
        <Image
          source={require('@/assets/images/auth/icon.png')}
          style={styles.logo}
          resizeMode="contain"
        />
      </View>

      {/* Welcome Text */}
      <Text style={styles.welcomeText}>Create your account</Text>

      {/* Form */}
      <View style={styles.form}>

      {/* Username Input */}
      <View style={styles.flexColumn}>
        <Text style={styles.label}>Username</Text>
      </View>
      <View style={styles.inputForm}>
        <Feather name="user" size={20} color="#666" />
        <TextInput
          style={styles.input}
          placeholder="Enter your Username"
          value={username}
          onChangeText={setUsername}
          autoCapitalize="none"
        />
      </View>

      {/* Email Input */}
      <View style={styles.flexColumn}>
        <Text style={styles.label}>Email</Text>
      </View>
      <View style={styles.inputForm}>
        <Feather name="mail" size={20} color="#666" />
        <TextInput
          style={styles.input}
          placeholder="Enter your Email"
          value={username}
          onChangeText={setUsername}
          autoCapitalize="none"
        />
      </View>

      {/* Password Input */}
      <View style={styles.flexColumn}>
          <Text style={styles.label}>Password</Text>
        </View>
        <View style={styles.inputForm}>
          <Feather name="lock" size={20} color="#666" />
          <TextInput
            style={styles.input}
            placeholder="Enter your Password"
            value={password}
            onChangeText={setPassword}
            secureTextEntry={!showPassword}
          />
          
        </View>

      {/* Confirm Password Input */}
      <View style={styles.flexColumn}>
          <Text style={styles.label}>Confirm Password</Text>
        </View>
        <View style={styles.inputForm}>
          <Feather name="lock" size={20} color="#666" />
          <TextInput
            style={styles.input}
            placeholder="Confirm your Password"
            value={confirmPassword}
            onChangeText={setConfirmPassword}
            secureTextEntry={!showPassword}
          />
        </View>
      
      


    </View>
    </SafeAreaView>
  );

}
  const styles = StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: theme.colors.background.offWhite,
      padding: 20,
    },
    avatarContainer: {
      marginTop: 40,
      marginBottom: 20,
      alignItems: 'center',
    }, welcomeText: {
      fontSize: 24,
      fontWeight: 'bold',
      marginBottom: 40,
      textAlign: 'center',
      fontFamily: 'serif',
    },
    form: {
      gap: 10,
      backgroundColor: theme.colors.background.offWhite,
      padding: 20,
      borderRadius: 20,
    },
    flexColumn: {
      marginBottom: 5,
    },
    label: {
      color: theme.colors.blacks.dark,
      fontWeight: '600',
    },
    inputForm: {
      flexDirection: 'row',
      alignItems: 'center',
      borderColor: theme.colors.blacks.dark,
      borderWidth: 1.5,
      borderRadius: 10,
      height: 50,
      paddingLeft: 10,
      marginBottom: 10,
    },
    input: {
      flex: 1,
      marginLeft: 10,
    },
    submitButton: {
      backgroundColor: theme.colors.primary.medium,
      height: 50,
      borderRadius: 10,
      justifyContent: 'center',
      alignItems: 'center',
      marginBottom: 10,
    },
    submitButtonText: {
      color: theme.colors.background.beige,
      fontWeight: '500',
      fontSize: 16,
    },
    span: {
      color: theme.colors.primary.medium,
      fontWeight: '500',
      fontSize: 14,
    },
    p: {
      textAlign: 'center',
      color: theme.colors.blacks.dark,
      fontSize: 14,
      marginTop: 10,
    },
    logo: {
      width: 100,
      height: 100,
      marginBottom: 20,
    },




  });

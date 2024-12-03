import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

export default function AuthPage() {
  return (
    <View style={styles.container}>
      <Text style={styles.text}>Authentication Page</Text>
      {/* Add authentication form or logic here */}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  text: {
    fontSize: 18,
  },
});

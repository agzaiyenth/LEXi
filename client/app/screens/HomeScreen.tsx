import { StyleSheet } from 'react-native';

import { Text, View } from '@/components/Themed';
import { theme } from '../theme';
import React from 'react';

export default function HomeScreen() {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>HI LExi</Text>

    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: theme.colors.background.offWhite,
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
  },
  separator: {
    marginVertical: 30,
    height: 1,
    width: '80%',
  },
});

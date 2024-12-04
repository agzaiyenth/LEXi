import theme from '@/app/theme';
import React from 'react';
import { Text, TouchableOpacity } from "react-native";


export const CustomTabBarButton = ({
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
  
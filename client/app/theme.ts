// theme.ts
export const theme = {
  colors: {
    primary: {
      dark1: '#264653',  // Darkest shade
      dark2: '#254d4e',  // Darker shade
      medium: '#2A9D8F', // Medium shade
      medium2:'#98c1b9', // Medium shade
      light: '#A8DADC',  // Lighter shade
      light2: '#D3E0DE', // Lightest shade
    },
    secondary: {
      dark1: '#A17E56',
      dark2: '#',
      medium: '#F4A261',
      light: '#',
      light2: '#FDF5E6',
    },
    accent: {
      dark: '#6D597A',
      medium: '#B56576',
      light: '#E56B6F',
    },
    background: {
      offWhite: '#FFF9EB', // Off-white
      beige: '#EAE2D6', // Beige
      dark: '#D9CAB3',   // Darker beige
    },
  },
  fonts: {
    regular: 'System',
    bold: 'System',
    sizes: {
      small: 12,
      medium: 16,
      large: 20,
      extraLarge: 24,
    },
  },
  spacing: {
    small: 8,
    medium: 16,
    large: 24,
    extraLarge: 32,
  },
  accessibility: {
    highContrast: false,
    fontScale: 1, // 1 for normal, >1 for larger fonts
  },
};
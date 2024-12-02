/** @type {import('tailwindcss').Config} */
module.exports = {
  // NOTE: Update this to include the paths to all of your component files.
  content: [
  "./app/**/*.{js,jsx,ts,tsx}",
  "./src/**/*.{js,jsx,ts,tsx}",
  './screens/**/*.{js,jsx,ts,tsx}',
    './components/**/*.{js,jsx,ts,tsx}',
  ],
  presets: [require("nativewind/preset")],
  theme: {
    extend: {
      colors: {
        darkGreen: '#264653',
        teal: '#2A9D8F',
        lightTeal: '#A8DADC',
        peach: '#F4A261',
        offWhite: '#FDF5E6',
      },
    },
  },
  plugins: [],
}
/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        sans: ['DM Sans', 'sans-serif'],
        mono: ['DM Mono', 'monospace'],
      },
      colors: {
        accent: '#2563eb',
      },
      borderRadius: {
        DEFAULT: '10px',
        lg: '16px',
      },
    },
  },
  plugins: [],
}

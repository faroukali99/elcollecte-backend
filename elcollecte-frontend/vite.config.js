import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  // Proxy API calls to backend during development to avoid CORS issues
  server: {
    proxy: {
      // proxy /api/* to the backend running on localhost:8080
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        // keep the /api prefix; adjust rewrite if your backend expects a different path
        rewrite: (path) => path.replace(/^\/api/, '/api')
      }
    }
  }
})
import { createSlice } from '@reduxjs/toolkit';
import client from '../../api/client';

const initialState = {
  isAuthenticated: !!localStorage.getItem('token'),
  user: (() => {
    try {
      const user = localStorage.getItem('user');
      return user ? JSON.parse(user) : null;
    } catch {
      return null;
    }
  })(),
  token: localStorage.getItem('token') || null,
  loading: false,
  error: null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    loginSuccess: (state, action) => {
      state.isAuthenticated = true;
      state.user = action.payload.user;
      state.token = action.payload.token;
      state.error = null;
    },
    logout: (state) => {
      state.isAuthenticated = false;
      state.user = null;
      state.token = null;
      state.error = null;
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
    },
    setLoading: (state, action) => {
      state.loading = action.payload;
    },
    setError: (state, action) => {
      state.error = action.payload;
    },
  },
});

export const { loginSuccess, logout, setLoading, setError } = authSlice.actions;
export default authSlice.reducer;

/**
 * Service d'authentification — service-utilisateur (via API Gateway)
 * Routes : /api/auth/*
 *
 * AuthResponse backend : { token, refreshToken, tokenType, expiresIn, user: { id, nom, prenom, email, role, organisationId } }
 */
export const authService = {
  /**
   * Connexion utilisateur
   * @param {string} email
   * @param {string} password
   * @returns {Promise<{ token, refreshToken, tokenType, expiresIn, user }>}
   */
  login: async (email, password) => {
    const { data } = await client.post('/auth/login', { email, password });
    // Backend retourne "token" (pas "accessToken")
    if (data.token) localStorage.setItem('token', data.token);
    if (data.refreshToken) localStorage.setItem('refreshToken', data.refreshToken);
    if (data.user) localStorage.setItem('user', JSON.stringify(data.user));
    return data;
  },

  /**
   * Inscription d'un nouvel utilisateur
   * @param {{ nom, prenom, email, password, role, organisationId }} userData
   */
  register: async (userData) => {
    const { data } = await client.post('/auth/register', userData);
    return data;
  },

  /**
   * Rafraîchir le token d'accès
   */
  refreshToken: async () => {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) throw new Error('Aucun refresh token disponible');
    const { data } = await client.post('/auth/refresh', null, {
      headers: { 'X-Refresh-Token': refreshToken },
    });
    if (data.token) localStorage.setItem('token', data.token);
    if (data.refreshToken) localStorage.setItem('refreshToken', data.refreshToken);
    return data;
  },

  /**
   * Déconnexion
   */
  logout: async () => {
    try {
      await client.post('/auth/logout');
    } finally {
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
    }
  },

  /**
   * Récupérer l'utilisateur courant depuis le localStorage
   */
  getCurrentUser: () => {
    try {
      const user = localStorage.getItem('user');
      return user ? JSON.parse(user) : null;
    } catch {
      return null;
    }
  },

  isAuthenticated: () => !!localStorage.getItem('token'),
};

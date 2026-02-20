import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate, Link } from 'react-router-dom';
import { LogIn, Mail, Lock } from 'lucide-react';
import { TextField, Button, CircularProgress, Alert } from '@mui/material';
import styled from 'styled-components';
import { loginSuccess } from '../features/auth/authSlice';
import apiClient from '../api/client'; // Import de votre client API

const LoginContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(to bottom right, #3b82f6, #8b5cf6);
  padding: 1rem;
`;

const LoginFormWrapper = styled.div`
  background-color: white;
  padding: 2rem;
  border-radius: 1rem;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  width: 100%;
  max-width: 28rem;
  transition: transform 0.3s ease-in-out;

  &:hover {
    transform: scale(1.01);
  }
`;

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      const response = await apiClient.post('/auth/login', { email, password });

      // **Correction : Adapter à la réponse réelle du backend**
      const { token, user } = response.data;

      if (!token || !user) {
        throw new Error("Réponse de l'API invalide : token ou utilisateur manquant.");
      }

      localStorage.setItem('token', token);

      dispatch(loginSuccess({ user, token }));

      navigate('/');

    } catch (err) {
      const errorMessage = err.response?.data?.message || err.message || 'Identifiants incorrects ou problème de connexion.';
      setError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <LoginContainer>
      <LoginFormWrapper>
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-800 mb-2">Bienvenue</h1>
          <p className="text-gray-500">Connectez-vous à ElCollecte</p>
        </div>

        {error && (
          <Alert severity="error" style={{ marginBottom: '1.5rem' }}>
            {error}
          </Alert>
        )}

        <form onSubmit={handleSubmit} className="space-y-6">
          <TextField
            label="Email"
            variant="outlined"
            fullWidth
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            InputProps={{
              startAdornment: <Mail className="h-5 w-5 text-gray-400 mr-3" />,
            }}
          />

          <TextField
            label="Mot de passe"
            type="password"
            variant="outlined"
            fullWidth
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            InputProps={{
              startAdornment: <Lock className="h-5 w-5 text-gray-400 mr-3" />,
            }}
          />

          <Button
            type="submit"
            variant="contained"
            fullWidth
            disabled={isLoading}
            size="large"
            startIcon={isLoading ? <CircularProgress size={20} color="inherit" /> : <LogIn />}
          >
            {isLoading ? 'Connexion...' : 'Se connecter'}
          </Button>
        </form>

        <div className="mt-6 text-center text-sm text-gray-600">
          Pas encore de compte ?{' '}
          <Link to="/register" className="text-blue-600 hover:text-blue-800 font-medium hover:underline">
            Créer un compte
          </Link>
        </div>
      </LoginFormWrapper>
    </LoginContainer>
  );
};

export default Login;
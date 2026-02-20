import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { UserPlus, User, Mail, Lock, Briefcase } from 'lucide-react';
import { TextField, Button, CircularProgress, Alert, MenuItem, Select, FormControl, InputLabel, InputAdornment } from '@mui/material';
import styled from 'styled-components';
import apiClient from '../api/client';

const RegisterContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(to bottom right, #3b82f6, #8b5cf6);
  padding: 1rem;
`;

const RegisterFormWrapper = styled.div`
  background-color: white;
  padding: 2rem;
  border-radius: 1rem;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  width: 100%;
  max-width: 32rem;
  transition: transform 0.3s ease-in-out;

  &:hover {
    transform: scale(1.01);
  }
`;

const Register = () => {
  const [formData, setFormData] = useState({
    nom: '',
    prenom: '',
    email: '',
    password: '',
    role: 'ENQUETEUR',
    organisationId: 1,
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setIsLoading(true);

    if (formData.password.length < 8) {
      setError('Le mot de passe doit contenir au moins 8 caractères.');
      setIsLoading(false);
      return;
    }
    // Validation simplifiée pour l'exemple, à renforcer selon vos besoins
    if (!/[0-9]/.test(formData.password)) {
      setError('Le mot de passe doit contenir au moins un chiffre.');
      setIsLoading(false);
      return;
    }

    try {
      // Appel direct à l'API au lieu d'utiliser useAuth
      await apiClient.post('/auth/register', formData);

      setSuccess('Compte créé avec succès ! Redirection...');
      setTimeout(() => navigate('/login'), 2000);
    } catch (err) {
      console.error("Registration error details:", err);
      const message = err.response?.data?.message || err.message || 'Erreur lors de la création du compte.';
      setError(message);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <RegisterContainer>
      <RegisterFormWrapper>
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-800 mb-2">Inscription</h1>
          <p className="text-gray-500">Rejoignez l'équipe ElCollecte</p>
        </div>

        {error && (
          <Alert severity="error" style={{ marginBottom: '1.5rem' }}>
            {error}
          </Alert>
        )}
        {success && (
          <Alert severity="success" style={{ marginBottom: '1.5rem' }}>
            {success}
          </Alert>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <TextField
              label="Nom"
              name="nom"
              variant="outlined"
              fullWidth
              value={formData.nom}
              onChange={handleChange}
              required
              InputProps={{
                startAdornment: <User className="h-5 w-5 text-gray-400 mr-2" />,
              }}
            />
            <TextField
              label="Prénom"
              name="prenom"
              variant="outlined"
              fullWidth
              value={formData.prenom}
              onChange={handleChange}
              required
              InputProps={{
                startAdornment: <User className="h-5 w-5 text-gray-400 mr-2" />,
              }}
            />
          </div>

          <TextField
            label="Email"
            name="email"
            type="email"
            variant="outlined"
            fullWidth
            value={formData.email}
            onChange={handleChange}
            required
            InputProps={{
              startAdornment: <Mail className="h-5 w-5 text-gray-400 mr-2" />,
            }}
          />

          <TextField
            label="Mot de passe"
            name="password"
            type="password"
            variant="outlined"
            fullWidth
            value={formData.password}
            onChange={handleChange}
            required
            helperText="Min. 8 caractères, 1 chiffre."
            InputProps={{
              startAdornment: <Lock className="h-5 w-5 text-gray-400 mr-2" />,
            }}
          />

          <FormControl fullWidth variant="outlined">
            <InputLabel>Rôle</InputLabel>
            <Select
              label="Rôle"
              name="role"
              value={formData.role}
              onChange={handleChange}
              startAdornment={
                <InputAdornment position="start">
                  <Briefcase className="h-5 w-5 text-gray-400" />
                </InputAdornment>
              }
            >
              <MenuItem value="ENQUETEUR">Enquêteur</MenuItem>
              <MenuItem value="CHEF_PROJET">Chef de Projet</MenuItem>
              <MenuItem value="ANALYSTE">Analyste</MenuItem>
              <MenuItem value="ADMIN">Admin</MenuItem>
            </Select>
          </FormControl>

          <Button
            type="submit"
            variant="contained"
            fullWidth
            disabled={isLoading}
            size="large"
            startIcon={isLoading ? <CircularProgress size={20} color="inherit" /> : <UserPlus />}
            sx={{ mt: 2 }}
          >
            {isLoading ? 'Création...' : "S'inscrire"}
          </Button>
        </form>

        <div className="mt-6 text-center text-sm text-gray-600">
          Déjà un compte ?{' '}
          <Link to="/login" className="text-blue-600 hover:text-blue-800 font-medium hover:underline">
            Se connecter
          </Link>
        </div>
      </RegisterFormWrapper>
    </RegisterContainer>
  );
};

export default Register;
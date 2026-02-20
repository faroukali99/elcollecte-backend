import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import Layout from './components/Layout';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Projets from './pages/Projets';
import Collecte from './pages/Collecte';
import Validation from './pages/Validation';

// Composant pour protéger les routes
const PrivateRoute = ({ children }) => {
  const { isAuthenticated } = useSelector((state) => state.auth);
  return isAuthenticated ? children : <Navigate to="/login" replace />;
};

// Composant pour rediriger si déjà connecté
const PublicRoute = ({ children }) => {
  const { isAuthenticated } = useSelector((state) => state.auth);
  return isAuthenticated ? <Navigate to="/" replace /> : children;
};

function App() {
  return (
    <Router>
      <Routes>
        {/* Routes publiques (Login, Register) */}
        <Route
          path="/login"
          element={
            <PublicRoute>
              <Login />
            </PublicRoute>
          }
        />
        <Route
          path="/register"
          element={
            <PublicRoute>
              <Register />
            </PublicRoute>
          }
        />

        {/* Routes protégées (Dashboard, etc.) */}
        <Route
          path="/"
          element={
            <PrivateRoute>
              <Layout />
            </PrivateRoute>
          }
        >
          <Route index element={<Dashboard />} />
          <Route path="projets" element={<Projets />} />
          <Route path="collecte" element={<Collecte />} />
          <Route path="validation" element={<Validation />} />
        </Route>
      </Routes>
    </Router>
  );
}

export default App;
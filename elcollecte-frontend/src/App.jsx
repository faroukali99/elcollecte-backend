import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Layout from './components/Layout';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Projets from './pages/Projets';
import Collecte from './pages/Collecte';
import Validation from './pages/Validation';

const PrivateRoute = ({ children }) => {
  const { user } = useAuth();
  return user ? children : <Navigate to="/login" />;
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/" element={<PrivateRoute><Layout /></PrivateRoute>}>
            <Route index element={<Dashboard />} />
            <Route path="projets" element={<Projets />} />
            <Route path="collecte" element={<Collecte />} />
            <Route path="validation" element={<Validation />} />
          </Route>
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
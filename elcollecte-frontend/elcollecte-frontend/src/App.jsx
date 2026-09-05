import React from 'react';
import {BrowserRouter as Router, Routes, Route, Navigate} from 'react-router-dom';
import {useSelector} from 'react-redux';
import Layout from './components/Layout';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Projets from './pages/Projets';
import GestionProjets from './pages/GestionProjets';
import Collecte from './pages/Collecte';
import Validation from './pages/Validation';
import Analytics from './pages/Analytique';
import Rapport from './pages/Rapport';
import Utilisateurs from './pages/admin/Utilisateurs';
import Profils from './pages/admin/Profils';
import Permissions from './pages/admin/Permissions';
import { hasPermission } from './utils/permissions';

// Composant pour protéger les routes
const PrivateRoute = ({children}) => {
    const {isAuthenticated} = useSelector((state) => state.auth);
    return isAuthenticated ? children : <Navigate to="/login" replace/>;
};

// Composant pour rediriger si déjà connecté
const PublicRoute = ({children}) => {
    const {isAuthenticated} = useSelector((state) => state.auth);
    return isAuthenticated ? <Navigate to="/" replace/> : children;
};

// Garde additionnelle par permission dynamique, pour les pages d'administration.
// Toujours combinée à PrivateRoute (l'utilisateur doit déjà être authentifié).
// Rappel : cette garde est un confort d'affichage, pas la sécurité réelle —
// le backend revérifie systématiquement via @PreAuthorize.
const PermissionRoute = ({children, permission}) => {
    const {user} = useSelector((state) => state.auth);
    return hasPermission(user, permission) ? children : <Navigate to="/" replace/>;
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
                            <Login/>
                        </PublicRoute>
                    }
                />
                <Route
                    path="/register"
                    element={
                        <PublicRoute>
                            <Register/>
                        </PublicRoute>
                    }
                />

                {/* Routes protégées (Dashboard, etc.) */}
                <Route
                    path="/"
                    element={
                        <PrivateRoute>
                            <Layout/>
                        </PrivateRoute>
                    }
                >
                    <Route index element={<Dashboard/>}/>
                    <Route path="projets" element={<Projets/>}/>
                    <Route path="gestion-projets" element={<GestionProjets/>}/>
                    <Route path="collecte" element={<Collecte/>}/>
                    <Route path="validation" element={<Validation/>}/>
                    <Route path="analytics" element={<Analytics/>}/>
                    <Route path="analytique" element={<Analytics/>}/>
                    <Route path="rapport" element={<Rapport/>}/>
                    <Route
                        path="admin/utilisateurs"
                        element={
                            <PermissionRoute permission="USER_READ">
                                <Utilisateurs/>
                            </PermissionRoute>
                        }
                    />
                    <Route
                        path="admin/profils"
                        element={
                            <PermissionRoute permission="PROFILE_READ">
                                <Profils/>
                            </PermissionRoute>
                        }
                    />
                    <Route
                        path="admin/permissions"
                        element={
                            <PermissionRoute permission="PROFILE_READ">
                                <Permissions/>
                            </PermissionRoute>
                        }
                    />
                </Route>
            </Routes>
        </Router>
    );
}

export default App;

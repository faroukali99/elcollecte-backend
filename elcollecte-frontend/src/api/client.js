import axios from 'axios';

/**
 * Client HTTP centralisé pour tous les appels vers l'API Gateway.
 *
 * Fonctionnalités :
 * - Injection automatique du Bearer token
 * - Refresh token automatique en cas de 401
 * - Redirection vers /login si le refresh échoue
 * - File d'attente des requêtes pendant le refresh (évite les races conditions)
 */

const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const client = axios.create({
    baseURL: BASE_URL,
    headers: { 'Content-Type': 'application/json' },
    timeout: 30_000, // 30 secondes
});

// ── État du refresh ──────────────────────────────────────────────────────────
let isRefreshing = false;
let failedQueue  = [];

const processQueue = (error, token = null) => {
    failedQueue.forEach(prom => {
        if (error) prom.reject(error);
        else       prom.resolve(token);
    });
    failedQueue = [];
};

// ── Intercepteur de requête : injecte le JWT ─────────────────────────────────
client.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// ── Intercepteur de réponse : gère les 401 avec refresh automatique ──────────
client.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        // Si ce n'est pas un 401 ou que c'est déjà une tentative de refresh → rejeter
        if (
            !error.response ||
            error.response.status !== 401 ||
            originalRequest._retry ||
            originalRequest.url?.includes('/auth/login') ||
            originalRequest.url?.includes('/auth/refresh')
        ) {
            return Promise.reject(error);
        }

        if (isRefreshing) {
            // Mettre en attente pendant que le refresh est en cours
            return new Promise((resolve, reject) => {
                failedQueue.push({ resolve, reject });
            }).then(token => {
                originalRequest.headers.Authorization = `Bearer ${token}`;
                return client(originalRequest);
            }).catch(err => Promise.reject(err));
        }

        originalRequest._retry = true;
        isRefreshing = true;

        const refreshToken = localStorage.getItem('refreshToken');

        if (!refreshToken) {
            isRefreshing = false;
            redirectToLogin();
            return Promise.reject(error);
        }

        try {
            const response = await axios.post(
                `${BASE_URL}/auth/refresh`,
                null,
                { headers: { 'X-Refresh-Token': refreshToken } }
            );

            const newToken = response.data.token;
            localStorage.setItem('token', newToken);
            if (response.data.refreshToken) {
                localStorage.setItem('refreshToken', response.data.refreshToken);
            }

            client.defaults.headers.common.Authorization = `Bearer ${newToken}`;
            processQueue(null, newToken);

            originalRequest.headers.Authorization = `Bearer ${newToken}`;
            return client(originalRequest);

        } catch (refreshError) {
            processQueue(refreshError, null);
            redirectToLogin();
            return Promise.reject(refreshError);
        } finally {
            isRefreshing = false;
        }
    }
);

function redirectToLogin() {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    // Évite une boucle si on est déjà sur /login
    if (!window.location.pathname.includes('/login')) {
        window.location.href = '/login';
    }
}

export default client;

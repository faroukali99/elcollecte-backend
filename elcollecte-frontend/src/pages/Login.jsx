import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate, Link } from 'react-router-dom';
import { Mail, Lock, ArrowRight, Eye, EyeOff } from 'lucide-react';
import { loginSuccess } from '../features/auth/authSlice';
import apiClient from '../api/client';

const Login = () => {
  const [email,    setEmail]    = useState('');
  const [password, setPassword] = useState('');
  const [showPwd,  setShowPwd]  = useState(false);
  const [error,    setError]    = useState('');
  const [loading,  setLoading]  = useState(false);
  const dispatch  = useDispatch();
  const navigate  = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const { data } = await apiClient.post('/auth/login', { email, password });
      const { token, user } = data;
      if (!token || !user) throw new Error("Réponse API invalide");
      localStorage.setItem('token', token);
      dispatch(loginSuccess({ user, token }));
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message ?? err.message ?? 'Identifiants incorrects.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh', display: 'flex',
      background: '#0f1117',
      fontFamily: 'var(--font-sans)',
    }}>
      {/* Left panel — branding */}
      <div style={{
        flex: 1, display: 'flex', flexDirection: 'column',
        padding: 48, position: 'relative', overflow: 'hidden',
      }} className="login-left-panel">
        {/* Background geometry */}
        <div style={{
          position: 'absolute', inset: 0, zIndex: 0,
          background: 'radial-gradient(ellipse at 30% 60%, rgba(37,99,235,0.18) 0%, transparent 60%), radial-gradient(ellipse at 70% 20%, rgba(124,58,237,0.12) 0%, transparent 50%)',
        }} />
        {/* Grid overlay */}
        <div style={{
          position: 'absolute', inset: 0,
          backgroundImage: 'linear-gradient(rgba(255,255,255,0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,0.03) 1px, transparent 1px)',
          backgroundSize: '48px 48px',
          zIndex: 0,
        }} />

        {/* Logo */}
        <div style={{ position: 'relative', zIndex: 1 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 'auto' }}>
            <div style={{
              width: 36, height: 36, borderRadius: 10,
              background: 'linear-gradient(135deg, #2563eb, #7c3aed)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              boxShadow: '0 4px 16px rgba(37,99,235,0.35)',
            }}>
              <span style={{ color: '#fff', fontWeight: 800, fontSize: '1rem' }}>E</span>
            </div>
            <span style={{ color: '#fff', fontWeight: 700, fontSize: '1.05rem', letterSpacing: '-0.02em' }}>
              ElCollecte
            </span>
          </div>
        </div>

        {/* Tagline */}
        <div style={{ position: 'relative', zIndex: 1, marginTop: 'auto', paddingBottom: 24 }}>
          <p style={{
            fontSize: '2rem', fontWeight: 700, color: '#fff',
            lineHeight: 1.25, letterSpacing: '-0.03em',
            margin: '0 0 16px',
          }}>
            Collecte terrain,<br />
            <span style={{ color: '#60a5fa' }}>simplifiée.</span>
          </p>
          <p style={{ color: 'rgba(255,255,255,0.45)', fontSize: '0.9rem', margin: 0, maxWidth: 340, lineHeight: 1.6 }}>
            Gérez vos projets de collecte de données, coordonnez vos équipes et validez vos données terrain en temps réel.
          </p>
        </div>
      </div>

      {/* Right panel — form */}
      <div style={{
        width: 440, display: 'flex', flexDirection: 'column',
        justifyContent: 'center', padding: '48px 40px',
        background: '#f4f3f0',
        position: 'relative',
      }} className="login-right-panel">
        <div className="animate-fade-up">
          <h1 style={{ margin: '0 0 6px', fontSize: '1.5rem', fontWeight: 700, color: 'var(--c-ink)', letterSpacing: '-0.02em' }}>
            Connexion
          </h1>
          <p style={{ margin: '0 0 32px', color: 'var(--c-ink-2)', fontSize: '0.875rem' }}>
            Bienvenue ! Entrez vos identifiants.
          </p>

          {error && (
            <div style={{
              padding: '12px 14px', borderRadius: 10, marginBottom: 20,
              background: '#fee2e2', border: '1px solid #fca5a5',
              color: '#991b1b', fontSize: '0.855rem', fontWeight: 500,
            }}>
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            {/* Email */}
            <div>
              <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, color: 'var(--c-ink)', marginBottom: 6 }}>
                Adresse e-mail
              </label>
              <div style={{ position: 'relative' }}>
                <Mail size={15} style={{ position: 'absolute', left: 12, top: '50%', transform: 'translateY(-50%)', color: 'var(--c-ink-3)' }} />
                <input
                  type="email"
                  value={email}
                  onChange={e => setEmail(e.target.value)}
                  required
                  placeholder="vous@organisation.com"
                  className="input-field"
                  style={{ paddingLeft: 36 }}
                />
              </div>
            </div>

            {/* Password */}
            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 6 }}>
                <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--c-ink)' }}>
                  Mot de passe
                </label>
                <a href="#" style={{ fontSize: '0.78rem', color: 'var(--c-accent)', textDecoration: 'none', fontWeight: 500 }}>
                  Mot de passe oublié ?
                </a>
              </div>
              <div style={{ position: 'relative' }}>
                <Lock size={15} style={{ position: 'absolute', left: 12, top: '50%', transform: 'translateY(-50%)', color: 'var(--c-ink-3)' }} />
                <input
                  type={showPwd ? 'text' : 'password'}
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  required
                  placeholder="••••••••"
                  className="input-field"
                  style={{ paddingLeft: 36, paddingRight: 40 }}
                />
                <button
                  type="button"
                  onClick={() => setShowPwd(v => !v)}
                  style={{
                    position: 'absolute', right: 10, top: '50%', transform: 'translateY(-50%)',
                    border: 'none', background: 'transparent', cursor: 'pointer',
                    color: 'var(--c-ink-3)', padding: 4, display: 'flex', alignItems: 'center',
                  }}
                >
                  {showPwd ? <EyeOff size={15} /> : <Eye size={15} />}
                </button>
              </div>
            </div>

            {/* Submit */}
            <button
              type="submit"
              disabled={loading}
              style={{
                marginTop: 4,
                width: '100%', padding: '11px 0',
                border: 'none', borderRadius: 10,
                background: loading ? '#93c5fd' : 'var(--c-accent)',
                color: '#fff', fontWeight: 700, fontSize: '0.9rem',
                fontFamily: 'var(--font-sans)',
                cursor: loading ? 'not-allowed' : 'pointer',
                display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8,
                transition: 'background 0.2s, transform 0.15s, box-shadow 0.2s',
                boxShadow: loading ? 'none' : '0 4px 12px rgba(37,99,235,0.3)',
              }}
              onMouseEnter={e => { if (!loading) { e.currentTarget.style.background = 'var(--c-accent-2)'; e.currentTarget.style.transform = 'translateY(-1px)'; }}}
              onMouseLeave={e => { if (!loading) { e.currentTarget.style.background = 'var(--c-accent)'; e.currentTarget.style.transform = 'translateY(0)'; }}}
            >
              {loading ? 'Connexion en cours…' : (
                <> Se connecter <ArrowRight size={16} /> </>
              )}
            </button>
          </form>

          <p style={{ marginTop: 24, textAlign: 'center', fontSize: '0.855rem', color: 'var(--c-ink-2)' }}>
            Pas encore de compte ?{' '}
            <Link to="/register" style={{ color: 'var(--c-accent)', fontWeight: 600, textDecoration: 'none' }}>
              Créer un compte
            </Link>
          </p>
        </div>
      </div>

      <style>{`
        @media (max-width: 768px) {
          .login-left-panel { display: none !important; }
          .login-right-panel { width: 100% !important; }
        }
      `}</style>
    </div>
  );
};

export default Login;

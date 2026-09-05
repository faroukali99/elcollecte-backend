import React, { useState, useEffect } from 'react';
import { Users, Plus, X, AlertCircle, CheckCircle2, Power } from 'lucide-react';
import { usersApi, profilsApi } from '../../api/adminApi';
import { useSelector } from 'react-redux';

const Utilisateurs = () => {
  const { user: currentUser } = useSelector((state) => state.auth);
  const [users, setUsers] = useState([]);
  const [profils, setProfils] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [showCreateModal, setShowCreateModal] = useState(false);
  const [form, setForm] = useState({ nom: '', prenom: '', email: '', password: '', profilId: '' });
  const [saving, setSaving] = useState(false);

  const [editingUserId, setEditingUserId] = useState(null);
  const [editProfilId, setEditProfilId] = useState('');

  const organisationId = currentUser?.organisationId;

  const fetchAll = async () => {
    setLoading(true);
    try {
      const [usersPage, profilsPage] = await Promise.all([
        usersApi.list(0, 50),
        profilsApi.list(0, 50),
      ]);
      setUsers(usersPage.content ?? usersPage ?? []);
      setProfils((profilsPage.content ?? profilsPage ?? []).filter(p => p.active));
    } catch (err) {
      console.error('Erreur lors du chargement des utilisateurs:', err);
      setError('Impossible de charger les utilisateurs');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchAll(); }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    setError(''); setSuccess('');
    if (!form.nom.trim() || !form.prenom.trim() || !form.email.trim() || !form.password || !form.profilId) {
      setError('Tous les champs sont obligatoires');
      return;
    }
    if (!organisationId) {
      setError("Impossible de déterminer l'organisation courante");
      return;
    }
    setSaving(true);
    try {
      await usersApi.create({
        nom: form.nom.trim(),
        prenom: form.prenom.trim(),
        email: form.email.trim(),
        password: form.password,
        organisationId,
        profilId: Number(form.profilId),
      });
      setSuccess(`Utilisateur "${form.prenom} ${form.nom}" créé`);
      setForm({ nom: '', prenom: '', email: '', password: '', profilId: '' });
      setShowCreateModal(false);
      fetchAll();
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la création');
    } finally {
      setSaving(false);
    }
  };

  const handleAssignProfil = async (userId) => {
    if (!editProfilId) return;
    setError(''); setSuccess('');
    try {
      await usersApi.assignProfil(userId, Number(editProfilId));
      setSuccess('Profil mis à jour');
      setEditingUserId(null);
      fetchAll();
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors du changement de profil');
    }
  };

  const handleToggleActive = async (u) => {
    setError(''); setSuccess('');
    try {
      if (u.active) {
        await usersApi.desactiver(u.id);
        setSuccess(`Utilisateur "${u.prenom} ${u.nom}" désactivé`);
      } else {
        await usersApi.reactiver(u.id);
        setSuccess(`Utilisateur "${u.prenom} ${u.nom}" réactivé`);
      }
      fetchAll();
    } catch (err) {
      setError(err.response?.data?.message || 'Action impossible');
    }
  };

  return (
    <div style={{ padding: 24 }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 }}>
        <div>
          <h1 style={{ fontSize: '1.4rem', fontWeight: 700, margin: 0, display: 'flex', alignItems: 'center', gap: 10 }}>
            <Users size={22} /> Utilisateurs
          </h1>
          <p style={{ color: 'var(--c-ink-2)', fontSize: '0.875rem', marginTop: 4 }}>
            Créer des comptes et leur affecter un profil.
          </p>
        </div>
        <button
          onClick={() => setShowCreateModal(true)}
          style={{
            display: 'flex', alignItems: 'center', gap: 8,
            padding: '10px 16px', borderRadius: 8, border: 'none',
            background: '#2563eb', color: '#fff', fontWeight: 600,
            fontSize: '0.875rem', cursor: 'pointer',
          }}
        >
          <Plus size={16} /> Nouvel utilisateur
        </button>
      </div>

      {error && (
        <div style={{ display: 'flex', gap: 8, alignItems: 'center', padding: '10px 14px', background: '#fee2e2', color: '#b91c1c', borderRadius: 8, marginBottom: 16, fontSize: '0.875rem' }}>
          <AlertCircle size={16} /> {error}
        </div>
      )}
      {success && (
        <div style={{ display: 'flex', gap: 8, alignItems: 'center', padding: '10px 14px', background: '#d1fae5', color: '#047857', borderRadius: 8, marginBottom: 16, fontSize: '0.875rem' }}>
          <CheckCircle2 size={16} /> {success}
        </div>
      )}

      {loading ? (
        <p style={{ color: 'var(--c-ink-2)' }}>Chargement…</p>
      ) : (
        <div style={{ background: 'var(--c-surface)', border: '1px solid var(--c-border)', borderRadius: 12, overflow: 'hidden' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.85rem' }}>
            <thead>
              <tr style={{ background: '#faf9f7', textAlign: 'left' }}>
                <th style={thStyle}>Nom</th>
                <th style={thStyle}>Email</th>
                <th style={thStyle}>Profil</th>
                <th style={thStyle}>Statut</th>
                <th style={thStyle}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map(u => (
                <tr key={u.id} style={{ borderTop: '1px solid var(--c-border)', opacity: u.active ? 1 : 0.55 }}>
                  <td style={tdStyle}>{u.prenom} {u.nom}</td>
                  <td style={tdStyle}>{u.email}</td>
                  <td style={tdStyle}>
                    {editingUserId === u.id ? (
                      <div style={{ display: 'flex', gap: 6 }}>
                        <select
                          value={editProfilId}
                          onChange={(e) => setEditProfilId(e.target.value)}
                          style={{ padding: '4px 8px', borderRadius: 6, border: '1px solid var(--c-border)', fontSize: '0.8rem' }}
                        >
                          <option value="">Choisir…</option>
                          {profils.map(p => (
                            <option key={p.id} value={p.id}>{p.libelle}</option>
                          ))}
                        </select>
                        <button onClick={() => handleAssignProfil(u.id)} style={{ ...actionBtnStyle, padding: '4px 8px' }}>OK</button>
                        <button onClick={() => setEditingUserId(null)} style={{ ...actionBtnStyle, padding: '4px 8px' }}>
                          <X size={12} />
                        </button>
                      </div>
                    ) : (
                      <button
                        onClick={() => { setEditingUserId(u.id); setEditProfilId(String(u.profilId ?? '')); }}
                        style={{ border: 'none', background: 'transparent', cursor: 'pointer', padding: 0, textAlign: 'left' }}
                      >
                        <span style={{ fontWeight: 600 }}>{u.profilLibelle ?? '—'}</span>
                        {u.profilLibelle && <span style={{ color: 'var(--c-ink-2)', fontSize: '0.72rem' }}> (modifier)</span>}
                      </button>
                    )}
                  </td>
                  <td style={tdStyle}>
                    {u.active ? (
                      <span style={{ fontSize: '0.7rem', fontWeight: 700, color: '#047857', background: '#d1fae5', padding: '2px 8px', borderRadius: 6 }}>Actif</span>
                    ) : (
                      <span style={{ fontSize: '0.7rem', fontWeight: 700, color: '#b45309', background: '#fef3c7', padding: '2px 8px', borderRadius: 6 }}>Désactivé</span>
                    )}
                  </td>
                  <td style={tdStyle}>
                    <button onClick={() => handleToggleActive(u)} style={actionBtnStyle}>
                      <Power size={13} /> {u.active ? 'Désactiver' : 'Réactiver'}
                    </button>
                  </td>
                </tr>
              ))}
              {users.length === 0 && (
                <tr><td colSpan={5} style={{ ...tdStyle, textAlign: 'center', color: 'var(--c-ink-2)' }}>Aucun utilisateur</td></tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {showCreateModal && (
        <div style={{
          position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.4)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 50,
        }}>
          <div style={{ background: '#fff', borderRadius: 12, padding: 24, width: 420, maxWidth: '90vw' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
              <h2 style={{ margin: 0, fontSize: '1.1rem', fontWeight: 700 }}>Nouvel utilisateur</h2>
              <button onClick={() => setShowCreateModal(false)} style={{ border: 'none', background: 'transparent', cursor: 'pointer' }}>
                <X size={18} />
              </button>
            </div>
            <form onSubmit={handleCreate} style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
              <input placeholder="Prénom" value={form.prenom} onChange={(e) => setForm({ ...form, prenom: e.target.value })} style={inputStyle} />
              <input placeholder="Nom" value={form.nom} onChange={(e) => setForm({ ...form, nom: e.target.value })} style={inputStyle} />
              <input type="email" placeholder="Email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} style={inputStyle} />
              <input type="password" placeholder="Mot de passe (min. 8, 1 maj., 1 chiffre)" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} style={inputStyle} />
              <select value={form.profilId} onChange={(e) => setForm({ ...form, profilId: e.target.value })} style={inputStyle}>
                <option value="">Profil…</option>
                {profils.map(p => <option key={p.id} value={p.id}>{p.libelle}</option>)}
              </select>
              <button
                type="submit"
                disabled={saving}
                style={{
                  padding: '10px 16px', borderRadius: 8, border: 'none',
                  background: '#2563eb', color: '#fff', fontWeight: 600,
                  fontSize: '0.875rem', cursor: saving ? 'not-allowed' : 'pointer',
                  opacity: saving ? 0.7 : 1,
                }}
              >
                {saving ? 'Création…' : 'Créer'}
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

const inputStyle = {
  padding: '10px 12px', borderRadius: 8, border: '1px solid var(--c-border)',
  fontSize: '0.875rem', fontFamily: 'inherit',
};

const thStyle = { padding: '10px 14px', fontSize: '0.72rem', fontWeight: 700, textTransform: 'uppercase', color: 'var(--c-ink-2)' };
const tdStyle = { padding: '10px 14px' };

const actionBtnStyle = {
  display: 'flex', alignItems: 'center', gap: 6,
  padding: '6px 10px', borderRadius: 7, border: '1px solid var(--c-border)',
  background: 'transparent', cursor: 'pointer', fontSize: '0.78rem', fontWeight: 600,
  color: 'var(--c-ink)',
};

export default Utilisateurs;

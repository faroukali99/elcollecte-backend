import React, { useState, useEffect } from 'react';
import { KeyRound, Plus, X, AlertCircle, CheckCircle2 } from 'lucide-react';
import { permissionsApi } from '../../api/adminApi';

const CATEGORIES = [
  'UTILISATEUR', 'PROFIL', 'PROJET', 'FORMULAIRE', 'MISSION',
  'COLLECTE', 'CARTOGRAPHIE', 'ZONE', 'ANALYSE', 'RAPPORT',
  'AUDIT', 'SYNCHRONISATION',
];

const Permissions = () => {
  const [permissions, setPermissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [filterCategorie, setFilterCategorie] = useState('');
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [form, setForm] = useState({ code: '', libelle: '', categorie: '', description: '' });
  const [saving, setSaving] = useState(false);

  const fetchPermissions = async () => {
    setLoading(true);
    try {
      const data = await permissionsApi.list(filterCategorie || undefined);
      setPermissions(data ?? []);
    } catch (err) {
      console.error('Erreur lors du chargement des permissions:', err);
      setError('Impossible de charger les permissions');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchPermissions(); }, [filterCategorie]);

  const grouped = permissions.reduce((acc, p) => {
    (acc[p.categorie] = acc[p.categorie] || []).push(p);
    return acc;
  }, {});

  const handleCreate = async (e) => {
    e.preventDefault();
    setError(''); setSuccess('');
    if (!form.code.trim() || !form.libelle.trim() || !form.categorie.trim()) {
      setError('Code, libellé et catégorie sont obligatoires');
      return;
    }
    setSaving(true);
    try {
      await permissionsApi.create({
        code: form.code.trim().toUpperCase(),
        libelle: form.libelle.trim(),
        categorie: form.categorie.trim().toUpperCase(),
        description: form.description.trim() || null,
      });
      setSuccess(`Permission "${form.code}" créée`);
      setForm({ code: '', libelle: '', categorie: '', description: '' });
      setShowCreateModal(false);
      fetchPermissions();
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la création');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div style={{ padding: 24 }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 }}>
        <div>
          <h1 style={{ fontSize: '1.4rem', fontWeight: 700, margin: 0, display: 'flex', alignItems: 'center', gap: 10 }}>
            <KeyRound size={22} /> Permissions
          </h1>
          <p style={{ color: 'var(--c-ink-2)', fontSize: '0.875rem', marginTop: 4 }}>
            Catalogue des permissions disponibles, associables aux profils.
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
          <Plus size={16} /> Nouvelle permission
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

      <div style={{ marginBottom: 16 }}>
        <select
          value={filterCategorie}
          onChange={(e) => setFilterCategorie(e.target.value)}
          style={{ padding: '8px 12px', borderRadius: 8, border: '1px solid var(--c-border)', fontSize: '0.875rem' }}
        >
          <option value="">Toutes les catégories</option>
          {CATEGORIES.map(c => <option key={c} value={c}>{c}</option>)}
        </select>
      </div>

      {loading ? (
        <p style={{ color: 'var(--c-ink-2)' }}>Chargement…</p>
      ) : permissions.length === 0 ? (
        <p style={{ color: 'var(--c-ink-2)' }}>Aucune permission trouvée.</p>
      ) : (
        Object.entries(grouped).map(([categorie, items]) => (
          <div key={categorie} style={{ marginBottom: 24 }}>
            <h3 style={{ fontSize: '0.8rem', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.05em', color: 'var(--c-ink-2)', marginBottom: 8 }}>
              {categorie}
            </h3>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))', gap: 10 }}>
              {items.map(p => (
                <div key={p.id} style={{
                  padding: '12px 14px', borderRadius: 10, border: '1px solid var(--c-border)',
                  background: 'var(--c-surface)',
                }}>
                  <code style={{ fontSize: '0.75rem', fontWeight: 700, color: '#2563eb' }}>{p.code}</code>
                  <p style={{ margin: '4px 0 0', fontSize: '0.85rem', fontWeight: 600 }}>{p.libelle}</p>
                  {p.description && (
                    <p style={{ margin: '4px 0 0', fontSize: '0.78rem', color: 'var(--c-ink-2)' }}>{p.description}</p>
                  )}
                </div>
              ))}
            </div>
          </div>
        ))
      )}

      {showCreateModal && (
        <div style={{
          position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.4)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 50,
        }}>
          <div style={{ background: '#fff', borderRadius: 12, padding: 24, width: 420, maxWidth: '90vw' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
              <h2 style={{ margin: 0, fontSize: '1.1rem', fontWeight: 700 }}>Nouvelle permission</h2>
              <button onClick={() => setShowCreateModal(false)} style={{ border: 'none', background: 'transparent', cursor: 'pointer' }}>
                <X size={18} />
              </button>
            </div>
            <form onSubmit={handleCreate} style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
              <input
                placeholder="Code (ex: ZONE_ARCHIVE)"
                value={form.code}
                onChange={(e) => setForm({ ...form, code: e.target.value })}
                style={inputStyle}
              />
              <input
                placeholder="Libellé (ex: Archiver une zone)"
                value={form.libelle}
                onChange={(e) => setForm({ ...form, libelle: e.target.value })}
                style={inputStyle}
              />
              <select
                value={form.categorie}
                onChange={(e) => setForm({ ...form, categorie: e.target.value })}
                style={inputStyle}
              >
                <option value="">Catégorie…</option>
                {CATEGORIES.map(c => <option key={c} value={c}>{c}</option>)}
              </select>
              <textarea
                placeholder="Description (optionnel)"
                value={form.description}
                onChange={(e) => setForm({ ...form, description: e.target.value })}
                style={{ ...inputStyle, minHeight: 60, resize: 'vertical' }}
              />
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

export default Permissions;

import React, { useState, useEffect } from 'react';
import { UserCog, Plus, X, AlertCircle, CheckCircle2, Lock, Power, Trash2, Settings2 } from 'lucide-react';
import { profilsApi, permissionsApi } from '../../api/adminApi';

const Profils = () => {
  const [profils, setProfils] = useState([]);
  const [allPermissions, setAllPermissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [showCreateModal, setShowCreateModal] = useState(false);
  const [form, setForm] = useState({ code: '', libelle: '', description: '' });
  const [saving, setSaving] = useState(false);

  const [permModalProfil, setPermModalProfil] = useState(null);
  const [selectedPermIds, setSelectedPermIds] = useState(new Set());

  const fetchAll = async () => {
    setLoading(true);
    try {
      const [profilsPage, perms] = await Promise.all([
        profilsApi.list(0, 50),
        permissionsApi.list(),
      ]);
      setProfils(profilsPage.content ?? profilsPage ?? []);
      setAllPermissions(perms ?? []);
    } catch (err) {
      console.error('Erreur lors du chargement des profils:', err);
      setError('Impossible de charger les profils');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchAll(); }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    setError(''); setSuccess('');
    if (!form.code.trim() || !form.libelle.trim()) {
      setError('Code et libellé sont obligatoires');
      return;
    }
    setSaving(true);
    try {
      await profilsApi.create({
        code: form.code.trim().toUpperCase(),
        libelle: form.libelle.trim(),
        description: form.description.trim() || null,
      });
      setSuccess(`Profil "${form.libelle}" créé`);
      setForm({ code: '', libelle: '', description: '' });
      setShowCreateModal(false);
      fetchAll();
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la création');
    } finally {
      setSaving(false);
    }
  };

  const handleToggleActive = async (profil) => {
    setError(''); setSuccess('');
    try {
      if (profil.active) {
        await profilsApi.desactiver(profil.id);
        setSuccess(`Profil "${profil.libelle}" désactivé`);
      } else {
        await profilsApi.reactiver(profil.id);
        setSuccess(`Profil "${profil.libelle}" réactivé`);
      }
      fetchAll();
    } catch (err) {
      setError(err.response?.data?.message || "Action impossible sur ce profil");
    }
  };

  const handleDelete = async (profil) => {
    if (!window.confirm(`Supprimer le profil "${profil.libelle}" ?`)) return;
    setError(''); setSuccess('');
    try {
      await profilsApi.delete(profil.id);
      setSuccess(`Profil "${profil.libelle}" supprimé`);
      fetchAll();
    } catch (err) {
      setError(err.response?.data?.message || 'Suppression impossible');
    }
  };

  const openPermModal = (profil) => {
    setPermModalProfil(profil);
    setSelectedPermIds(new Set((profil.permissions ?? []).map(p => p.id)));
  };

  const togglePerm = (id) => {
    setSelectedPermIds(prev => {
      const next = new Set(prev);
      next.has(id) ? next.delete(id) : next.add(id);
      return next;
    });
  };

  const savePermissions = async () => {
    if (!permModalProfil) return;
    setError(''); setSuccess('');
    const currentIds = new Set((permModalProfil.permissions ?? []).map(p => p.id));
    const toAdd = [...selectedPermIds].filter(id => !currentIds.has(id));
    const toRemove = [...currentIds].filter(id => !selectedPermIds.has(id));
    try {
      if (toAdd.length > 0) {
        await profilsApi.assignPermissions(permModalProfil.id, toAdd);
      }
      for (const id of toRemove) {
        await profilsApi.removePermission(permModalProfil.id, id);
      }
      setSuccess(`Permissions du profil "${permModalProfil.libelle}" mises à jour`);
      setPermModalProfil(null);
      fetchAll();
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la mise à jour des permissions');
    }
  };

  const grouped = allPermissions.reduce((acc, p) => {
    (acc[p.categorie] = acc[p.categorie] || []).push(p);
    return acc;
  }, {});

  return (
    <div style={{ padding: 24 }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 }}>
        <div>
          <h1 style={{ fontSize: '1.4rem', fontWeight: 700, margin: 0, display: 'flex', alignItems: 'center', gap: 10 }}>
            <UserCog size={22} /> Profils
          </h1>
          <p style={{ color: 'var(--c-ink-2)', fontSize: '0.875rem', marginTop: 4 }}>
            Les profils regroupent des permissions. Les profils système (badge <Lock size={12} style={{ verticalAlign: -1 }} />) sont les rôles historiques migrés — ils ne peuvent pas être supprimés.
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
          <Plus size={16} /> Nouveau profil
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
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 14 }}>
          {profils.map(profil => (
            <div key={profil.id} style={{
              border: '1px solid var(--c-border)', borderRadius: 12, padding: 16,
              background: 'var(--c-surface)', opacity: profil.active ? 1 : 0.6,
            }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                    <h3 style={{ margin: 0, fontSize: '1rem', fontWeight: 700 }}>{profil.libelle}</h3>
                    {profil.systeme && <Lock size={13} color="var(--c-ink-2)" title="Profil système" />}
                    {!profil.active && (
                      <span style={{ fontSize: '0.68rem', fontWeight: 700, color: '#b45309', background: '#fef3c7', padding: '2px 6px', borderRadius: 5 }}>
                        DÉSACTIVÉ
                      </span>
                    )}
                  </div>
                  <code style={{ fontSize: '0.72rem', color: 'var(--c-ink-2)' }}>{profil.code}</code>
                </div>
              </div>
              {profil.description && (
                <p style={{ fontSize: '0.82rem', color: 'var(--c-ink-2)', margin: '8px 0' }}>{profil.description}</p>
              )}
              <p style={{ fontSize: '0.78rem', color: 'var(--c-ink-2)', margin: '8px 0 12px' }}>
                {profil.permissions?.length ?? 0} permission(s) associée(s)
              </p>
              <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
                <button onClick={() => openPermModal(profil)} style={actionBtnStyle}>
                  <Settings2 size={14} /> Permissions
                </button>
                {!profil.systeme && (
                  <>
                    <button onClick={() => handleToggleActive(profil)} style={actionBtnStyle}>
                      <Power size={14} /> {profil.active ? 'Désactiver' : 'Réactiver'}
                    </button>
                    <button onClick={() => handleDelete(profil)} style={{ ...actionBtnStyle, color: '#b91c1c' }}>
                      <Trash2 size={14} /> Supprimer
                    </button>
                  </>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {showCreateModal && (
        <div style={{
          position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.4)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 50,
        }}>
          <div style={{ background: '#fff', borderRadius: 12, padding: 24, width: 420, maxWidth: '90vw' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
              <h2 style={{ margin: 0, fontSize: '1.1rem', fontWeight: 700 }}>Nouveau profil</h2>
              <button onClick={() => setShowCreateModal(false)} style={{ border: 'none', background: 'transparent', cursor: 'pointer' }}>
                <X size={18} />
              </button>
            </div>
            <form onSubmit={handleCreate} style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
              <input
                placeholder="Code (ex: SUPERVISEUR_NORD)"
                value={form.code}
                onChange={(e) => setForm({ ...form, code: e.target.value })}
                style={inputStyle}
              />
              <input
                placeholder="Libellé (ex: Superviseur zone Nord)"
                value={form.libelle}
                onChange={(e) => setForm({ ...form, libelle: e.target.value })}
                style={inputStyle}
              />
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

      {permModalProfil && (
        <div style={{
          position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.4)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 50,
        }}>
          <div style={{ background: '#fff', borderRadius: 12, padding: 24, width: 520, maxWidth: '90vw', maxHeight: '80vh', display: 'flex', flexDirection: 'column' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
              <h2 style={{ margin: 0, fontSize: '1.1rem', fontWeight: 700 }}>
                Permissions — {permModalProfil.libelle}
              </h2>
              <button onClick={() => setPermModalProfil(null)} style={{ border: 'none', background: 'transparent', cursor: 'pointer' }}>
                <X size={18} />
              </button>
            </div>
            <div style={{ overflowY: 'auto', flex: 1 }}>
              {Object.entries(grouped).map(([categorie, items]) => (
                <div key={categorie} style={{ marginBottom: 14 }}>
                  <p style={{ fontSize: '0.72rem', fontWeight: 700, textTransform: 'uppercase', color: 'var(--c-ink-2)', marginBottom: 6 }}>
                    {categorie}
                  </p>
                  {items.map(p => (
                    <label key={p.id} style={{ display: 'flex', alignItems: 'center', gap: 8, padding: '4px 0', fontSize: '0.85rem', cursor: 'pointer' }}>
                      <input
                        type="checkbox"
                        checked={selectedPermIds.has(p.id)}
                        onChange={() => togglePerm(p.id)}
                      />
                      {p.libelle} <code style={{ fontSize: '0.7rem', color: 'var(--c-ink-2)' }}>({p.code})</code>
                    </label>
                  ))}
                </div>
              ))}
            </div>
            <button
              onClick={savePermissions}
              style={{
                marginTop: 12, padding: '10px 16px', borderRadius: 8, border: 'none',
                background: '#2563eb', color: '#fff', fontWeight: 600,
                fontSize: '0.875rem', cursor: 'pointer',
              }}
            >
              Enregistrer
            </button>
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

const actionBtnStyle = {
  display: 'flex', alignItems: 'center', gap: 6,
  padding: '6px 10px', borderRadius: 7, border: '1px solid var(--c-border)',
  background: 'transparent', cursor: 'pointer', fontSize: '0.78rem', fontWeight: 600,
  color: 'var(--c-ink)',
};

export default Profils;

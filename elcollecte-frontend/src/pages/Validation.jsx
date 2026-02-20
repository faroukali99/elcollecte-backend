import React, { useState, useEffect } from 'react';
import client from '../api/client';
import { Check, X, RefreshCw, MessageSquare } from 'lucide-react';

const Skeleton = ({ h = 16, w = '100%' }) => (
  <div className="skeleton" style={{ height: h, width: w, borderRadius: 6 }} />
);

const Validation = () => {
  const [collectes,  setCollectes]  = useState([]);
  const [selected,   setSelected]   = useState(null);
  const [loading,    setLoading]    = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [commentaire, setCommentaire] = useState('');
  const [message,    setMessage]    = useState(null); // { type: 'success'|'error', text }

  const fetchEnAttente = async () => {
    setLoading(true);
    try {
      // Récupère les collectes avec statut SOUMIS (en attente de validation)
      const { data } = await client.get('/collectes?statut=SOUMIS&page=0&size=50');
      const list = data.content ?? data ?? [];
      setCollectes(list);
      if (list.length > 0 && !selected) setSelected(list[0]);
    } catch (err) {
      console.error('[Validation]', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchEnAttente(); }, []);

  const handleValider = async () => {
    if (!selected) return;
    setActionLoading(true);
    setMessage(null);
    try {
      await client.put(`/collectes/${selected.id}/valider`);
      setMessage({ type: 'success', text: 'Collecte validée avec succès !' });
      setCommentaire('');
      // Retire de la liste
      const updated = collectes.filter(c => c.id !== selected.id);
      setCollectes(updated);
      setSelected(updated[0] ?? null);
    } catch (err) {
      setMessage({ type: 'error', text: err.response?.data?.message ?? 'Erreur lors de la validation.' });
    } finally {
      setActionLoading(false);
    }
  };

  const handleRejeter = async () => {
    if (!selected) return;
    if (!commentaire.trim()) {
      setMessage({ type: 'error', text: 'Veuillez ajouter un motif de rejet.' });
      return;
    }
    setActionLoading(true);
    setMessage(null);
    try {
      await client.put(`/collectes/${selected.id}/rejeter?motif=${encodeURIComponent(commentaire)}`);
      setMessage({ type: 'success', text: 'Collecte rejetée.' });
      setCommentaire('');
      const updated = collectes.filter(c => c.id !== selected.id);
      setCollectes(updated);
      setSelected(updated[0] ?? null);
    } catch (err) {
      setMessage({ type: 'error', text: err.response?.data?.message ?? 'Erreur lors du rejet.' });
    } finally {
      setActionLoading(false);
    }
  };

  return (
    <div style={{ padding: '28px 0' }}>
      {/* Header */}
      <div className="animate-fade-up" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <div>
          <h1 style={{ margin: 0, fontSize: '1.5rem', fontWeight: 700, color: 'var(--c-ink)', letterSpacing: '-0.02em' }}>
            Centre de Validation
          </h1>
          <p style={{ margin: '4px 0 0', fontSize: '0.855rem', color: 'var(--c-ink-2)' }}>
            {loading ? '…' : `${collectes.length} collecte${collectes.length > 1 ? 's' : ''} en attente`}
          </p>
        </div>
        <button
          onClick={fetchEnAttente}
          style={{ width: 38, height: 38, border: '1.5px solid var(--c-border)', borderRadius: 10, background: '#fff', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--c-ink-2)' }}
          title="Actualiser"
        >
          <RefreshCw size={15} />
        </button>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '300px 1fr', gap: 20, alignItems: 'start' }} className="validation-grid">

        {/* ── Liste ── */}
        <div className="card animate-fade-up delay-100" style={{ overflow: 'hidden' }}>
          <div style={{ padding: '14px 16px', borderBottom: '1px solid var(--c-border)', fontSize: '0.8rem', fontWeight: 700, color: 'var(--c-ink-2)', textTransform: 'uppercase', letterSpacing: '0.06em' }}>
            En attente ({collectes.length})
          </div>
          <div style={{ maxHeight: 500, overflowY: 'auto' }}>
            {loading
              ? Array.from({ length: 4 }).map((_, i) => (
                  <div key={i} style={{ padding: '14px 16px', borderBottom: '1px solid var(--c-border)' }}>
                    <Skeleton h={14} w="70%" />
                    <div style={{ marginTop: 6 }}><Skeleton h={11} w="50%" /></div>
                  </div>
                ))
              : collectes.length === 0
              ? (
                <div style={{ padding: '32px 16px', textAlign: 'center', color: 'var(--c-ink-2)', fontSize: '0.855rem' }}>
                  ✓ Aucune collecte en attente
                </div>
              )
              : collectes.map(c => (
                <button
                  key={c.id}
                  onClick={() => { setSelected(c); setMessage(null); setCommentaire(''); }}
                  style={{
                    width: '100%', textAlign: 'left', border: 'none', cursor: 'pointer',
                    padding: '14px 16px', borderBottom: '1px solid var(--c-border)',
                    background: selected?.id === c.id ? '#f0f4ff' : 'transparent',
                    borderLeft: selected?.id === c.id ? '3px solid var(--c-accent)' : '3px solid transparent',
                    transition: 'background 0.15s',
                    fontFamily: 'var(--font-sans)',
                  }}
                >
                  <p style={{ margin: 0, fontWeight: 600, fontSize: '0.875rem', color: 'var(--c-ink)' }}>
                    Projet #{c.projetId}
                  </p>
                  <p style={{ margin: '3px 0 0', fontSize: '0.75rem', color: 'var(--c-ink-2)' }}>
                    Collecte #{c.id} — {c.collectedAt ? new Date(c.collectedAt).toLocaleDateString('fr-FR') : '—'}
                  </p>
                </button>
              ))
            }
          </div>
        </div>

        {/* ── Détail ── */}
        <div className="card animate-fade-up delay-150" style={{ padding: 24 }}>
          {!selected && !loading ? (
            <div style={{ textAlign: 'center', padding: '60px 20px', color: 'var(--c-ink-2)' }}>
              <MessageSquare size={32} style={{ margin: '0 auto 12px', opacity: 0.3 }} />
              <p style={{ fontWeight: 600 }}>Sélectionnez une collecte à valider</p>
            </div>
          ) : selected ? (
            <>
              {/* Info collecte */}
              <div style={{ marginBottom: 24 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 12 }}>
                  <div>
                    <h2 style={{ margin: 0, fontSize: '1.1rem', fontWeight: 700, color: 'var(--c-ink)' }}>
                      Collecte #{selected.id}
                    </h2>
                    <p style={{ margin: '4px 0 0', fontSize: '0.82rem', color: 'var(--c-ink-2)' }}>
                      Projet #{selected.projetId} · Enquêteur #{selected.enqueteurId}
                    </p>
                  </div>
                  <span style={{ padding: '4px 12px', borderRadius: 99, fontSize: '0.72rem', fontWeight: 700, background: '#fef3c7', color: '#92400e' }}>
                    En attente
                  </span>
                </div>

                {/* Données de la collecte */}
                {selected.donnees && Object.keys(selected.donnees).length > 0 && (
                  <div style={{ background: '#faf9f7', border: '1px solid var(--c-border)', borderRadius: 10, padding: 16, marginTop: 12 }}>
                    <p style={{ margin: '0 0 8px', fontSize: '0.72rem', fontWeight: 700, color: 'var(--c-ink-2)', textTransform: 'uppercase', letterSpacing: '0.06em' }}>
                      Données collectées
                    </p>
                    {Object.entries(selected.donnees).map(([k, v]) => (
                      <div key={k} style={{ display: 'flex', gap: 8, marginBottom: 4 }}>
                        <span style={{ fontSize: '0.82rem', fontWeight: 600, color: 'var(--c-ink-2)', minWidth: 120 }}>{k} :</span>
                        <span style={{ fontSize: '0.82rem', color: 'var(--c-ink)' }}>{String(v)}</span>
                      </div>
                    ))}
                  </div>
                )}

                {/* Coordonnées GPS */}
                {selected.latitude && (
                  <div style={{ marginTop: 10, fontSize: '0.82rem', color: 'var(--c-ink-2)' }}>
                    📍 {selected.latitude}, {selected.longitude}
                  </div>
                )}
              </div>

              <div style={{ borderTop: '1px solid var(--c-border)', paddingTop: 20 }}>
                <p style={{ margin: '0 0 10px', fontSize: '0.8rem', fontWeight: 700, color: 'var(--c-ink)' }}>
                  Commentaire de validation
                </p>
                <textarea
                  className="input-field"
                  style={{ minHeight: 90, resize: 'vertical', marginBottom: 16 }}
                  placeholder="Ajoutez un commentaire ou un motif (obligatoire pour le rejet)…"
                  value={commentaire}
                  onChange={e => setCommentaire(e.target.value)}
                />

                {message && (
                  <div style={{
                    padding: '10px 14px', borderRadius: 8, marginBottom: 16, fontSize: '0.855rem',
                    background: message.type === 'success' ? '#d1fae5' : '#fee2e2',
                    color:      message.type === 'success' ? '#065f46' : '#991b1b',
                  }}>
                    {message.text}
                  </div>
                )}

                <div style={{ display: 'flex', gap: 12 }}>
                  <button
                    onClick={handleValider}
                    disabled={actionLoading}
                    style={{
                      display: 'flex', alignItems: 'center', gap: 6,
                      padding: '10px 20px', border: 'none', borderRadius: 10,
                      background: '#059669', color: '#fff',
                      fontWeight: 700, fontSize: '0.875rem', fontFamily: 'var(--font-sans)',
                      cursor: actionLoading ? 'not-allowed' : 'pointer',
                      opacity: actionLoading ? 0.7 : 1,
                      transition: 'background 0.15s',
                    }}
                  >
                    <Check size={16} /> Approuver
                  </button>
                  <button
                    onClick={handleRejeter}
                    disabled={actionLoading}
                    style={{
                      display: 'flex', alignItems: 'center', gap: 6,
                      padding: '10px 20px', border: '1.5px solid #dc2626', borderRadius: 10,
                      background: 'transparent', color: '#dc2626',
                      fontWeight: 700, fontSize: '0.875rem', fontFamily: 'var(--font-sans)',
                      cursor: actionLoading ? 'not-allowed' : 'pointer',
                      opacity: actionLoading ? 0.7 : 1,
                      transition: 'background 0.15s',
                    }}
                  >
                    <X size={16} /> Rejeter
                  </button>
                </div>
              </div>
            </>
          ) : null}
        </div>
      </div>

      <style>{`
        @media (max-width: 900px) {
          .validation-grid { grid-template-columns: 1fr !important; }
        }
      `}</style>
    </div>
  );
};

export default Validation;

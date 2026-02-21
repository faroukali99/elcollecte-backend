import React, { useState, useEffect } from 'react';
import collectesApi from '../api/collectesApi';
import { UploadCloud, MoreVertical, RefreshCw, Send } from 'lucide-react';

const STATUS_CHIP = {
  VALIDE:    { label: 'Validé',     bg: '#d1fae5', color: '#065f46' },
  SOUMIS:    { label: 'En attente', bg: '#fef3c7', color: '#92400e' },
  REJETE:    { label: 'Rejeté',    bg: '#fee2e2', color: '#991b1b' },
  BROUILLON: { label: 'Brouillon', bg: '#f3f4f6', color: '#374151' },
};

const Skeleton = () => (
    <tr>
      {[1,2,3,4].map(i => (
          <td key={i} style={{ padding: '14px 16px' }}>
            <div className="skeleton" style={{ height: 14, borderRadius: 6 }} />
          </td>
      ))}
    </tr>
);

const Collecte = () => {
  const [collectes,  setCollectes]  = useState([]);
  const [loading,    setLoading]    = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error,      setError]      = useState(null);
  const [success,    setSuccess]    = useState('');
  const [form, setForm] = useState({ projetId: '', description: '' });

  const fetchCollectes = async () => {
    setLoading(true);
    setError(null);
    try {
      const { data } = await collectesApi.list({ page: 0, size: 20 });
      setCollectes(data.content ?? data ?? []);
    } catch (err) {
      setError('Impossible de charger les collectes.');
      console.error('[Collecte]', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchCollectes(); }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.projetId) { setError('Veuillez renseigner un ID de projet.'); return; }
    setSubmitting(true);
    setError(null);
    setSuccess('');
    try {
      await collectesApi.submit({
        projetId:     parseInt(form.projetId),
        formulaireId: 1,
        donnees:      { description: form.description },
      });
      setSuccess('Collecte soumise avec succès !');
      setForm({ projetId: '', description: '' });
      fetchCollectes();
    } catch (err) {
      setError(err.response?.data?.message ?? 'Erreur lors de la soumission.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
      <div style={{ padding: '28px 0' }}>
        <div className="animate-fade-up" style={{ marginBottom: 24 }}>
          <h1 style={{ margin: 0, fontSize: '1.5rem', fontWeight: 700, color: 'var(--c-ink)', letterSpacing: '-0.02em' }}>
            Collectes
          </h1>
          <p style={{ margin: '4px 0 0', fontSize: '0.855rem', color: 'var(--c-ink-2)' }}>
            Soumettez et suivez vos collectes terrain
          </p>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '360px 1fr', gap: 20, alignItems: 'start' }} className="collecte-grid">
          {/* ── Formulaire ── */}
          <div className="card animate-fade-up delay-100" style={{ padding: 24 }}>
            <h2 style={{ margin: '0 0 20px', fontSize: '1rem', fontWeight: 700, color: 'var(--c-ink)' }}>
              Nouvelle collecte
            </h2>

            {error && (
                <div style={{ padding: '10px 14px', borderRadius: 8, marginBottom: 16, background: '#fee2e2', color: '#991b1b', fontSize: '0.82rem' }}>
                  {error}
                </div>
            )}
            {success && (
                <div style={{ padding: '10px 14px', borderRadius: 8, marginBottom: 16, background: '#d1fae5', color: '#065f46', fontSize: '0.82rem' }}>
                  ✓ {success}
                </div>
            )}

            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
              <div>
                <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, color: 'var(--c-ink)', marginBottom: 6 }}>
                  ID du projet *
                </label>
                <input
                    type="number"
                    className="input-field"
                    placeholder="ex: 1"
                    value={form.projetId}
                    onChange={e => setForm(f => ({ ...f, projetId: e.target.value }))}
                    required
                />
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, color: 'var(--c-ink)', marginBottom: 6 }}>
                  Description / Observations
                </label>
                <textarea
                    className="input-field"
                    style={{ minHeight: 100, resize: 'vertical' }}
                    placeholder="Ajoutez des notes, observations…"
                    value={form.description}
                    onChange={e => setForm(f => ({ ...f, description: e.target.value }))}
                />
              </div>

              <label style={{ border: '2px dashed var(--c-border)', borderRadius: 10, padding: '20px', textAlign: 'center', color: 'var(--c-ink-3)', fontSize: '0.82rem', cursor: 'pointer', display: 'block' }}>
                <UploadCloud size={20} style={{ margin: '0 auto 8px', display: 'block' }} />
                Glissez des fichiers ici ou cliquez pour sélectionner
                <input type="file" style={{ display: 'none' }} multiple />
              </label>

              <button
                  type="submit"
                  disabled={submitting}
                  className="btn-primary"
                  style={{ justifyContent: 'center', width: '100%', padding: '11px', opacity: submitting ? 0.7 : 1 }}
              >
                <Send size={15} />
                {submitting ? 'Envoi en cours…' : 'Soumettre la collecte'}
              </button>
            </form>
          </div>

          {/* ── Liste ── */}
          <div className="card animate-fade-up delay-150" style={{ overflow: 'hidden' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '18px 20px', borderBottom: '1px solid var(--c-border)' }}>
              <h2 style={{ margin: 0, fontSize: '1rem', fontWeight: 700, color: 'var(--c-ink)' }}>
                Collectes récentes
              </h2>
              <button
                  onClick={fetchCollectes}
                  style={{ width: 32, height: 32, border: '1px solid var(--c-border)', borderRadius: 8, background: 'transparent', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--c-ink-2)' }}
              >
                <RefreshCw size={13} />
              </button>
            </div>

            <div style={{ overflowX: 'auto' }}>
              <table className="data-table">
                <thead>
                <tr>
                  <th>ID</th>
                  <th>Projet</th>
                  <th>Date</th>
                  <th>Statut</th>
                  <th style={{ textAlign: 'right' }}></th>
                </tr>
                </thead>
                <tbody>
                {loading
                    ? Array.from({ length: 5 }).map((_, i) => <Skeleton key={i} />)
                    : collectes.length === 0
                        ? (
                            <tr>
                              <td colSpan={5} style={{ textAlign: 'center', padding: '40px 20px', color: 'var(--c-ink-2)' }}>
                                Aucune collecte pour le moment.
                              </td>
                            </tr>
                        )
                        : collectes.map(c => {
                          const chip = STATUS_CHIP[c.statut] ?? { label: c.statut, bg: '#f3f4f6', color: '#374151' };
                          return (
                              <tr key={c.id}>
                                <td>
                          <span style={{ fontFamily: 'var(--font-mono)', fontWeight: 600, fontSize: '0.82rem', color: 'var(--c-accent)' }}>
                            #{c.id}
                          </span>
                                </td>
                                <td style={{ color: 'var(--c-ink-2)' }}>Projet #{c.projetId}</td>
                                <td style={{ color: 'var(--c-ink-2)', fontSize: '0.82rem' }}>
                                  {c.collectedAt ? new Date(c.collectedAt).toLocaleDateString('fr-FR') : '—'}
                                </td>
                                <td>
                          <span style={{ padding: '3px 10px', borderRadius: 99, fontSize: '0.72rem', fontWeight: 700, background: chip.bg, color: chip.color }}>
                            {chip.label}
                          </span>
                                </td>
                                <td style={{ textAlign: 'right' }}>
                                  <button style={{ width: 28, height: 28, border: '1px solid var(--c-border)', borderRadius: 6, background: 'transparent', cursor: 'pointer', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', color: 'var(--c-ink-2)' }}>
                                    <MoreVertical size={13} />
                                  </button>
                                </td>
                              </tr>
                          );
                        })
                }
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <style>{`
        @media (max-width: 900px) {
          .collecte-grid { grid-template-columns: 1fr !important; }
        }
      `}</style>
      </div>
  );
};

export default Collecte;

import React, { useState, useEffect, useCallback } from 'react';
import client from '../api/client';
import {
  UploadCloud, RefreshCw, Send, ChevronDown, MapPin,
  FileText, AlertCircle, CheckCircle2, Clock, XCircle,
  Plus, Trash2, Eye, Download, BarChart3
} from 'lucide-react';

/* ── Status config ─────────────────────────────────────────── */
const STATUS_CONFIG = {
  VALIDE:    { label: 'Validé',     bg: '#d1fae5', color: '#065f46', icon: CheckCircle2 },
  SOUMIS:    { label: 'En attente', bg: '#fef3c7', color: '#92400e', icon: Clock },
  REJETE:    { label: 'Rejeté',     bg: '#fee2e2', color: '#991b1b', icon: XCircle },
  BROUILLON: { label: 'Brouillon',  bg: '#f3f4f6', color: '#374151', icon: FileText },
};

/* ── Skeleton row ─────────────────────────────────────────── */
const SkeletonRow = () => (
    <tr>
      {[1,2,3,4,5].map(i => (
          <td key={i} style={{ padding: '14px 16px' }}>
            <div className="skeleton" style={{ height: 13, borderRadius: 4 }} />
          </td>
      ))}
    </tr>
);

/* ── Modal Détail collecte ─────────────────────────────────── */
const DetailModal = ({ collecte, onClose }) => {
  if (!collecte) return null;
  const cfg = STATUS_CONFIG[collecte.statut] ?? { label: collecte.statut, bg: '#f3f4f6', color: '#374151' };
  const Icon = cfg.icon ?? FileText;

  return (
      <div style={{
        position: 'fixed', inset: 0, zIndex: 100,
        background: 'rgba(0,0,0,0.45)', backdropFilter: 'blur(4px)',
        display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 20,
      }} onClick={onClose}>
        <div
            style={{
              background: '#fff', borderRadius: 20, width: '100%', maxWidth: 560,
              boxShadow: '0 24px 80px rgba(0,0,0,0.18)',
              overflow: 'hidden', maxHeight: '80vh', display: 'flex', flexDirection: 'column',
            }}
            onClick={e => e.stopPropagation()}
        >
          <div style={{ padding: '20px 24px', borderBottom: '1px solid var(--c-border)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <h2 style={{ margin: 0, fontSize: '1.05rem', fontWeight: 700, color: 'var(--c-ink)' }}>
                Collecte #{collecte.id}
              </h2>
              <p style={{ margin: '3px 0 0', fontSize: '0.78rem', color: 'var(--c-ink-2)' }}>
                Projet #{collecte.projetId} · Formulaire #{collecte.formulaireId}
              </p>
            </div>
            <span style={{ padding: '4px 12px', borderRadius: 99, fontSize: '0.72rem', fontWeight: 700, background: cfg.bg, color: cfg.color, display: 'flex', alignItems: 'center', gap: 5 }}>
            <Icon size={11} /> {cfg.label}
          </span>
          </div>

          <div style={{ padding: 24, overflowY: 'auto', flex: 1 }}>
            {collecte.donnees && Object.keys(collecte.donnees).length > 0 && (
                <div style={{ marginBottom: 20 }}>
                  <p style={{ margin: '0 0 10px', fontSize: '0.72rem', fontWeight: 700, color: 'var(--c-ink-2)', textTransform: 'uppercase', letterSpacing: '0.06em' }}>
                    Données collectées
                  </p>
                  <div style={{ background: '#faf9f7', borderRadius: 10, padding: 14, border: '1px solid var(--c-border)' }}>
                    {Object.entries(collecte.donnees).map(([k, v]) => (
                        <div key={k} style={{ display: 'flex', gap: 12, padding: '6px 0', borderBottom: '1px solid #f0eeeb' }}>
                          <span style={{ fontSize: '0.82rem', fontWeight: 600, color: 'var(--c-ink-2)', minWidth: 140 }}>{k}</span>
                          <span style={{ fontSize: '0.82rem', color: 'var(--c-ink)' }}>{String(v)}</span>
                        </div>
                    ))}
                  </div>
                </div>
            )}
            {collecte.latitude && (
                <div style={{ display: 'flex', alignItems: 'center', gap: 8, padding: '10px 14px', background: '#eff6ff', borderRadius: 10, marginBottom: 16 }}>
                  <MapPin size={14} color="var(--c-accent)" />
                  <span style={{ fontSize: '0.82rem', color: 'var(--c-accent)', fontWeight: 600 }}>
                {collecte.latitude}, {collecte.longitude}
              </span>
                </div>
            )}
            {collecte.statut === 'REJETE' && collecte.motifRejet && (
                <div style={{ padding: '10px 14px', background: '#fee2e2', borderRadius: 10, border: '1px solid #fca5a5' }}>
                  <p style={{ margin: 0, fontSize: '0.8rem', fontWeight: 700, color: '#991b1b', marginBottom: 4 }}>Motif de rejet</p>
                  <p style={{ margin: 0, fontSize: '0.82rem', color: '#7f1d1d' }}>{collecte.motifRejet}</p>
                </div>
            )}
            <div style={{ marginTop: 16, fontSize: '0.78rem', color: 'var(--c-ink-3)', display: 'flex', gap: 20 }}>
              {collecte.collectedAt && (
                  <span>Collecté le {new Date(collecte.collectedAt).toLocaleString('fr-FR')}</span>
              )}
              {collecte.offline && <span>📴 Hors-ligne</span>}
            </div>
          </div>

          <div style={{ padding: '16px 24px', borderTop: '1px solid var(--c-border)', display: 'flex', justifyContent: 'flex-end' }}>
            <button onClick={onClose} style={{
              padding: '9px 20px', border: '1.5px solid var(--c-border)', borderRadius: 9,
              background: 'transparent', cursor: 'pointer', fontSize: '0.855rem', fontWeight: 600,
              color: 'var(--c-ink)', fontFamily: 'var(--font-sans)',
            }}>
              Fermer
            </button>
          </div>
        </div>
      </div>
  );
};

/* ── TYPES DE COLLECTE prédéfinis ──────────────────────────── */
const TYPES_COLLECTE = [
  { id: 'terrain', label: 'Collecte Terrain', icon: '🌿', champs: ['localite', 'zone', 'conditions_meteo', 'observations', 'responsable'] },
  { id: 'interview', label: 'Interview / Enquête', icon: '🎤', champs: ['nom_enquete', 'age', 'profession', 'reponses', 'remarques', 'enqueteur'] },
  { id: 'echantillon', label: 'Prélèvement Échantillon', icon: '🧪', champs: ['type_echantillon', 'quantite', 'conditions_stockage', 'date_prelevement', 'laboratoire'] },
  { id: 'inspection', label: 'Inspection Visuelle', icon: '🔍', champs: ['site_inspecte', 'etat_general', 'anomalies_detectees', 'actions_requises', 'inspecteur'] },
  { id: 'mesure', label: 'Mesures / Relevés', icon: '📏', champs: ['parametre_mesure', 'valeur', 'unite', 'instrument', 'conditions', 'operateur'] },
  { id: 'libre', label: 'Formulaire Libre', icon: '✏️', champs: [] },
];

/* ── Page Collecte ─────────────────────────────────────────── */
const Collecte = () => {
  const [projets,    setProjets]    = useState([]);
  const [collectes,  setCollectes]  = useState([]);
  const [loading,    setLoading]    = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error,      setError]      = useState(null);
  const [success,    setSuccess]    = useState('');
  const [detail,     setDetail]     = useState(null);
  const [filterStatut, setFilterStatut] = useState('');
  const [typeCollecte, setTypeCollecte] = useState(null);
  const [form, setForm] = useState({ projetId: '', formulaireId: '1', latitude: '', longitude: '' });
  const [champsDyn, setChampsDyn] = useState({});
  const [champsLibres, setChampsLibres] = useState([{ nom: '', valeur: '' }]);

  const fetchProjets = useCallback(async () => {
    try {
      const { data } = await client.get('/projets?page=0&size=50');
      setProjets(data.content ?? data ?? []);
    } catch { setProjets([]); }
  }, []);

  const fetchCollectes = useCallback(async () => {
    setLoading(true); setError(null);
    try {
      const params = new URLSearchParams({ page: 0, size: 50 });
      if (filterStatut) params.set('statut', filterStatut);
      const { data } = await client.get(`/collectes?${params}`);
      setCollectes(data.content ?? data ?? []);
    } catch { setError('Impossible de charger les collectes.'); }
    finally { setLoading(false); }
  }, [filterStatut]);

  useEffect(() => { fetchProjets(); }, [fetchProjets]);
  useEffect(() => { fetchCollectes(); }, [fetchCollectes]);

  const selectType = (type) => {
    setTypeCollecte(type);
    if (type.id !== 'libre') {
      const init = {};
      type.champs.forEach(c => { init[c] = ''; });
      setChampsDyn(init);
    } else {
      setChampsLibres([{ nom: '', valeur: '' }]);
    }
  };

  const getGPS = () => {
    if (!navigator.geolocation) return;
    navigator.geolocation.getCurrentPosition(pos => {
      setForm(f => ({ ...f, latitude: pos.coords.latitude.toFixed(6), longitude: pos.coords.longitude.toFixed(6) }));
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.projetId) { setError('Veuillez sélectionner un projet.'); return; }
    if (!typeCollecte)  { setError('Veuillez choisir un type de collecte.'); return; }

    let donnees = {};
    if (typeCollecte.id !== 'libre') {
      donnees = { ...champsDyn, type_collecte: typeCollecte.label };
    } else {
      champsLibres.filter(c => c.nom.trim()).forEach(c => { donnees[c.nom.trim()] = c.valeur; });
      donnees.type_collecte = 'Formulaire Libre';
    }

    setSubmitting(true); setError(null); setSuccess('');
    try {
      await client.post('/collectes', {
        projetId: parseInt(form.projetId),
        formulaireId: parseInt(form.formulaireId) || 1,
        donnees,
        latitude:  form.latitude  ? parseFloat(form.latitude)  : null,
        longitude: form.longitude ? parseFloat(form.longitude) : null,
      });
      setSuccess('✅ Collecte soumise avec succès !');
      setTypeCollecte(null);
      setForm({ projetId: form.projetId, formulaireId: '1', latitude: '', longitude: '' });
      setChampsDyn({});
      setChampsLibres([{ nom: '', valeur: '' }]);
      fetchCollectes();
      setTimeout(() => setSuccess(''), 4000);
    } catch (err) {
      setError(err.response?.data?.message ?? 'Erreur lors de la soumission.');
    } finally { setSubmitting(false); }
  };

  const stats = {
    total: collectes.length,
    valides: collectes.filter(c => c.statut === 'VALIDE').length,
    enAttente: collectes.filter(c => c.statut === 'SOUMIS').length,
    rejetes: collectes.filter(c => c.statut === 'REJETE').length,
  };

  return (
      <div style={{ padding: '28px 0' }}>
        <div className="animate-fade-up" style={{ marginBottom: 24 }}>
          <h1 style={{ margin: 0, fontSize: '1.5rem', fontWeight: 700, color: 'var(--c-ink)', letterSpacing: '-0.02em' }}>Collectes</h1>
          <p style={{ margin: '4px 0 0', fontSize: '0.855rem', color: 'var(--c-ink-2)' }}>Saisissez vos données terrain et suivez leur validation</p>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 12, marginBottom: 24 }} className="collecte-kpis">
          {[
            { label: 'Total', value: stats.total, color: '#2563eb', bg: '#dbeafe' },
            { label: 'Validées', value: stats.valides, color: '#059669', bg: '#d1fae5' },
            { label: 'En attente', value: stats.enAttente, color: '#d97706', bg: '#fef3c7' },
            { label: 'Rejetées', value: stats.rejetes, color: '#dc2626', bg: '#fee2e2' },
          ].map(k => (
              <div key={k.label} className="card animate-fade-up" style={{ padding: '14px 18px', textAlign: 'center' }}>
                <div style={{ fontSize: '1.6rem', fontWeight: 800, color: k.color }}>{loading ? '—' : k.value}</div>
                <div style={{ fontSize: '0.72rem', color: 'var(--c-ink-2)', fontWeight: 600, marginTop: 2 }}>{k.label}</div>
                <div style={{ height: 3, borderRadius: 99, background: k.color, opacity: 0.2, marginTop: 8 }} />
              </div>
          ))}
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '420px 1fr', gap: 20, alignItems: 'start' }} className="collecte-grid">

          {/* ── FORMULAIRE ── */}
          <div className="card animate-fade-up delay-100" style={{ overflow: 'hidden' }}>
            <div style={{ padding: '18px 20px', borderBottom: '1px solid var(--c-border)', background: 'linear-gradient(135deg, #f0f4ff, #faf9f7)' }}>
              <h2 style={{ margin: 0, fontSize: '1rem', fontWeight: 700, color: 'var(--c-ink)' }}>Nouvelle collecte</h2>
              <p style={{ margin: '3px 0 0', fontSize: '0.78rem', color: 'var(--c-ink-2)' }}>Remplissez le formulaire adapté à votre mission</p>
            </div>

            <form onSubmit={handleSubmit} style={{ padding: 20 }}>
              {error && (
                  <div style={{ padding: '10px 14px', borderRadius: 8, marginBottom: 14, background: '#fee2e2', color: '#991b1b', fontSize: '0.82rem', display: 'flex', gap: 8, alignItems: 'flex-start' }}>
                    <AlertCircle size={14} style={{ flexShrink: 0, marginTop: 1 }} /> {error}
                  </div>
              )}
              {success && (
                  <div style={{ padding: '10px 14px', borderRadius: 8, marginBottom: 14, background: '#d1fae5', color: '#065f46', fontSize: '0.82rem' }}>{success}</div>
              )}

              {/* Projet */}
              <div style={{ marginBottom: 16 }}>
                <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, color: 'var(--c-ink)', marginBottom: 6 }}>
                  Projet <span style={{ color: '#dc2626' }}>*</span>
                </label>
                <select className="input-field" value={form.projetId} onChange={e => setForm(f => ({ ...f, projetId: e.target.value }))} required>
                  <option value="">— Sélectionnez un projet —</option>
                  {projets.map(p => <option key={p.id} value={p.id}>#{p.id} — {p.titre}</option>)}
                </select>
              </div>

              {/* Type de collecte */}
              {!typeCollecte ? (
                  <div style={{ marginBottom: 16 }}>
                    <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, color: 'var(--c-ink)', marginBottom: 10 }}>
                      Type de collecte <span style={{ color: '#dc2626' }}>*</span>
                    </label>
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8 }}>
                      {TYPES_COLLECTE.map(type => (
                          <button key={type.id} type="button" onClick={() => selectType(type)}
                                  style={{ padding: '12px 10px', border: '1.5px solid var(--c-border)', borderRadius: 10, background: '#fff', cursor: 'pointer', textAlign: 'left', transition: 'all 0.15s', fontFamily: 'var(--font-sans)' }}
                                  onMouseEnter={e => { e.currentTarget.style.borderColor = 'var(--c-accent)'; e.currentTarget.style.background = '#f0f4ff'; }}
                                  onMouseLeave={e => { e.currentTarget.style.borderColor = 'var(--c-border)'; e.currentTarget.style.background = '#fff'; }}
                          >
                            <span style={{ display: 'block', fontSize: '1.2rem', marginBottom: 4 }}>{type.icon}</span>
                            <span style={{ fontSize: '0.78rem', fontWeight: 700, color: 'var(--c-ink)' }}>{type.label}</span>
                          </button>
                      ))}
                    </div>
                  </div>
              ) : (
                  <>
                    <div style={{ marginBottom: 16, display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '10px 14px', background: '#f0f4ff', borderRadius: 10, border: '1.5px solid #bfdbfe' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                        <span style={{ fontSize: '1.1rem' }}>{typeCollecte.icon}</span>
                        <span style={{ fontSize: '0.855rem', fontWeight: 700, color: 'var(--c-accent)' }}>{typeCollecte.label}</span>
                      </div>
                      <button type="button" onClick={() => { setTypeCollecte(null); setChampsDyn({}); setChampsLibres([{ nom: '', valeur: '' }]); }}
                              style={{ fontSize: '0.72rem', color: 'var(--c-ink-2)', border: 'none', background: 'transparent', cursor: 'pointer', fontFamily: 'var(--font-sans)' }}>
                        Changer
                      </button>
                    </div>

                    {typeCollecte.id !== 'libre' ? (
                        <div style={{ marginBottom: 16, display: 'flex', flexDirection: 'column', gap: 10 }}>
                          <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--c-ink)' }}>Données de la collecte</label>
                          {typeCollecte.champs.map(champ => (
                              <div key={champ}>
                                <label style={{ display: 'block', fontSize: '0.72rem', fontWeight: 600, color: 'var(--c-ink-2)', marginBottom: 4, textTransform: 'capitalize' }}>
                                  {champ.replace(/_/g, ' ')}
                                </label>
                                <input className="input-field" style={{ fontSize: '0.855rem' }}
                                       placeholder={`Saisir ${champ.replace(/_/g, ' ')}...`}
                                       value={champsDyn[champ] ?? ''}
                                       onChange={e => setChampsDyn(prev => ({ ...prev, [champ]: e.target.value }))}
                                />
                              </div>
                          ))}
                        </div>
                    ) : (
                        <div style={{ marginBottom: 16 }}>
                          <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, color: 'var(--c-ink)', marginBottom: 10 }}>Champs libres</label>
                          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                            {champsLibres.map((c, i) => (
                                <div key={i} style={{ display: 'flex', gap: 8 }}>
                                  <input className="input-field" style={{ width: 130, flexShrink: 0, fontSize: '0.82rem' }} placeholder="Nom du champ"
                                         value={c.nom} onChange={e => { const next = [...champsLibres]; next[i] = { ...next[i], nom: e.target.value }; setChampsLibres(next); }} />
                                  <input className="input-field" style={{ flex: 1, fontSize: '0.82rem' }} placeholder="Valeur"
                                         value={c.valeur} onChange={e => { const next = [...champsLibres]; next[i] = { ...next[i], valeur: e.target.value }; setChampsLibres(next); }} />
                                  {champsLibres.length > 1 && (
                                      <button type="button" onClick={() => setChampsLibres(prev => prev.filter((_, idx) => idx !== i))}
                                              style={{ width: 30, height: 30, border: '1px solid #fee2e2', borderRadius: 6, background: 'transparent', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#ef4444', flexShrink: 0 }}>
                                        <Trash2 size={11} />
                                      </button>
                                  )}
                                </div>
                            ))}
                            <button type="button" onClick={() => setChampsLibres(prev => [...prev, { nom: '', valeur: '' }])}
                                    style={{ display: 'flex', alignItems: 'center', gap: 6, padding: '7px 12px', border: '1.5px dashed var(--c-border)', borderRadius: 8, background: 'transparent', cursor: 'pointer', fontSize: '0.78rem', fontWeight: 600, color: 'var(--c-ink-2)', fontFamily: 'var(--font-sans)' }}>
                              <Plus size={12} /> Ajouter un champ
                            </button>
                          </div>
                        </div>
                    )}

                    {/* GPS */}
                    <div style={{ marginBottom: 16 }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
                        <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--c-ink)' }}>Coordonnées GPS (optionnel)</label>
                        <button type="button" onClick={getGPS}
                                style={{ fontSize: '0.72rem', color: 'var(--c-accent)', border: 'none', background: 'transparent', cursor: 'pointer', fontFamily: 'var(--font-sans)', display: 'flex', alignItems: 'center', gap: 4, fontWeight: 600 }}>
                          <MapPin size={11} /> Localiser
                        </button>
                      </div>
                      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8 }}>
                        <input className="input-field" placeholder="Latitude" value={form.latitude}
                               onChange={e => setForm(f => ({ ...f, latitude: e.target.value }))} style={{ fontSize: '0.82rem' }} />
                        <input className="input-field" placeholder="Longitude" value={form.longitude}
                               onChange={e => setForm(f => ({ ...f, longitude: e.target.value }))} style={{ fontSize: '0.82rem' }} />
                      </div>
                      {form.latitude && <p style={{ margin: '5px 0 0', fontSize: '0.72rem', color: '#059669' }}>📍 Position capturée : {form.latitude}, {form.longitude}</p>}
                    </div>

                    {/* Upload */}
                    <label style={{ display: 'block', border: '2px dashed var(--c-border)', borderRadius: 10, padding: '16px', textAlign: 'center', color: 'var(--c-ink-3)', fontSize: '0.78rem', cursor: 'pointer', marginBottom: 16, transition: 'border-color 0.2s' }}
                           onMouseEnter={e => e.currentTarget.style.borderColor = 'var(--c-accent)'}
                           onMouseLeave={e => e.currentTarget.style.borderColor = 'var(--c-border)'}>
                      <UploadCloud size={18} style={{ margin: '0 auto 6px', display: 'block', opacity: 0.5 }} />
                      Photos / Documents (optionnel)
                      <input type="file" style={{ display: 'none' }} multiple accept="image/*,.pdf,.doc,.docx" />
                    </label>

                    <button type="submit" disabled={submitting} className="btn-primary"
                            style={{ width: '100%', justifyContent: 'center', padding: '11px', opacity: submitting ? 0.7 : 1 }}>
                      <Send size={14} />
                      {submitting ? 'Envoi en cours…' : 'Soumettre la collecte'}
                    </button>
                  </>
              )}
            </form>
          </div>

          {/* ── LISTE DES COLLECTES ── */}
          <div className="card animate-fade-up delay-150" style={{ overflow: 'hidden' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '18px 20px', borderBottom: '1px solid var(--c-border)' }}>
              <h2 style={{ margin: 0, fontSize: '1rem', fontWeight: 700, color: 'var(--c-ink)' }}>Collectes récentes</h2>
              <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
                <select value={filterStatut} onChange={e => setFilterStatut(e.target.value)}
                        style={{ padding: '6px 10px', border: '1px solid var(--c-border)', borderRadius: 7, fontSize: '0.78rem', color: 'var(--c-ink-2)', fontFamily: 'var(--font-sans)', background: '#fff', cursor: 'pointer' }}>
                  <option value="">Tous les statuts</option>
                  <option value="SOUMIS">En attente</option>
                  <option value="VALIDE">Validé</option>
                  <option value="REJETE">Rejeté</option>
                </select>
                <button onClick={fetchCollectes}
                        style={{ width: 32, height: 32, border: '1px solid var(--c-border)', borderRadius: 8, background: '#fff', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--c-ink-2)' }}>
                  <RefreshCw size={13} />
                </button>
              </div>
            </div>

            <div style={{ overflowX: 'auto' }}>
              <table className="data-table">
                <thead>
                <tr>
                  <th>ID</th><th>Projet</th><th>Type</th><th>Date</th><th>Statut</th>
                  <th style={{ textAlign: 'right' }}>Action</th>
                </tr>
                </thead>
                <tbody>
                {loading
                    ? Array.from({ length: 6 }).map((_, i) => <SkeletonRow key={i} />)
                    : collectes.length === 0
                        ? (
                            <tr>
                              <td colSpan={6} style={{ textAlign: 'center', padding: '50px 20px', color: 'var(--c-ink-2)' }}>
                                <FileText size={28} style={{ margin: '0 auto 10px', display: 'block', opacity: 0.3 }} />
                                <p style={{ margin: 0, fontWeight: 600 }}>Aucune collecte</p>
                                <p style={{ margin: '4px 0 0', fontSize: '0.78rem' }}>Soumettez votre première collecte ci-contre</p>
                              </td>
                            </tr>
                        )
                        : collectes.map(c => {
                          const cfg = STATUS_CONFIG[c.statut] ?? { label: c.statut, bg: '#f3f4f6', color: '#374151', icon: FileText };
                          const Icon = cfg.icon;
                          return (
                              <tr key={c.id}>
                                <td><span style={{ fontFamily: 'var(--font-mono)', fontWeight: 700, fontSize: '0.82rem', color: 'var(--c-accent)' }}>#{c.id}</span></td>
                                <td style={{ color: 'var(--c-ink-2)', fontSize: '0.855rem' }}>Projet #{c.projetId}</td>
                                <td style={{ fontSize: '0.78rem', color: 'var(--c-ink-2)' }}>{c.donnees?.type_collecte ?? '—'}</td>
                                <td style={{ color: 'var(--c-ink-2)', fontSize: '0.78rem' }}>
                                  {c.collectedAt ? new Date(c.collectedAt).toLocaleDateString('fr-FR') : '—'}
                                </td>
                                <td>
                          <span style={{ display: 'inline-flex', alignItems: 'center', gap: 4, padding: '3px 9px', borderRadius: 99, fontSize: '0.7rem', fontWeight: 700, background: cfg.bg, color: cfg.color }}>
                            <Icon size={10} /> {cfg.label}
                          </span>
                                </td>
                                <td style={{ textAlign: 'right' }}>
                                  <button onClick={() => setDetail(c)}
                                          style={{ width: 28, height: 28, border: '1px solid var(--c-border)', borderRadius: 6, background: 'transparent', cursor: 'pointer', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', color: 'var(--c-ink-2)' }}
                                          title="Voir le détail">
                                    <Eye size={12} />
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

        {detail && <DetailModal collecte={detail} onClose={() => setDetail(null)} />}

        <style>{`
        @media (max-width: 1100px) { .collecte-grid { grid-template-columns: 1fr !important; } }
        @media (max-width: 640px)  { .collecte-kpis { grid-template-columns: repeat(2, 1fr) !important; } }
      `}</style>
      </div>
  );
};

export default Collecte;

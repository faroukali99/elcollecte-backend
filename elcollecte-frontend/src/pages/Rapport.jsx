import React, { useState, useEffect, useCallback } from 'react';
import client from '../api/client';
import {
    BarChart3, Download, RefreshCw, FileText, Filter,
    CheckCircle2, Clock, XCircle, TrendingUp, Calendar, Printer
} from 'lucide-react';

/* ── Helpers ───────────────────────────────────────────────── */
const fmt = (n) => n?.toLocaleString('fr-FR') ?? 0;
const pct = (a, b) => b === 0 ? '0%' : `${Math.round((a / b) * 100)}%`;

const STATUS_CONFIG = {
    VALIDE:    { label: 'Validé',     color: '#059669', bg: '#d1fae5' },
    SOUMIS:    { label: 'En attente', color: '#d97706', bg: '#fef3c7' },
    REJETE:    { label: 'Rejeté',     color: '#dc2626', bg: '#fee2e2' },
    BROUILLON: { label: 'Brouillon',  color: '#6b7280', bg: '#f3f4f6' },
};

/* ── Mini bar ──────────────────────────────────────────────── */
const MiniBar = ({ value, max, color }) => (
    <div style={{ flex: 1, height: 6, background: '#f0eeeb', borderRadius: 99, overflow: 'hidden' }}>
        <div style={{ height: '100%', width: `${max ? (value / max) * 100 : 0}%`, background: color, borderRadius: 99, transition: 'width 0.6s ease' }} />
    </div>
);

/* ── Page Rapport ──────────────────────────────────────────── */
const Rapport = () => {
    const [projets,   setProjets]   = useState([]);
    const [collectes, setCollectes] = useState([]);
    const [loading,   setLoading]   = useState(true);
    const [error,     setError]     = useState(null);
    const [projetFilter, setProjetFilter] = useState('');
    const [dateFrom, setDateFrom] = useState('');
    const [dateTo,   setDateTo]   = useState('');
    const [typeFilter, setTypeFilter] = useState('');
    const [printing, setPrinting] = useState(false);

    const fetchAll = useCallback(async () => {
        setLoading(true); setError(null);
        try {
            const [resP, resC] = await Promise.all([
                client.get('/projets?page=0&size=100'),
                client.get('/collectes?page=0&size=500'),
            ]);
            setProjets(resP.data.content ?? resP.data ?? []);
            setCollectes(resC.data.content ?? resC.data ?? []);
        } catch (e) {
            setError('Impossible de charger les données.');
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => { fetchAll(); }, [fetchAll]);

    /* Filtrage */
    const filtered = collectes.filter(c => {
        if (projetFilter && String(c.projetId) !== projetFilter) return false;
        if (typeFilter && c.donnees?.type_collecte !== typeFilter) return false;
        if (dateFrom && c.collectedAt && new Date(c.collectedAt) < new Date(dateFrom)) return false;
        if (dateTo   && c.collectedAt && new Date(c.collectedAt) > new Date(dateTo + 'T23:59:59')) return false;
        return true;
    });

    /* Statistiques calculées */
    const total = filtered.length;
    const valides   = filtered.filter(c => c.statut === 'VALIDE').length;
    const enAttente = filtered.filter(c => c.statut === 'SOUMIS').length;
    const rejetes   = filtered.filter(c => c.statut === 'REJETE').length;

    /* Par projet */
    const parProjet = projets.map(p => {
        const cs = filtered.filter(c => c.projetId === p.id);
        return { ...p, count: cs.length, valides: cs.filter(c => c.statut === 'VALIDE').length };
    }).filter(p => p.count > 0).sort((a, b) => b.count - a.count);

    /* Par type */
    const typesMap = {};
    filtered.forEach(c => {
        const t = c.donnees?.type_collecte ?? 'Non spécifié';
        typesMap[t] = (typesMap[t] ?? 0) + 1;
    });
    const parType = Object.entries(typesMap).sort((a, b) => b[1] - a[1]);

    /* Types uniques pour le filtre */
    const typesUniques = [...new Set(collectes.map(c => c.donnees?.type_collecte).filter(Boolean))];

    /* Export CSV */
    const exportCSV = () => {
        const headers = ['ID', 'Projet', 'Type', 'Statut', 'Date', 'Latitude', 'Longitude'];
        const rows = filtered.map(c => [
            c.id, `Projet #${c.projetId}`, c.donnees?.type_collecte ?? '', c.statut,
            c.collectedAt ? new Date(c.collectedAt).toLocaleDateString('fr-FR') : '',
            c.latitude ?? '', c.longitude ?? '',
        ]);
        const csv = [headers, ...rows].map(r => r.join(';')).join('\n');
        const blob = new Blob(['\uFEFF' + csv], { type: 'text/csv;charset=utf-8;' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url; a.download = `rapport_collectes_${new Date().toISOString().slice(0,10)}.csv`;
        a.click(); URL.revokeObjectURL(url);
    };

    /* Print */
    const handlePrint = () => {
        setPrinting(true);
        setTimeout(() => { window.print(); setPrinting(false); }, 100);
    };

    if (loading) return (
        <div style={{ padding: '40px 0', textAlign: 'center', color: 'var(--c-ink-2)' }}>
            <RefreshCw size={24} style={{ animation: 'spin 1s linear infinite', marginBottom: 12 }} />
            <p>Chargement du rapport…</p>
        </div>
    );

    if (error) return (
        <div style={{ padding: '40px 0', textAlign: 'center', color: '#dc2626' }}>
            <p>{error}</p>
            <button onClick={fetchAll} className="btn-primary" style={{ marginTop: 12 }}>Réessayer</button>
        </div>
    );

    return (
        <div style={{ padding: '28px 0' }}>

            {/* Header */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 24 }} className="animate-fade-up">
                <div>
                    <h1 style={{ margin: 0, fontSize: '1.5rem', fontWeight: 700, color: 'var(--c-ink)', letterSpacing: '-0.02em' }}>Rapport</h1>
                    <p style={{ margin: '4px 0 0', fontSize: '0.855rem', color: 'var(--c-ink-2)' }}>
                        Analyse de {fmt(total)} collecte{total > 1 ? 's' : ''} · Mis à jour maintenant
                    </p>
                </div>
                <div style={{ display: 'flex', gap: 8 }}>
                    <button onClick={handlePrint}
                            style={{ display: 'flex', alignItems: 'center', gap: 6, padding: '8px 14px', border: '1.5px solid var(--c-border)', borderRadius: 9, background: '#fff', cursor: 'pointer', fontSize: '0.8rem', fontWeight: 600, color: 'var(--c-ink)', fontFamily: 'var(--font-sans)' }}>
                        <Printer size={13} /> Imprimer
                    </button>
                    <button onClick={exportCSV}
                            style={{ display: 'flex', alignItems: 'center', gap: 6, padding: '8px 14px', border: '1.5px solid var(--c-border)', borderRadius: 9, background: '#fff', cursor: 'pointer', fontSize: '0.8rem', fontWeight: 600, color: 'var(--c-ink)', fontFamily: 'var(--font-sans)' }}>
                        <Download size={13} /> Exporter CSV
                    </button>
                    <button onClick={fetchAll}
                            style={{ width: 36, height: 36, border: '1.5px solid var(--c-border)', borderRadius: 9, background: '#fff', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--c-ink-2)' }}>
                        <RefreshCw size={13} />
                    </button>
                </div>
            </div>

            {/* Filtres */}
            <div className="card animate-fade-up delay-50" style={{ padding: '16px 20px', marginBottom: 20 }}>
                <div style={{ display: 'flex', gap: 12, alignItems: 'center', flexWrap: 'wrap' }}>
                    <Filter size={14} color="var(--c-ink-2)" />
                    <span style={{ fontSize: '0.78rem', fontWeight: 600, color: 'var(--c-ink-2)' }}>Filtres :</span>

                    <select value={projetFilter} onChange={e => setProjetFilter(e.target.value)}
                            style={{ padding: '6px 10px', border: '1px solid var(--c-border)', borderRadius: 7, fontSize: '0.78rem', fontFamily: 'var(--font-sans)', background: '#fff', cursor: 'pointer', color: 'var(--c-ink)' }}>
                        <option value="">Tous les projets</option>
                        {projets.map(p => <option key={p.id} value={p.id}>#{p.id} — {p.titre}</option>)}
                    </select>

                    <select value={typeFilter} onChange={e => setTypeFilter(e.target.value)}
                            style={{ padding: '6px 10px', border: '1px solid var(--c-border)', borderRadius: 7, fontSize: '0.78rem', fontFamily: 'var(--font-sans)', background: '#fff', cursor: 'pointer', color: 'var(--c-ink)' }}>
                        <option value="">Tous les types</option>
                        {typesUniques.map(t => <option key={t} value={t}>{t}</option>)}
                    </select>

                    <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                        <Calendar size={13} color="var(--c-ink-3)" />
                        <input type="date" value={dateFrom} onChange={e => setDateFrom(e.target.value)}
                               style={{ padding: '5px 8px', border: '1px solid var(--c-border)', borderRadius: 7, fontSize: '0.78rem', fontFamily: 'var(--font-sans)', color: 'var(--c-ink)' }} />
                        <span style={{ fontSize: '0.78rem', color: 'var(--c-ink-3)' }}>→</span>
                        <input type="date" value={dateTo} onChange={e => setDateTo(e.target.value)}
                               style={{ padding: '5px 8px', border: '1px solid var(--c-border)', borderRadius: 7, fontSize: '0.78rem', fontFamily: 'var(--font-sans)', color: 'var(--c-ink)' }} />
                    </div>

                    {(projetFilter || typeFilter || dateFrom || dateTo) && (
                        <button onClick={() => { setProjetFilter(''); setTypeFilter(''); setDateFrom(''); setDateTo(''); }}
                                style={{ padding: '5px 10px', border: 'none', borderRadius: 7, background: '#fee2e2', color: '#dc2626', fontSize: '0.72rem', fontWeight: 700, cursor: 'pointer', fontFamily: 'var(--font-sans)' }}>
                            ✕ Réinitialiser
                        </button>
                    )}
                </div>
            </div>

            {/* KPIs */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 12, marginBottom: 20 }} className="rapport-kpis animate-fade-up delay-100">
                {[
                    { label: 'Total collectes', value: total, icon: FileText, color: '#2563eb', bg: '#dbeafe' },
                    { label: 'Validées', value: valides, icon: CheckCircle2, color: '#059669', bg: '#d1fae5', sub: pct(valides, total) },
                    { label: 'En attente', value: enAttente, icon: Clock, color: '#d97706', bg: '#fef3c7', sub: pct(enAttente, total) },
                    { label: 'Rejetées', value: rejetes, icon: XCircle, color: '#dc2626', bg: '#fee2e2', sub: pct(rejetes, total) },
                ].map(k => {
                    const Icon = k.icon;
                    return (
                        <div key={k.label} className="card" style={{ padding: '18px 20px' }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 12 }}>
                                <div style={{ width: 36, height: 36, borderRadius: 10, background: k.bg, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                    <Icon size={16} color={k.color} />
                                </div>
                                {k.sub && <span style={{ fontSize: '0.72rem', fontWeight: 700, color: k.color, background: k.bg, padding: '3px 8px', borderRadius: 99 }}>{k.sub}</span>}
                            </div>
                            <div style={{ fontSize: '1.8rem', fontWeight: 800, color: k.color, lineHeight: 1 }}>{fmt(k.value)}</div>
                            <div style={{ fontSize: '0.78rem', color: 'var(--c-ink-2)', marginTop: 4, fontWeight: 500 }}>{k.label}</div>
                        </div>
                    );
                })}
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 20, marginBottom: 20 }} className="rapport-grid">

                {/* Par projet */}
                <div className="card animate-fade-up delay-150" style={{ overflow: 'hidden' }}>
                    <div style={{ padding: '16px 20px', borderBottom: '1px solid var(--c-border)' }}>
                        <h3 style={{ margin: 0, fontSize: '0.9rem', fontWeight: 700, color: 'var(--c-ink)' }}>Collectes par projet</h3>
                    </div>
                    <div style={{ padding: '16px 20px' }}>
                        {parProjet.length === 0 ? (
                            <p style={{ textAlign: 'center', color: 'var(--c-ink-3)', fontSize: '0.82rem', margin: 0 }}>Aucune donnée</p>
                        ) : parProjet.map(p => (
                            <div key={p.id} style={{ marginBottom: 14 }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6 }}>
                                    <span style={{ fontSize: '0.82rem', fontWeight: 600, color: 'var(--c-ink)' }}>{p.titre || `Projet #${p.id}`}</span>
                                    <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
                                        <span style={{ fontSize: '0.7rem', color: '#059669', fontWeight: 700 }}>{p.valides} ✓</span>
                                        <span style={{ fontSize: '0.82rem', fontWeight: 700, color: 'var(--c-accent)' }}>{p.count}</span>
                                    </div>
                                </div>
                                <MiniBar value={p.count} max={parProjet[0]?.count ?? 1} color="var(--c-accent)" />
                            </div>
                        ))}
                    </div>
                </div>

                {/* Par type */}
                <div className="card animate-fade-up delay-200" style={{ overflow: 'hidden' }}>
                    <div style={{ padding: '16px 20px', borderBottom: '1px solid var(--c-border)' }}>
                        <h3 style={{ margin: 0, fontSize: '0.9rem', fontWeight: 700, color: 'var(--c-ink)' }}>Collectes par type</h3>
                    </div>
                    <div style={{ padding: '16px 20px' }}>
                        {parType.length === 0 ? (
                            <p style={{ textAlign: 'center', color: 'var(--c-ink-3)', fontSize: '0.82rem', margin: 0 }}>Aucune donnée</p>
                        ) : parType.map(([type, count], i) => {
                            const colors = ['#2563eb', '#7c3aed', '#059669', '#d97706', '#dc2626', '#0891b2'];
                            const color = colors[i % colors.length];
                            return (
                                <div key={type} style={{ marginBottom: 14 }}>
                                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6 }}>
                                        <span style={{ fontSize: '0.82rem', fontWeight: 600, color: 'var(--c-ink)' }}>{type}</span>
                                        <span style={{ fontSize: '0.82rem', fontWeight: 700, color }}>{count} ({pct(count, total)})</span>
                                    </div>
                                    <MiniBar value={count} max={parType[0]?.[1] ?? 1} color={color} />
                                </div>
                            );
                        })}
                    </div>
                </div>
            </div>

            {/* Tableau détaillé */}
            <div className="card animate-fade-up delay-250" style={{ overflow: 'hidden' }}>
                <div style={{ padding: '16px 20px', borderBottom: '1px solid var(--c-border)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <h3 style={{ margin: 0, fontSize: '0.9rem', fontWeight: 700, color: 'var(--c-ink)' }}>Données détaillées</h3>
                    <span style={{ fontSize: '0.72rem', color: 'var(--c-ink-3)', fontWeight: 600 }}>{fmt(filtered.length)} entrée{filtered.length > 1 ? 's' : ''}</span>
                </div>
                <div style={{ overflowX: 'auto' }}>
                    <table className="data-table">
                        <thead>
                        <tr>
                            <th>ID</th><th>Projet</th><th>Type de collecte</th><th>Date</th>
                            <th>Localisation</th><th>Statut</th>
                        </tr>
                        </thead>
                        <tbody>
                        {filtered.length === 0 ? (
                            <tr>
                                <td colSpan={6} style={{ textAlign: 'center', padding: '40px 20px', color: 'var(--c-ink-3)' }}>
                                    Aucune collecte ne correspond aux filtres sélectionnés
                                </td>
                            </tr>
                        ) : filtered.slice(0, 100).map(c => {
                            const cfg = STATUS_CONFIG[c.statut] ?? { label: c.statut, color: '#374151', bg: '#f3f4f6' };
                            return (
                                <tr key={c.id}>
                                    <td><span style={{ fontFamily: 'var(--font-mono)', fontWeight: 700, fontSize: '0.82rem', color: 'var(--c-accent)' }}>#{c.id}</span></td>
                                    <td style={{ fontSize: '0.82rem', color: 'var(--c-ink-2)' }}>
                                        {projets.find(p => p.id === c.projetId)?.titre ?? `Projet #${c.projetId}`}
                                    </td>
                                    <td style={{ fontSize: '0.82rem', color: 'var(--c-ink)' }}>{c.donnees?.type_collecte ?? '—'}</td>
                                    <td style={{ fontSize: '0.78rem', color: 'var(--c-ink-2)' }}>
                                        {c.collectedAt ? new Date(c.collectedAt).toLocaleDateString('fr-FR') : '—'}
                                    </td>
                                    <td style={{ fontSize: '0.78rem', color: 'var(--c-ink-2)' }}>
                                        {c.latitude ? `${c.latitude}, ${c.longitude}` : '—'}
                                    </td>
                                    <td>
                      <span style={{ display: 'inline-flex', alignItems: 'center', gap: 4, padding: '3px 9px', borderRadius: 99, fontSize: '0.7rem', fontWeight: 700, background: cfg.bg, color: cfg.color }}>
                        {cfg.label}
                      </span>
                                    </td>
                                </tr>
                            );
                        })}
                        </tbody>
                    </table>
                    {filtered.length > 100 && (
                        <p style={{ textAlign: 'center', padding: '12px', fontSize: '0.78rem', color: 'var(--c-ink-3)' }}>
                            Affichage limité à 100 lignes. Exportez le CSV pour voir toutes les données.
                        </p>
                    )}
                </div>
            </div>

            <style>{`
        @keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
        @media (max-width: 900px) {
          .rapport-grid { grid-template-columns: 1fr !important; }
          .rapport-kpis { grid-template-columns: repeat(2, 1fr) !important; }
        }
        @media print {
          .rapport-kpis, .rapport-grid { break-inside: avoid; }
          button { display: none !important; }
        }
      `}</style>
        </div>
    );
};

export default Rapport;

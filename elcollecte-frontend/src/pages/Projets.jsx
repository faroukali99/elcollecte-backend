import React, { useState, useEffect, useCallback } from 'react';
import client from '../api/client';
import { Search, Plus, Filter, RefreshCw } from 'lucide-react';

// ── Helpers ────────────────────────────────────────────────────────────────────
const STATUS_MAP = {
  ACTIF:     { label: 'En cours',  bg: '#dbeafe', color: '#1d4ed8' },
  BROUILLON: { label: 'En attente',bg: '#fef3c7', color: '#92400e' },
  SUSPENDU:  { label: 'Annulé',   bg: '#fee2e2', color: '#991b1b' },
  TERMINE:   { label: 'Terminé',  bg: '#d1fae5', color: '#065f46' },
};

function progress(statut) {
  return { BROUILLON: 5, ACTIF: 50, SUSPENDU: 30, TERMINE: 100 }[statut] ?? 0;
}

// ── Skeleton ───────────────────────────────────────────────────────────────────
const Skeleton = () => (
  <div className="card" style={{ padding: 20 }}>
    {[80, 60, 40].map((w, i) => (
      <div key={i} className="skeleton" style={{ height: 14, width: `${w}%`, borderRadius: 6, marginBottom: i < 2 ? 10 : 0 }} />
    ))}
  </div>
);

// ── ProjectCard ────────────────────────────────────────────────────────────────
const ProjectCard = ({ project }) => {
  const chip = STATUS_MAP[project.statut] ?? { label: project.statut, bg: '#f3f4f6', color: '#374151' };
  const pct  = progress(project.statut);

  return (
    <div className="card animate-fade-up" style={{ padding: 20, display: 'flex', flexDirection: 'column', gap: 12 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: 8 }}>
        <h3 style={{ margin: 0, fontSize: '0.95rem', fontWeight: 700, color: 'var(--c-ink)', lineHeight: 1.3 }}>
          {project.titre}
        </h3>
        <span style={{
          flexShrink: 0, padding: '3px 10px', borderRadius: 99,
          fontSize: '0.72rem', fontWeight: 700,
          background: chip.bg, color: chip.color,
        }}>
          {chip.label}
        </span>
      </div>

      {project.description && (
        <p style={{ margin: 0, fontSize: '0.82rem', color: 'var(--c-ink-2)', lineHeight: 1.5, display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
          {project.description}
        </p>
      )}

      <div>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6 }}>
          <span style={{ fontSize: '0.72rem', color: 'var(--c-ink-2)', fontWeight: 600 }}>Progression</span>
          <span style={{ fontSize: '0.72rem', color: 'var(--c-ink-2)', fontFamily: 'var(--font-mono)' }}>{pct}%</span>
        </div>
        <div className="progress-bar-track">
          <div className="progress-bar-fill" style={{ width: `${pct}%`, background: chip.color }} />
        </div>
      </div>

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 4 }}>
        <span style={{ fontSize: '0.75rem', color: 'var(--c-ink-3)' }}>
          {project.dateDebut} {project.dateFin ? `→ ${project.dateFin}` : ''}
        </span>
        <button style={{
          padding: '6px 14px', border: '1.5px solid var(--c-accent)', borderRadius: 8,
          background: 'transparent', color: 'var(--c-accent)', cursor: 'pointer',
          fontSize: '0.78rem', fontWeight: 600, fontFamily: 'var(--font-sans)',
          transition: 'background 0.15s',
        }}
          onMouseEnter={e => { e.currentTarget.style.background = '#dbeafe'; }}
          onMouseLeave={e => { e.currentTarget.style.background = 'transparent'; }}
        >
          Voir détails
        </button>
      </div>
    </div>
  );
};

// ── Page ───────────────────────────────────────────────────────────────────────
const Projets = () => {
  const [projets,    setProjets]    = useState([]);
  const [loading,    setLoading]    = useState(true);
  const [error,      setError]      = useState(null);
  const [search,     setSearch]     = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [page,       setPage]       = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const fetchProjets = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const { data } = await client.get(`/projets?page=${page}&size=12`);
      // La réponse est paginée : { content, totalPages, ... }
      const list = data.content ?? data ?? [];
      setProjets(list);
      setTotalPages(data.totalPages ?? 1);
    } catch (err) {
      setError('Impossible de charger les projets. Vérifiez que le backend est démarré.');
      console.error('[Projets]', err);
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => { fetchProjets(); }, [fetchProjets]);

  // Filtrage local (search + statut)
  const filtered = projets.filter(p => {
    const matchSearch = p.titre?.toLowerCase().includes(search.toLowerCase());
    const matchStatus = statusFilter === 'all' || p.statut === statusFilter;
    return matchSearch && matchStatus;
  });

  return (
    <div style={{ padding: '28px 0' }}>
      {/* Header */}
      <div className="animate-fade-up" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24, flexWrap: 'wrap', gap: 12 }}>
        <div>
          <h1 style={{ margin: 0, fontSize: '1.5rem', fontWeight: 700, color: 'var(--c-ink)', letterSpacing: '-0.02em' }}>Projets</h1>
          <p style={{ margin: '4px 0 0', fontSize: '0.855rem', color: 'var(--c-ink-2)' }}>
            {loading ? '…' : `${filtered.length} projet${filtered.length > 1 ? 's' : ''}`}
          </p>
        </div>
        <div style={{ display: 'flex', gap: 10 }}>
          <button
            onClick={fetchProjets}
            style={{
              width: 38, height: 38, border: '1.5px solid var(--c-border)', borderRadius: 10,
              background: '#fff', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center',
              color: 'var(--c-ink-2)', transition: 'border-color 0.15s',
            }}
            title="Actualiser"
          >
            <RefreshCw size={15} />
          </button>
          <button className="btn-primary">
            <Plus size={15} /> Nouveau Projet
          </button>
        </div>
      </div>

      {/* Filters */}
      <div className="card animate-fade-up delay-100" style={{ padding: '14px 16px', marginBottom: 20, display: 'flex', gap: 12, flexWrap: 'wrap' }}>
        <div style={{ position: 'relative', flex: 1, minWidth: 200 }}>
          <Search size={14} style={{ position: 'absolute', left: 12, top: '50%', transform: 'translateY(-50%)', color: 'var(--c-ink-3)' }} />
          <input
            className="input-field"
            style={{ paddingLeft: 34 }}
            placeholder="Rechercher un projet…"
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>
        <div style={{ position: 'relative' }}>
          <Filter size={14} style={{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)', color: 'var(--c-ink-3)' }} />
          <select
            className="input-field"
            style={{ paddingLeft: 30, minWidth: 160, cursor: 'pointer' }}
            value={statusFilter}
            onChange={e => setStatusFilter(e.target.value)}
          >
            <option value="all">Tous les statuts</option>
            <option value="ACTIF">En cours</option>
            <option value="BROUILLON">En attente</option>
            <option value="TERMINE">Terminé</option>
            <option value="SUSPENDU">Annulé</option>
          </select>
        </div>
      </div>

      {/* Error */}
      {error && (
        <div style={{ padding: '14px 18px', borderRadius: 10, marginBottom: 20, background: '#fee2e2', border: '1px solid #fca5a5', color: '#991b1b', fontSize: '0.875rem' }}>
          ⚠️ {error}
        </div>
      )}

      {/* Grid */}
      {loading ? (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: 16 }}>
          {Array.from({ length: 6 }).map((_, i) => <Skeleton key={i} />)}
        </div>
      ) : filtered.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '60px 20px', color: 'var(--c-ink-2)' }}>
          <p style={{ fontSize: '1.1rem', fontWeight: 600, marginBottom: 8 }}>Aucun projet trouvé</p>
          <p style={{ fontSize: '0.875rem' }}>Essayez de modifier vos filtres.</p>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: 16 }}>
          {filtered.map((p, i) => (
            <div key={p.id} style={{ animationDelay: `${i * 50}ms` }}>
              <ProjectCard project={p} />
            </div>
          ))}
        </div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div style={{ display: 'flex', justifyContent: 'center', gap: 8, marginTop: 32 }}>
          <button
            onClick={() => setPage(p => Math.max(0, p - 1))}
            disabled={page === 0}
            style={{
              padding: '8px 16px', border: '1.5px solid var(--c-border)', borderRadius: 8,
              background: '#fff', cursor: page === 0 ? 'not-allowed' : 'pointer',
              fontSize: '0.855rem', fontWeight: 600, fontFamily: 'var(--font-sans)',
              color: page === 0 ? 'var(--c-ink-3)' : 'var(--c-ink)',
            }}
          >
            ← Précédent
          </button>
          <span style={{ padding: '8px 16px', fontSize: '0.855rem', color: 'var(--c-ink-2)', alignSelf: 'center' }}>
            Page {page + 1} / {totalPages}
          </span>
          <button
            onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
            disabled={page >= totalPages - 1}
            style={{
              padding: '8px 16px', border: '1.5px solid var(--c-border)', borderRadius: 8,
              background: '#fff', cursor: page >= totalPages - 1 ? 'not-allowed' : 'pointer',
              fontSize: '0.855rem', fontWeight: 600, fontFamily: 'var(--font-sans)',
              color: page >= totalPages - 1 ? 'var(--c-ink-3)' : 'var(--c-ink)',
            }}
          >
            Suivant →
          </button>
        </div>
      )}
    </div>
  );
};

export default Projets;

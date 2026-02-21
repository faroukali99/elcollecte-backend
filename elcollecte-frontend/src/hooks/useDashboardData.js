import { useState, useEffect } from 'react';
import analytiqueApi from '../api/analytiqueApi';
import projetsApi    from '../api/projetsApi';

/**
 * Hook pour charger toutes les données du tableau de bord.
 * Utilise les services API typés plutôt que client.get() direct.
 *
 * @param {'week'|'month'|'year'} period
 */
export const useDashboardData = (period = 'month') => {
  const [stats, setStats] = useState({
    totalCollectes: 0,
    projetsActifs:  0,
    validationRate: 0,
    usersActifs:    0,
    collectesAujourdhui: 0,
  });
  const [chartData,       setChartData]       = useState([]);
  const [recentProjects,  setRecentProjects]   = useState([]);
  const [loading,         setLoading]          = useState(true);
  const [error,           setError]            = useState(null);

  useEffect(() => {
    let cancelled = false;

    const fetchAll = async () => {
      setLoading(true);
      setError(null);

      try {
        // Requêtes parallèles pour la performance
        const days = period === 'week' ? 7 : period === 'month' ? 30 : 365;

        const [dashboard, timeline, projetsPage] = await Promise.allSettled([
          analytiqueApi.getDashboard(),
          analytiqueApi.getGlobalTimeline(days),
          projetsApi.list(0, 5),
        ]);

        if (cancelled) return;

        // ── Dashboard KPIs ──────────────────────────────────────────────────
        if (dashboard.status === 'fulfilled') {
          const d = dashboard.value;
          setStats({
            totalCollectes:      d.totalCollectes      ?? 0,
            projetsActifs:       d.projetsActifs        ?? 0,
            validationRate:      d.tauxValidation       ?? 0,
            usersActifs:         d.usersActifs          ?? 0, // non fourni par le backend actuel
            collectesAujourdhui: d.collectesAujourdhui ?? 0,
          });
        }

        // ── Timeline (graphique barres) ─────────────────────────────────────
        if (timeline.status === 'fulfilled') {
          setChartData(timeline.value);
        }

        // ── Projets récents ─────────────────────────────────────────────────
        if (projetsPage.status === 'fulfilled') {
          const projets = projetsPage.value.content ?? projetsPage.value ?? [];
          setRecentProjects(
              projets.map(p => ({
                id:       p.id,
                name:     p.titre,
                lead:     p.chefProjetId ? `Chef #${p.chefProjetId}` : '—',
                status:   mapStatut(p.statut),
                progress: estimateProgress(p.statut),
              }))
          );
        }

      } catch (err) {
        if (!cancelled) {
          console.error('[useDashboardData]', err);
          setError(err);
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    };

    fetchAll();
    return () => { cancelled = true; };
  }, [period]);

  return { stats, chartData, recentProjects, loading, error };
};

// ── Helpers ───────────────────────────────────────────────────────────────────

function mapStatut(statut) {
  const map = {
    ACTIF:     'En cours',
    BROUILLON: 'En attente',
    SUSPENDU:  'Annulé',
    TERMINE:   'Terminé',
  };
  return map[statut] ?? statut ?? 'Inconnu';
}

function estimateProgress(statut) {
  const map = {
    BROUILLON: 5,
    ACTIF:     50,
    SUSPENDU:  30,
    TERMINE:   100,
  };
  return map[statut] ?? 0;
}

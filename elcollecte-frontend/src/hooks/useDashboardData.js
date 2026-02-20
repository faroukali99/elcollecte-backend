import { useState, useEffect } from 'react';
import client from '../api/client';

export const useDashboardData = (period = 'month') => {
  const [stats, setStats] = useState({
    totalCollectes: 0,
    projetsActifs: 0,
    validationRate: 0,
    usersActifs: 0,
  });
  const [chartData, setChartData]       = useState([]);
  const [recentProjects, setRecentProjects] = useState([]);
  const [loading, setLoading]           = useState(true);
  const [error, setError]               = useState(null);

  useEffect(() => {
    const fetchAll = async () => {
      setLoading(true);
      setError(null);
      try {
        // 1. KPIs depuis service-analytique
        const { data: dash } = await client.get('/analytics/dashboard');
        setStats({
          totalCollectes:  dash.totalCollectes  ?? 0,
          projetsActifs:   dash.projetsActifs   ?? 0,
          validationRate:  dash.tauxValidation  ?? 0,
          usersActifs:     dash.usersActifs     ?? 0,
        });

        // 2. Timeline (graphique barres)
        const days = period === 'week' ? 7 : period === 'month' ? 30 : 365;
        const { data: timeline } = await client.get(
          `/analytics/timeline?days=${days}`
        );
        // Normalise en { name, value }
        const chartFormatted = Array.isArray(timeline)
          ? timeline.map(item => ({
              name:  item.dateJour ?? item.date ?? item.name,
              value: item.nbCollectes ?? item.value ?? 0,
            }))
          : [];
        setChartData(chartFormatted);

        // 3. Projets récents depuis service-projet
        const { data: projetsPage } = await client.get(
          '/projets?page=0&size=5'
        );
        const projets = projetsPage.content ?? projetsPage ?? [];
        const projectsMapped = projets.map(p => ({
          id:       p.id,
          name:     p.titre,
          lead:     `Chef #${p.chefProjetId}`,
          status:   mapStatut(p.statut),
          progress: estimateProgress(p.statut),
        }));
        setRecentProjects(projectsMapped);

      } catch (err) {
        console.error('[useDashboardData]', err);
        setError(err);
        // Fallback sur données vides plutôt que crash
        setStats({ totalCollectes: 0, projetsActifs: 0, validationRate: 0, usersActifs: 0 });
        setChartData([]);
        setRecentProjects([]);
      } finally {
        setLoading(false);
      }
    };

    fetchAll();
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

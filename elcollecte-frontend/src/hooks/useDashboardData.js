import { useState, useEffect } from 'react';

// Fonctions pour générer des données simulées
const generateRandomStats = (period) => {
  const multiplier = period === 'week' ? 0.25 : period === 'month' ? 1 : 4;
  return {
    totalCollectes: Math.floor((Math.random() * 200 + 800) * multiplier),
    projetsActifs: Math.floor(Math.random() * 5) + 10,
    validationRate: parseFloat((Math.random() * 5 + 90).toFixed(1)),
    usersActifs: Math.floor((Math.random() * 20 + 50) * multiplier),
  };
};

const generateChartData = (period) => {
  const points = period === 'week' ? 7 : period === 'month' ? 30 : 12;
  const labels = period === 'week'
    ? ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim']
    : period === 'month'
    ? Array.from({ length: 30 }, (_, i) => `${i + 1}`)
    : ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Juin', 'Juil', 'Août', 'Sep', 'Oct', 'Nov', 'Déc'];

  return labels.map(label => ({
    name: label,
    value: Math.floor(Math.random() * 100) + 10,
  }));
};

const generateRecentProjects = () => [
  { id: 1, name: 'Collecte Zone Nord', lead: 'Alice Martin', status: 'En cours', progress: Math.floor(Math.random() * 40) + 40 },
  { id: 2, name: 'Recyclage Plastique', lead: 'Bob Dupont', status: 'Terminé', progress: 100 },
  { id: 3, name: 'Sensibilisation Écoles', lead: 'Charlie Durand', status: 'En attente', progress: Math.floor(Math.random() * 20) },
  { id: 4, name: 'Nettoyage Plage', lead: 'David Leroy', status: 'En cours', progress: Math.floor(Math.random() * 50) + 20 },
  { id: 5, name: 'Compostage Quartier Sud', lead: 'Eve Moreau', status: 'Planifié', progress: 0 },
];


export const useDashboardData = (period = 'month') => {
  const [stats, setStats] = useState({
    totalCollectes: 0,
    projetsActifs: 0,
    validationRate: 0,
    usersActifs: 0,
  });
  const [chartData, setChartData] = useState([]);
  const [recentProjects, setRecentProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDashboardData = async () => {
      setLoading(true);
      try {
        // Simuler une latence réseau
        await new Promise(resolve => setTimeout(resolve, 500));

        // Générer des données simulées basées sur la période
        setStats(generateRandomStats(period));
        setChartData(generateChartData(period));
        setRecentProjects(generateRecentProjects());

        setLoading(false);
      } catch (err) {
        setError(err);
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, [period]); // <-- Le hook se ré-exécutera à chaque changement de 'period'

  return { stats, chartData, recentProjects, loading, error };
};
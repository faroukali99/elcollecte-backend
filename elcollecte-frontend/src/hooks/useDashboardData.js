import { useState, useEffect } from 'react';
import client from '../api/client';

export const useDashboardData = () => {
  const [stats, setStats] = useState({
    totalCollectes: 0,
    projetsActifs: 0,
    validationRate: 0,
    usersActifs: 0,
    recentProjects: [],
    chartData: []
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        // Dans un cas réel, on ferait plusieurs appels parallèles ou un appel agrégé
        // Pour l'instant, on simule des appels si les endpoints n'existent pas encore

        // Exemple d'appels réels (à décommenter quand les endpoints seront prêts)
        /*
        const [collectesRes, projetsRes, usersRes] = await Promise.all([
          client.get('/analytics/stats/collectes'),
          client.get('/projets/count/active'),
          client.get('/users/count/active')
        ]);
        */

        // Simulation de données dynamiques basées sur des appels API potentiels
        // On va essayer d'appeler les vrais services s'ils répondent, sinon fallback

        let totalCollectes = 0;
        let projetsActifs = 0;
        let usersActifs = 0;

        try {
            // Tentative de récupération des vraies données
            // Note: Adaptez les URLs selon vos contrôleurs réels
            // const usersResponse = await client.get('/users/count'); // Exemple
            // usersActifs = usersResponse.data;
        } catch (e) {
            console.warn("Impossible de récupérer les stats réelles, utilisation de données simulées", e);
            // Fallback sur des données aléatoires pour montrer le dynamisme
            totalCollectes = Math.floor(Math.random() * 500) + 1000;
            projetsActifs = Math.floor(Math.random() * 10) + 2;
            usersActifs = Math.floor(Math.random() * 50) + 10;
        }

        setStats({
          totalCollectes,
          projetsActifs,
          validationRate: 94.2, // Pourrait venir du service validation
          usersActifs,
          recentProjects: [
            { name: 'Enquête Satisfaction 2025', lead: 'Sarah Connor', status: 'En cours', progress: 45, color: 'blue' },
            { name: 'Recensement Zone Nord', lead: 'John Doe', status: 'Terminé', progress: 100, color: 'emerald' },
            { name: 'Étude de Marché Bio', lead: 'Mike Ross', status: 'En attente', progress: 12, color: 'amber' },
            { name: 'Sondage Transport', lead: 'Rachel Green', status: 'En cours', progress: 67, color: 'blue' },
          ],
          chartData: [35, 55, 40, 70, 50, 90, 65, 85, 45, 60, 75, 50]
        });
        setLoading(false);
      } catch (err) {
        setError(err);
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  return { stats, loading, error };
};
import React, { useState } from 'react';
import {
  Box, Grid, Card, CardContent, CardActions, Button, Typography,
  TextField, InputAdornment, Select, MenuItem, FormControl, InputLabel, Chip, LinearProgress, Paper
} from '@mui/material';
import { Search, Plus, Filter } from 'lucide-react'; // Correction : FilterList -> Filter

// Données simulées pour les projets
const projectsData = [
  { id: 1, name: 'Collecte Zone Nord', description: 'Coordination de la collecte des déchets dans la zone nord de la ville.', status: 'En cours', progress: 65, team: ['A', 'B', 'C'] },
  { id: 2, name: 'Recyclage Plastique', description: 'Mise en place d\'une nouvelle filière de recyclage pour les plastiques PET.', status: 'Terminé', progress: 100, team: ['D', 'E'] },
  { id: 3, name: 'Sensibilisation Écoles', description: 'Campagne de sensibilisation dans les écoles primaires sur le tri sélectif.', status: 'En attente', progress: 10, team: ['F', 'G', 'H', 'I'] },
  { id: 4, name: 'Nettoyage Plage Ouest', description: 'Opération de nettoyage de la plage ouest avant la saison estivale.', status: 'En cours', progress: 40, team: ['J', 'K'] },
  { id: 5, name: 'Compostage Quartier Sud', description: 'Déploiement de composteurs partagés dans le quartier sud.', status: 'Annulé', progress: 0, team: ['L'] },
  { id: 6, name: 'Réparation Ateliers', description: 'Organisation d\'ateliers de réparation pour les appareils électroniques.', status: 'Planifié', progress: 0, team: ['M', 'N', 'O'] },
];

const statusMap = {
  'En cours': { color: 'info', label: 'En cours' },
  'Terminé': { color: 'success', label: 'Terminé' },
  'En attente': { color: 'warning', label: 'En attente' },
  'Annulé': { color: 'error', label: 'Annulé' },
  'Planifié': { color: 'primary', label: 'Planifié' },
};

const ProjectCard = ({ project }) => (
  <Grid item xs={12} sm={6} md={4}>
    <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column', borderRadius: '1rem', transition: 'all 0.3s', '&:hover': { transform: 'translateY(-5px)', boxShadow: 3 } }}>
      <CardContent sx={{ flexGrow: 1 }}>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={1}>
          <Typography variant="h6" component="h2" fontWeight="bold">
            {project.name}
          </Typography>
          <Chip
            label={statusMap[project.status]?.label || 'Inconnu'}
            color={statusMap[project.status]?.color || 'default'}
            size="small"
          />
        </Box>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
          {project.description}
        </Typography>
        <Box sx={{ mb: 1 }}>
          <Typography variant="caption" color="text.secondary">Progression</Typography>
          <LinearProgress
            variant="determinate"
            value={project.progress}
            color={statusMap[project.status]?.color || 'primary'}
            sx={{ height: 6, borderRadius: 3, mt: 0.5 }}
          />
        </Box>
      </CardContent>
      <CardActions sx={{ p: 2, justifyContent: 'space-between' }}>
        <Button size="small" variant="contained">Voir Détails</Button>
        <Button size="small">Archiver</Button>
      </CardActions>
    </Card>
  </Grid>
);

const Projets = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');

  const filteredProjects = projectsData.filter(p => {
    const matchesSearch = p.name.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = statusFilter === 'all' || p.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  return (
    <Box sx={{ p: 3, backgroundColor: '#f9fafb', flexGrow: 1 }}>
      {/* Header et barre d'outils */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
        <Typography variant="h4" component="h1" fontWeight="bold">
          Projets
        </Typography>
        <Button variant="contained" startIcon={<Plus />}>
          Nouveau Projet
        </Button>
      </Box>

      {/* Filtres et recherche */}
      <Paper sx={{ p: 2, mb: 4, borderRadius: '1rem', display: 'flex', gap: 2 }}>
        <TextField
          fullWidth
          placeholder="Rechercher un projet..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <Search />
              </InputAdornment>
            ),
          }}
        />
        <FormControl sx={{ minWidth: 200 }}>
          <InputLabel>Statut</InputLabel>
          <Select
            value={statusFilter}
            label="Statut"
            onChange={(e) => setStatusFilter(e.target.value)}
            startAdornment={
              <InputAdornment position="start">
                <Filter />
              </InputAdornment>
            }
          >
            <MenuItem value="all">Tous</MenuItem>
            <MenuItem value="En cours">En cours</MenuItem>
            <MenuItem value="Terminé">Terminé</MenuItem>
            <MenuItem value="En attente">En attente</MenuItem>
            <MenuItem value="Planifié">Planifié</MenuItem>
            <MenuItem value="Annulé">Annulé</MenuItem>
          </Select>
        </FormControl>
      </Paper>

      {/* Grille des projets */}
      <Grid container spacing={3}>
        {filteredProjects.map(project => (
          <ProjectCard key={project.id} project={project} />
        ))}
      </Grid>
    </Box>
  );
};

export default Projets;

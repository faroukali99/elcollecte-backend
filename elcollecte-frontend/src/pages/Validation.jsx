import React, { useState } from 'react';
import {
  Box, Grid, Paper, Typography, Button, List, ListItem, ListItemButton,
  ListItemText, Divider, Chip, TextField, Avatar, Card, CardMedia
} from '@mui/material';
import { Check, X, MessageSquare } from 'lucide-react';

// Données simulées pour les collectes en attente de validation
const pendingCollectes = [
  {
    id: 'C002',
    project: 'Nettoyage Plage Ouest',
    user: { name: 'Bob Dupont', avatar: 'B' },
    date: '2024-07-21',
    items: 32,
    description: 'Collecte réalisée sur la plage ouest. Beaucoup de plastiques et de filets de pêche abandonnés.',
    media: [
      { type: 'image', url: 'https://via.placeholder.com/400x300.png?text=Plage+1' },
      { type: 'image', url: 'https://via.placeholder.com/400x300.png?text=D%C3%A9chets' },
      { type: 'video', url: 'https://www.w3schools.com/html/mov_bbb.mp4' },
    ]
  },
  {
    id: 'C005',
    project: 'Recensement Zone Sud',
    user: { name: 'Eve Moreau', avatar: 'E' },
    date: '2024-07-23',
    items: 12,
    description: 'Porte-à-porte pour le recensement des besoins en bacs de compostage.',
    media: [
      { type: 'image', url: 'https://via.placeholder.com/400x300.png?text=Porte+%C3%A0+Porte' },
    ]
  },
];

const Validation = () => {
  const [selectedCollecte, setSelectedCollecte] = useState(pendingCollectes[0]);

  const handleSelectCollecte = (collecte) => {
    setSelectedCollecte(collecte);
  };

  return (
    <Box sx={{ p: 3, backgroundColor: '#f9fafb', height: 'calc(100vh - 64px)', display: 'flex', flexDirection: 'column' }}>
      <Typography variant="h4" component="h1" fontWeight="bold" mb={3}>
        Centre de Validation
      </Typography>

      <Grid container spacing={3} sx={{ flexGrow: 1, overflow: 'hidden' }}>
        {/* Colonne de gauche: Liste des collectes à valider */}
        <Grid item xs={12} md={4} sx={{ display: 'flex', flexDirection: 'column' }}>
          <Paper sx={{ p: 2, borderRadius: '1rem', flexGrow: 1, display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
            <Typography variant="h6" p={1}>
              Collectes en attente ({pendingCollectes.length})
            </Typography>
            <List sx={{ overflowY: 'auto', flexGrow: 1 }}>
              {pendingCollectes.map((collecte) => (
                <ListItem key={collecte.id} disablePadding>
                  <ListItemButton
                    selected={selectedCollecte?.id === collecte.id}
                    onClick={() => handleSelectCollecte(collecte)}
                    sx={{ borderRadius: '0.5rem' }}
                  >
                    <ListItemText
                      primary={collecte.project}
                      secondary={`Par ${collecte.user.name} - ${collecte.date}`}
                    />
                    <Chip label={`${collecte.items} items`} size="small" />
                  </ListItemButton>
                </ListItem>
              ))}
            </List>
          </Paper>
        </Grid>

        {/* Colonne de droite: Détails de la collecte sélectionnée */}
        <Grid item xs={12} md={8} sx={{ display: 'flex', flexDirection: 'column' }}>
          {selectedCollecte ? (
            <Paper sx={{ p: 3, borderRadius: '1rem', flexGrow: 1, display: 'flex', flexDirection: 'column', overflowY: 'auto' }}>
              {/* Header */}
              <Box mb={3}>
                <Typography variant="h5" fontWeight="bold">{selectedCollecte.project}</Typography>
                <Box display="flex" alignItems="center" gap={1} mt={1}>
                  <Avatar sx={{ width: 24, height: 24, fontSize: '0.8rem' }}>{selectedCollecte.user.avatar}</Avatar>
                  <Typography variant="body2" color="text.secondary">
                    Soumis par {selectedCollecte.user.name} le {selectedCollecte.date}
                  </Typography>
                </Box>
              </Box>

              {/* Description */}
              <Typography variant="body1" mb={3}>
                {selectedCollecte.description}
              </Typography>

              {/* Médias */}
              <Typography variant="h6" mb={2}>Médias ({selectedCollecte.media.length})</Typography>
              <Grid container spacing={2} mb={3}>
                {selectedCollecte.media.map((m, index) => (
                  <Grid item xs={12} sm={6} md={4} key={index}>
                    <Card sx={{ borderRadius: '0.5rem' }}>
                      {m.type === 'image' ? (
                        <CardMedia component="img" height="140" image={m.url} alt={`media-${index}`} />
                      ) : (
                        <CardMedia component="video" height="140" src={m.url} controls />
                      )}
                    </Card>
                  </Grid>
                ))}
              </Grid>

              <Divider sx={{ my: 2 }} />

              {/* Actions */}
              <Box>
                <Typography variant="h6" mb={2}>Action de validation</Typography>
                <TextField
                  fullWidth
                  label="Commentaire (optionnel)"
                  multiline
                  rows={3}
                  variant="outlined"
                  placeholder="Ajoutez un commentaire pour justifier votre décision..."
                />
                <Box display="flex" gap={2} mt={2}>
                  <Button variant="contained" color="success" startIcon={<Check />}>
                    Approuver
                  </Button>
                  <Button variant="outlined" color="error" startIcon={<X />}>
                    Rejeter
                  </Button>
                </Box>
              </Box>
            </Paper>
          ) : (
            <Paper sx={{ p: 3, borderRadius: '1rem', flexGrow: 1, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
              <Typography variant="h6" color="text.secondary">
                Sélectionnez une collecte à valider
              </Typography>
            </Paper>
          )}
        </Grid>
      </Grid>
    </Box>
  );
};

export default Validation;

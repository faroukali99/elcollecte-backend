import React from 'react';
import {
  Box, Grid, Paper, Typography, TextField, Button, Table, TableBody,
  TableCell, TableContainer, TableHead, TableRow, Chip, IconButton
} from '@mui/material';
import { UploadCloud, MoreVertical } from 'lucide-react';
import MediaUpload from '../components/MediaUpload'; // On garde votre composant pour l'instant

// Données simulées pour les collectes récentes
const recentCollectes = [
  { id: 'C001', project: 'Collecte Zone Nord', user: 'Alice Martin', date: '2024-07-22', items: 15, status: 'Validé' },
  { id: 'C002', project: 'Nettoyage Plage Ouest', user: 'Bob Dupont', date: '2024-07-21', items: 32, status: 'En attente' },
  { id: 'C003', project: 'Collecte Zone Nord', user: 'Alice Martin', date: '2024-07-20', items: 8, status: 'Rejeté' },
  { id: 'C004', project: 'Recyclage Plastique', user: 'Charlie Durand', date: '2024-07-19', items: 50, status: 'Validé' },
];

const statusMap = {
  'Validé': { color: 'success', label: 'Validé' },
  'En attente': { color: 'warning', label: 'En attente' },
  'Rejeté': { color: 'error', label: 'Rejeté' },
};

const Collecte = () => {
  return (
    <Box sx={{ p: 3, backgroundColor: '#f9fafb', flexGrow: 1 }}>
      <Grid container spacing={4}>
        {/* Colonne de gauche: Formulaire de nouvelle collecte */}
        <Grid item xs={12} md={5}>
          <Paper sx={{ p: 3, borderRadius: '1rem', boxShadow: 2 }}>
            <Typography variant="h5" component="h2" fontWeight="bold" mb={3}>
              Nouvelle Collecte
            </Typography>
            <form>
              <Grid container spacing={3}>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Projet associé"
                    variant="outlined"
                    defaultValue="Collecte Zone Nord" // À remplacer par un Select ou un Autocomplete
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Description"
                    multiline
                    rows={4}
                    variant="outlined"
                    placeholder="Ajoutez une description, des notes ou des observations..."
                  />
                </Grid>
                <Grid item xs={12}>
                  <Typography variant="subtitle2" color="text.secondary" mb={1}>
                    Médias (Photos, Vidéos)
                  </Typography>
                  <MediaUpload />
                </Grid>
                <Grid item xs={12}>
                  <Button
                    type="submit"
                    variant="contained"
                    fullWidth
                    startIcon={<UploadCloud />}
                    sx={{ py: 1.5 }}
                  >
                    Soumettre la collecte
                  </Button>
                </Grid>
              </Grid>
            </form>
          </Paper>
        </Grid>

        {/* Colonne de droite: Liste des collectes récentes */}
        <Grid item xs={12} md={7}>
          <Paper sx={{ p: 3, borderRadius: '1rem', boxShadow: 2 }}>
            <Typography variant="h5" component="h2" fontWeight="bold" mb={2}>
              Collectes Récentes
            </Typography>
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>ID</TableCell>
                    <TableCell>Projet</TableCell>
                    <TableCell>Date</TableCell>
                    <TableCell>Statut</TableCell>
                    <TableCell align="right">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {recentCollectes.map((collecte) => (
                    <TableRow key={collecte.id} hover>
                      <TableCell>
                        <Typography variant="body2" fontWeight="bold">{collecte.id}</Typography>
                      </TableCell>
                      <TableCell>{collecte.project}</TableCell>
                      <TableCell>{collecte.date}</TableCell>
                      <TableCell>
                        <Chip
                          label={statusMap[collecte.status]?.label || 'Inconnu'}
                          color={statusMap[collecte.status]?.color || 'default'}
                          size="small"
                        />
                      </TableCell>
                      <TableCell align="right">
                        <IconButton size="small">
                          <MoreVertical size={18} />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Collecte;

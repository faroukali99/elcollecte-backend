import React from 'react';
import {
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper,
  Typography, Box, Chip, LinearProgress, IconButton
} from '@mui/material';
import { MoreHorizontal } from 'lucide-react';

const statusColors = {
  'Terminé': 'success',
  'En cours': 'info',
  'En attente': 'warning',
  'Annulé': 'error',
};

const ProjectsTable = ({ projects }) => {
  return (
    <TableContainer component={Paper} sx={{ borderRadius: '1rem', boxShadow: 'none', border: '1px solid #e5e7eb' }}>
      <Box p={2} display="flex" justifyContent="space-between" alignItems="center">
        <Typography variant="h6" component="h2" fontWeight="600">
          Derniers Projets
        </Typography>
        <Typography variant="body2" color="primary" sx={{ cursor: 'pointer', '&:hover': { textDecoration: 'underline' } }}>
          Voir tout
        </Typography>
      </Box>
      <Table aria-label="derniers projets">
        <TableHead>
          <TableRow sx={{ '& .MuiTableCell-head': { fontWeight: '600', color: 'text.secondary', bgcolor: '#f9fafb' } }}>
            <TableCell>Nom du Projet</TableCell>
            <TableCell>Chef de Projet</TableCell>
            <TableCell>Statut</TableCell>
            <TableCell>Progression</TableCell>
            <TableCell align="right">Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {projects.map((project) => (
            <TableRow key={project.id} sx={{ '&:last-child td, &:last-child th': { border: 0 }, '&:hover': { bgcolor: '#f9fafb' } }}>
              <TableCell component="th" scope="row">
                <Typography variant="body1" fontWeight="medium" color="text.primary">
                  {project.name}
                </Typography>
              </TableCell>
              <TableCell>{project.lead}</TableCell>
              <TableCell>
                <Chip
                  label={project.status}
                  color={statusColors[project.status] || 'default'}
                  size="small"
                />
              </TableCell>
              <TableCell>
                <Box display="flex" alignItems="center">
                  <Box width="100%" mr={1}>
                    <LinearProgress
                      variant="determinate"
                      value={project.progress}
                      color={statusColors[project.status] || 'primary'}
                    />
                  </Box>
                  <Typography variant="body2" color="text.secondary">{`${project.progress}%`}</Typography>
                </Box>
              </TableCell>
              <TableCell align="right">
                <IconButton size="small">
                  <MoreHorizontal size={18} />
                </IconButton>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default ProjectsTable;

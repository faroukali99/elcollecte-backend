import React, { useState } from 'react';
import { useDashboardData } from '../hooks/useDashboardData';
import { BarChart3, Users, FileCheck, Clock, MoreHorizontal } from 'lucide-react';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell, Legend
} from 'recharts';
import { Box, Grid, Paper, Typography, CircularProgress, Select, MenuItem, Button, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Chip, LinearProgress, IconButton } from '@mui/material';
import KpiCard from '../components/KpiCard';
import styled, { keyframes } from 'styled-components';

// Animation pour l'apparition des éléments
const fadeIn = keyframes`
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
`;

// Utilisation de `styled` pour surcharger les styles de Paper de MUI
const AnimatedPaper = styled(Paper)`
  padding: 2rem;
  border-radius: 1rem;
  box-shadow: 0 10px 15px -3px rgba(0,0,0,0.07), 0 4px 6px -2px rgba(0,0,0,0.05);
  transition: transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out;
  animation: ${fadeIn} 0.5s ease-out forwards;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1), 0 10px 10px -5px rgba(0,0,0,0.04);
  }
`;

const Dashboard = () => {
  const [period, setPeriod] = useState('month');
  const { stats, chartData, recentProjects, loading } = useDashboardData(period);

  const pieData = [
    { name: 'Validées', value: 65, color: '#3b82f6' },
    { name: 'En cours', value: 25, color: '#10b981' },
    { name: 'Rejetées', value: 10, color: '#f59e0b' },
  ];

  const statusColors = {
    'Terminé': 'success',
    'En cours': 'info',
    'En attente': 'warning',
    'Annulé': 'error',
    'Planifié': 'primary',
  };

  return (
    <Box sx={{ p: 3, backgroundColor: '#f9fafb', flexGrow: 1 }}>
      <Grid container spacing={4}>
        {/* Header */}
        <Grid item xs={12}>
          <Box display="flex" justifyContent="space-between" alignItems="center">
            <div>
              <Typography variant="h4" component="h1" fontWeight="bold" color="text.primary">
                Tableau de bord
              </Typography>
              <Typography variant="body1" color="text.secondary">
                Aperçu des performances pour : <span style={{ fontWeight: 'bold' }}>{period === 'week' ? 'Cette semaine' : period === 'month' ? 'Ce mois' : 'Cette année'}</span>
              </Typography>
            </div>
            <Box>
              <Select
                value={period}
                onChange={(e) => setPeriod(e.target.value)}
                size="small"
                sx={{ mr: 2, bgcolor: 'white' }}
              >
                <MenuItem value="week">Cette semaine</MenuItem>
                <MenuItem value="month">Ce mois</MenuItem>
                <MenuItem value="year">Cette année</MenuItem>
              </Select>
              <Button variant="contained" color="primary">Exporter</Button>
            </Box>
          </Box>
        </Grid>

        {/* KPI Cards */}
        <Grid item xs={12}>
          <Grid container spacing={3}>
            <Grid item xs={12} sm={6} md={3}>
              <KpiCard title="Total Collectes" value={loading ? <CircularProgress size={24} /> : stats.totalCollectes.toLocaleString()} icon={BarChart3} color="bg-blue-600" trend="+12.5%" trendUp />
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <KpiCard title="Projets Actifs" value={loading ? <CircularProgress size={24} /> : stats.projetsActifs} icon={Clock} color="bg-emerald-500" trend="+2" trendUp />
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <KpiCard title="Taux de Validation" value={loading ? <CircularProgress size={24} /> : `${stats.validationRate}%`} icon={FileCheck} color="bg-violet-500" trend="-1.4%" />
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <KpiCard title="Utilisateurs Actifs" value={loading ? <CircularProgress size={24} /> : stats.usersActifs} icon={Users} color="bg-amber-500" trend="+5" trendUp />
            </Grid>
          </Grid>
        </Grid>

        {/* Projects Table - **NOUVELLE POSITION** */}
        <Grid item xs={12}>
          <TableContainer component={Paper} sx={{ borderRadius: '1rem', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)', border: '1px solid #e5e7eb' }}>
            <Box p={3} display="flex" justifyContent="space-between" alignItems="center" borderBottom="1px solid #e5e7eb">
              <Typography variant="h6" component="h2" fontWeight="600">
                Derniers Projets
              </Typography>
              <Button size="small" sx={{ textTransform: 'none' }}>Voir tout</Button>
            </Box>
            {loading ? (
              <Box p={5} textAlign="center">
                <CircularProgress />
              </Box>
            ) : (
              <Table aria-label="derniers projets">
                <TableHead>
                  <TableRow sx={{ bgcolor: '#f9fafb' }}>
                    <TableCell sx={{ fontWeight: '600', color: 'text.secondary' }}>Nom du Projet</TableCell>
                    <TableCell sx={{ fontWeight: '600', color: 'text.secondary' }}>Chef de Projet</TableCell>
                    <TableCell sx={{ fontWeight: '600', color: 'text.secondary' }}>Statut</TableCell>
                    <TableCell sx={{ fontWeight: '600', color: 'text.secondary' }}>Progression</TableCell>
                    <TableCell align="right" sx={{ fontWeight: '600', color: 'text.secondary' }}>Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {recentProjects.map((project) => (
                    <TableRow key={project.id} hover sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                      <TableCell component="th" scope="row">
                        <Typography variant="body2" fontWeight="medium" color="text.primary">
                          {project.name}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Box display="flex" alignItems="center" gap={1}>
                          <Box
                            sx={{
                              width: 24, height: 24, borderRadius: '50%', bgcolor: '#e5e7eb',
                              display: 'flex', alignItems: 'center', justifyContent: 'center',
                              fontSize: '0.75rem', fontWeight: 'bold', color: '#4b5563'
                            }}
                          >
                            {project.lead.charAt(0)}
                          </Box>
                          <Typography variant="body2">{project.lead}</Typography>
                        </Box>
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={project.status}
                          color={statusColors[project.status] || 'default'}
                          size="small"
                          variant="outlined"
                          sx={{ fontWeight: 'medium' }}
                        />
                      </TableCell>
                      <TableCell sx={{ width: '20%' }}>
                        <Box display="flex" alignItems="center">
                          <Box width="100%" mr={1}>
                            <LinearProgress
                              variant="determinate"
                              value={project.progress}
                              color={statusColors[project.status] || 'primary'}
                              sx={{ height: 6, borderRadius: 3 }}
                            />
                          </Box>
                          <Typography variant="caption" color="text.secondary">{`${project.progress}%`}</Typography>
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
            )}
          </TableContainer>
        </Grid>

        {/* Bar Chart */}
        <Grid item xs={12} lg={8}>
          <AnimatedPaper>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
              <Typography variant="h6" component="h2" fontWeight="600">
                Volume des collectes
              </Typography>
              <IconButton size="small">
                <MoreHorizontal size={20} />
              </IconButton>
            </Box>
            <Box sx={{ height: 350 }}>
              {loading ? (
                <Box display="flex" justifyContent="center" alignItems="center" height="100%">
                  <CircularProgress />
                </Box>
              ) : (
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={chartData} margin={{ top: 5, right: 20, left: -10, bottom: 5 }}>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e5e7eb" />
                    <XAxis dataKey="name" tickLine={false} axisLine={false} tick={{ fill: '#6b7280', fontSize: 12 }} dy={10} />
                    <YAxis tickLine={false} axisLine={false} tick={{ fill: '#6b7280', fontSize: 12 }} />
                    <Tooltip
                      cursor={{ fill: 'rgba(59, 130, 246, 0.1)' }}
                      contentStyle={{ borderRadius: '0.75rem', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)' }}
                    />
                    <Bar dataKey="value" fill="#3b82f6" radius={[4, 4, 0, 0]} barSize={30} animationDuration={1500} />
                  </BarChart>
                </ResponsiveContainer>
              )}
            </Box>
          </AnimatedPaper>
        </Grid>

        {/* Pie Chart */}
        <Grid item xs={12} lg={4}>
          <AnimatedPaper sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
            <Typography variant="h6" component="h2" fontWeight="600" mb={2}>
              Répartition par Statut
            </Typography>
            <Box sx={{ flexGrow: 1, minHeight: 300, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
              {loading ? (
                <CircularProgress />
              ) : (
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={pieData}
                      cx="50%"
                      cy="50%"
                      innerRadius={80}
                      outerRadius={100}
                      paddingAngle={5}
                      dataKey="value"
                    >
                      {pieData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.color} strokeWidth={0} />
                      ))}
                    </Pie>
                    <Tooltip />
                    <Legend verticalAlign="bottom" height={36} iconType="circle" />
                  </PieChart>
                </ResponsiveContainer>
              )}
            </Box>
            <Box textAlign="center" mt={-4}>
               <Typography variant="body2" color="text.secondary">Total Collectes</Typography>
               <Typography variant="h4" fontWeight="bold">{loading ? '...' : stats.totalCollectes.toLocaleString()}</Typography>
            </Box>
          </AnimatedPaper>
        </Grid>

      </Grid>
    </Box>
  );
};

export default Dashboard;

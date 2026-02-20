import React from 'react';
import { ArrowUpRight, ArrowDownRight } from 'lucide-react';
import { Card, CardContent, Typography, Box, Avatar } from '@mui/material';
import styled from 'styled-components';

// Styled Card pour ajouter un effet de survol
const StyledCard = styled(Card)`
  transition: transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out;
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  }
`;

const KpiCard = ({ title, value, icon: Icon, color, trend, trendUp }) => {
  // Mapping des couleurs Tailwind vers les couleurs Material UI ou des codes hexadécimaux
  const colorMap = {
    'bg-blue-600': { main: '#2563eb', light: '#e0f2fe' }, // blue-600
    'bg-emerald-500': { main: '#10b981', light: '#e6fffa' }, // emerald-500
    'bg-violet-500': { main: '#8b5cf6', light: '#f3e8ff' }, // violet-500
    'bg-amber-500': { main: '#f59e0b', light: '#fffbeb' }, // amber-500
  };

  const selectedColor = colorMap[color] || { main: '#6b7280', light: '#f3f4f6' }; // Default to gray

  return (
    <StyledCard sx={{ borderRadius: '1rem', border: '1px solid #e5e7eb' }}>
      <CardContent>
        <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
          <Avatar sx={{
            bgcolor: selectedColor.light,
            color: selectedColor.main,
            width: 48,
            height: 48,
            transition: 'transform 0.3s ease-in-out',
            '&:hover': {
              transform: 'scale(1.1)',
            }
          }}>
            {Icon && <Icon size={24} />}
          </Avatar>
          {trend && (
            <Box
              sx={{
                display: 'flex',
                alignItems: 'center',
                gap: 0.5,
                fontSize: '0.75rem', // text-xs
                fontWeight: 'medium',
                px: 1,
                py: 0.5,
                borderRadius: '9999px', // rounded-full
                bgcolor: trendUp ? '#dcfce7' : '#fee2e2', // bg-green-50 / bg-red-50
                color: trendUp ? '#16a34a' : '#dc2626', // text-green-700 / text-red-700
              }}
            >
              {trendUp ? <ArrowUpRight size={14} /> : <ArrowDownRight size={14} />}
              {trend}
            </Box>
          )}
        </Box>

        <Typography variant="h4" component="div" fontWeight="bold" sx={{ mt: 1, color: 'text.primary' }}>
          {value}
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
          {title}
        </Typography>
      </CardContent>
    </StyledCard>
  );
};

export default KpiCard;

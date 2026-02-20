import React, { useState } from 'react';
import { useDashboardData } from '../hooks/useDashboardData';
import { BarChart3, Users, FileCheck, Clock, MoreHorizontal, TrendingUp } from 'lucide-react';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, Legend,
} from 'recharts';
import KpiCard from '../components/KpiCard';

/* ── Helpers ─────────────────────────────────────────────── */
const STATUS_CHIP = {
  'Terminé':  { bg: '#d1fae5', color: '#065f46' },
  'En cours': { bg: '#dbeafe', color: '#1d4ed8' },
  'En attente':{ bg: '#fef3c7', color: '#92400e' },
  'Annulé':   { bg: '#fee2e2', color: '#991b1b' },
  'Planifié': { bg: '#ede9fe', color: '#5b21b6' },
};

const PIE_DATA = [
  { name: 'Validées', value: 65, color: '#2563eb' },
  { name: 'En cours', value: 25, color: '#059669' },
  { name: 'Rejetées', value: 10, color: '#d97706' },
];

const CustomTooltip = ({ active, payload, label }) => {
  if (!active || !payload?.length) return null;
  return (
    <div style={{
      background: '#fff', border: '1px solid var(--c-border)',
      borderRadius: 10, padding: '10px 14px',
      boxShadow: 'var(--shadow)',
      fontSize: '0.82rem', fontFamily: 'var(--font-sans)',
    }}>
      <p style={{ margin: 0, color: 'var(--c-ink-2)', fontWeight: 600, marginBottom: 4 }}>{label}</p>
      <p style={{ margin: 0, color: 'var(--c-accent)', fontWeight: 700 }}>
        {payload[0].value} collectes
      </p>
    </div>
  );
};

/* ── Skeleton ────────────────────────────────────────────── */
const Skeleton = ({ h = 20, w = '100%', r = 6 }) => (
  <div className="skeleton" style={{ height: h, width: w, borderRadius: r }} />
);

/* ── Period selector ─────────────────────────────────────── */
const PeriodSelector = ({ value, onChange }) => {
  const opts = [
    { v: 'week',  l: 'Semaine' },
    { v: 'month', l: 'Mois' },
    { v: 'year',  l: 'Année' },
  ];
  return (
    <div style={{
      display: 'flex', gap: 2, background: '#f4f3f0',
      border: '1px solid var(--c-border)',
      borderRadius: 10, padding: 3,
    }}>
      {opts.map(o => (
        <button
          key={o.v}
          onClick={() => onChange(o.v)}
          style={{
            padding: '6px 14px', border: 'none', borderRadius: 8,
            fontSize: '0.82rem', fontWeight: 600, cursor: 'pointer',
            fontFamily: 'var(--font-sans)',
            transition: 'background 0.18s, color 0.18s, box-shadow 0.18s',
            background: value === o.v ? '#fff' : 'transparent',
            color: value === o.v ? 'var(--c-accent)' : 'var(--c-ink-2)',
            boxShadow: value === o.v ? 'var(--shadow-xs)' : 'none',
          }}
        >
          {o.l}
        </button>
      ))}
    </div>
  );
};

/* ── Dashboard ───────────────────────────────────────────── */
const Dashboard = () => {
  const [period, setPeriod] = useState('month');
  const { stats, chartData, recentProjects, loading } = useDashboardData(period);

  return (
    <div style={{ padding: '28px 0' }}>

      {/* ── Header ── */}
      <div
        className="animate-fade-up"
        style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 28, flexWrap: 'wrap', gap: 12 }}
      >
        <div>
          <h1 style={{ margin: 0, fontSize: '1.5rem', fontWeight: 700, color: 'var(--c-ink)', letterSpacing: '-0.02em' }}>
            Tableau de bord
          </h1>
          <p style={{ margin: '4px 0 0', fontSize: '0.855rem', color: 'var(--c-ink-2)' }}>
            Vue d'ensemble — {new Date().toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })}
          </p>
        </div>
        <div style={{ display: 'flex', gap: 10, alignItems: 'center' }}>
          <PeriodSelector value={period} onChange={setPeriod} />
          <button className="btn-primary">
            <TrendingUp size={15} />
            Exporter
          </button>
        </div>
      </div>

      {/* ── KPI Cards ── */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: 16, marginBottom: 24 }}>
        <KpiCard title="Total Collectes"    value={loading ? '—' : stats.totalCollectes.toLocaleString('fr-FR')} icon={BarChart3}  color="blue"   trend="+12.5%" trendUp delay={0}   />
        <KpiCard title="Projets Actifs"      value={loading ? '—' : stats.projetsActifs}                          icon={Clock}      color="green"  trend="+2"     trendUp delay={80}  />
        <KpiCard title="Taux de Validation"  value={loading ? '—' : `${stats.validationRate}%`}                  icon={FileCheck}  color="violet" trend="-1.4%"          delay={160} />
        <KpiCard title="Utilisateurs Actifs" value={loading ? '—' : stats.usersActifs}                           icon={Users}      color="amber"  trend="+5"     trendUp delay={240} />
      </div>

      {/* ── Charts row ── */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 340px', gap: 16, marginBottom: 24 }} className="chart-grid">
        {/* Bar chart */}
        <div className="card animate-fade-up delay-200" style={{ padding: 24 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
            <h2 style={{ margin: 0, fontSize: '0.95rem', fontWeight: 700, color: 'var(--c-ink)' }}>
              Volume des collectes
            </h2>
            <button style={{
              width: 30, height: 30, border: '1px solid var(--c-border)',
              borderRadius: 7, background: 'transparent', cursor: 'pointer',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              color: 'var(--c-ink-2)',
            }}>
              <MoreHorizontal size={15} />
            </button>
          </div>
          <div style={{ height: 260 }}>
            {loading
              ? <div style={{ display: 'flex', alignItems: 'flex-end', gap: 6, height: '100%', padding: '10px 0' }}>
                  {Array.from({ length: 8 }).map((_, i) => (
                    <Skeleton key={i} h={`${40 + Math.random() * 60}%`} w="100%" r={4} />
                  ))}
                </div>
              : (
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={chartData} margin={{ top: 4, right: 4, left: -16, bottom: 0 }}>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--c-border)" />
                    <XAxis
                      dataKey="name"
                      tickLine={false} axisLine={false}
                      tick={{ fill: 'var(--c-ink-2)', fontSize: 11, fontFamily: 'var(--font-sans)' }}
                      dy={6}
                    />
                    <YAxis
                      tickLine={false} axisLine={false}
                      tick={{ fill: 'var(--c-ink-2)', fontSize: 11, fontFamily: 'var(--font-sans)' }}
                    />
                    <Tooltip content={<CustomTooltip />} cursor={{ fill: '#f0f4ff', radius: 4 }} />
                    <Bar dataKey="value" fill="var(--c-accent)" radius={[5, 5, 0, 0]} maxBarSize={36} animationDuration={900} />
                  </BarChart>
                </ResponsiveContainer>
              )
            }
          </div>
        </div>

        {/* Pie chart */}
        <div className="card animate-fade-up delay-250" style={{ padding: 24, display: 'flex', flexDirection: 'column' }}>
          <h2 style={{ margin: '0 0 4px', fontSize: '0.95rem', fontWeight: 700, color: 'var(--c-ink)' }}>
            Répartition
          </h2>
          <p style={{ margin: '0 0 16px', fontSize: '0.78rem', color: 'var(--c-ink-2)' }}>Par statut</p>
          <div style={{ flex: 1, minHeight: 200 }}>
            {loading
              ? <div className="skeleton" style={{ height: '100%', borderRadius: '50%', width: 160, margin: 'auto' }} />
              : (
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie data={PIE_DATA} cx="50%" cy="45%" innerRadius={60} outerRadius={80} paddingAngle={4} dataKey="value" animationDuration={900}>
                      {PIE_DATA.map((e, i) => <Cell key={i} fill={e.color} strokeWidth={0} />)}
                    </Pie>
                    <Tooltip formatter={(v) => [`${v}%`]} />
                    <Legend iconType="circle" iconSize={8} wrapperStyle={{ fontSize: '0.78rem', fontFamily: 'var(--font-sans)' }} />
                  </PieChart>
                </ResponsiveContainer>
              )
            }
          </div>
          <div style={{ textAlign: 'center', paddingTop: 8 }}>
            <span style={{ fontSize: '0.72rem', color: 'var(--c-ink-2)', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.06em' }}>
              Total
            </span>
            <p style={{ margin: '2px 0 0', fontSize: '1.5rem', fontWeight: 700, color: 'var(--c-ink)', fontVariantNumeric: 'tabular-nums' }}>
              {loading ? '—' : stats.totalCollectes.toLocaleString('fr-FR')}
            </p>
          </div>
        </div>
      </div>

      {/* ── Projects table ── */}
      <div className="card animate-fade-up delay-300" style={{ overflow: 'hidden' }}>
        <div style={{
          display: 'flex', justifyContent: 'space-between', alignItems: 'center',
          padding: '18px 24px',
          borderBottom: '1px solid var(--c-border)',
        }}>
          <h2 style={{ margin: 0, fontSize: '0.95rem', fontWeight: 700, color: 'var(--c-ink)' }}>
            Derniers Projets
          </h2>
          <button style={{
            fontSize: '0.82rem', fontWeight: 600, color: 'var(--c-accent)',
            border: 'none', background: 'transparent', cursor: 'pointer',
            fontFamily: 'var(--font-sans)', padding: '5px 10px', borderRadius: 6,
          }}>
            Voir tout →
          </button>
        </div>

        {loading ? (
          <div style={{ padding: 24, display: 'flex', flexDirection: 'column', gap: 16 }}>
            {[1,2,3].map(i => <Skeleton key={i} h={32} />)}
          </div>
        ) : (
          <div style={{ overflowX: 'auto' }}>
            <table className="data-table">
              <thead>
                <tr>
                  <th>Projet</th>
                  <th>Chef de projet</th>
                  <th>Statut</th>
                  <th style={{ width: '22%' }}>Progression</th>
                  <th style={{ textAlign: 'right' }}></th>
                </tr>
              </thead>
              <tbody>
                {recentProjects.map(p => {
                  const chip = STATUS_CHIP[p.status] ?? { bg: '#f3f4f6', color: '#374151' };
                  return (
                    <tr key={p.id}>
                      <td>
                        <span style={{ fontWeight: 600, color: 'var(--c-ink)', fontSize: '0.875rem' }}>
                          {p.name}
                        </span>
                      </td>
                      <td>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                          <div style={{
                            width: 26, height: 26, borderRadius: 7,
                            background: '#f0f4ff', color: 'var(--c-accent)',
                            fontWeight: 700, fontSize: '0.72rem',
                            display: 'flex', alignItems: 'center', justifyContent: 'center',
                            flexShrink: 0,
                          }}>
                            {p.lead.charAt(0)}
                          </div>
                          <span style={{ fontSize: '0.855rem', color: 'var(--c-ink-2)' }}>{p.lead}</span>
                        </div>
                      </td>
                      <td>
                        <span style={{
                          display: 'inline-block', padding: '3px 10px', borderRadius: 99,
                          fontSize: '0.72rem', fontWeight: 700,
                          background: chip.bg, color: chip.color,
                          letterSpacing: '0.02em',
                        }}>
                          {p.status}
                        </span>
                      </td>
                      <td>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                          <div className="progress-bar-track" style={{ flex: 1 }}>
                            <div
                              className="progress-bar-fill"
                              style={{ width: `${p.progress}%`, background: chip.color }}
                            />
                          </div>
                          <span style={{ fontSize: '0.72rem', fontWeight: 600, color: 'var(--c-ink-2)', minWidth: 32, fontFamily: 'var(--font-mono)' }}>
                            {p.progress}%
                          </span>
                        </div>
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        <button style={{
                          width: 28, height: 28, border: '1px solid var(--c-border)',
                          borderRadius: 6, background: 'transparent', cursor: 'pointer',
                          display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
                          color: 'var(--c-ink-2)',
                        }}>
                          <MoreHorizontal size={14} />
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Responsive chart grid */}
      <style>{`
        @media (max-width: 900px) {
          .chart-grid { grid-template-columns: 1fr !important; }
        }
      `}</style>
    </div>
  );
};

export default Dashboard;

import React from 'react';
import KpiCard from '../components/KpiCard';
import { BarChart3, Users, FileCheck, Clock, MoreHorizontal } from 'lucide-react';
import { useDashboardData } from '../hooks/useDashboardData';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  Legend
} from 'recharts';

const Dashboard = () => {
  const { stats, loading } = useDashboardData();

  if (loading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  // Préparation des données pour le BarChart
  const barData = [
    { name: 'Jan', value: 35 }, { name: 'Fév', value: 55 }, { name: 'Mar', value: 40 },
    { name: 'Avr', value: 70 }, { name: 'Mai', value: 50 }, { name: 'Juin', value: 90 },
    { name: 'Juil', value: 65 }, { name: 'Août', value: 85 }, { name: 'Sep', value: 45 },
    { name: 'Oct', value: 60 }, { name: 'Nov', value: 75 }, { name: 'Déc', value: 50 },
  ];

  // Préparation des données pour le PieChart
  const pieData = [
    { name: 'Validées', value: 65, color: '#2563eb' }, // blue-600
    { name: 'En cours', value: 25, color: '#10b981' }, // emerald-500
    { name: 'Rejetées', value: 10, color: '#fbbf24' }, // amber-400
  ];

  return (
    <div className="space-y-8">
      {/* Header Section */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Tableau de bord</h1>
          <p className="text-sm text-gray-500 mt-1">Bienvenue sur votre espace de gestion ElCollecte.</p>
        </div>
        <div className="flex gap-2">
          <select className="bg-white border border-gray-300 text-gray-700 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block p-2.5 shadow-sm">
            <option>Cette semaine</option>
            <option>Ce mois</option>
            <option>Cette année</option>
          </select>
          <button className="bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium px-4 py-2.5 rounded-lg shadow-sm transition-colors">
            Exporter le rapport
          </button>
        </div>
      </div>

      {/* KPIs */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <KpiCard
          title="Total Collectes"
          value={stats.totalCollectes.toLocaleString()}
          icon={BarChart3}
          color="bg-blue-600"
          trend="+12.5%"
          trendUp={true}
        />
        <KpiCard
          title="Projets Actifs"
          value={stats.projetsActifs}
          icon={Clock}
          color="bg-emerald-500"
          trend="+2"
          trendUp={true}
        />
        <KpiCard
          title="Taux de Validation"
          value={`${stats.validationRate}%`}
          icon={FileCheck}
          color="bg-violet-500"
          trend="-1.4%"
          trendUp={false}
        />
        <KpiCard
          title="Utilisateurs Actifs"
          value={stats.usersActifs}
          icon={Users}
          color="bg-amber-500"
          trend="+5"
          trendUp={true}
        />
      </div>

      {/* Charts & Tables */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Main Chart Area (BarChart) */}
        <div className="lg:col-span-2 bg-white p-6 rounded-xl shadow-sm border border-gray-200">
          <div className="flex justify-between items-center mb-6">
            <h3 className="text-lg font-semibold text-gray-900">Volume des collectes</h3>
            <button className="text-gray-400 hover:text-gray-600"><MoreHorizontal size={20} /></button>
          </div>

          <div className="h-80 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={barData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f3f4f6" />
                <XAxis
                  dataKey="name"
                  axisLine={false}
                  tickLine={false}
                  tick={{ fill: '#9ca3af', fontSize: 12 }}
                  dy={10}
                />
                <YAxis
                  axisLine={false}
                  tickLine={false}
                  tick={{ fill: '#9ca3af', fontSize: 12 }}
                />
                <Tooltip
                  cursor={{ fill: '#f9fafb' }}
                  contentStyle={{ borderRadius: '8px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)' }}
                />
                <Bar
                  dataKey="value"
                  fill="#2563eb"
                  radius={[4, 4, 0, 0]}
                  barSize={30}
                  animationDuration={1500}
                />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Recent Activity / Stats (PieChart) */}
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-200 flex flex-col">
          <h3 className="text-lg font-semibold text-gray-900 mb-2">Répartition par Statut</h3>

          <div className="flex-1 flex items-center justify-center h-64">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={pieData}
                  cx="50%"
                  cy="50%"
                  innerRadius={60}
                  outerRadius={80}
                  paddingAngle={5}
                  dataKey="value"
                >
                  {pieData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} strokeWidth={0} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend
                  verticalAlign="bottom"
                  height={36}
                  iconType="circle"
                  formatter={(value, entry) => <span className="text-sm text-gray-600 ml-1">{value}</span>}
                />
              </PieChart>
            </ResponsiveContainer>
          </div>

          {/* Total Center Overlay (Optional, hard to center perfectly with responsive pie, so kept simple) */}
          <div className="text-center -mt-4">
             <p className="text-sm text-gray-500">Total Collectes</p>
             <p className="text-2xl font-bold text-gray-900">{stats.totalCollectes.toLocaleString()}</p>
          </div>
        </div>
      </div>

      {/* Data Table */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
        <div className="p-6 border-b border-gray-200 flex justify-between items-center">
          <h3 className="text-lg font-semibold text-gray-900">Derniers Projets</h3>
          <button className="text-blue-600 text-sm font-medium hover:text-blue-800">Voir tout</button>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm text-gray-600">
            <thead className="bg-gray-50 text-xs uppercase font-semibold text-gray-500">
              <tr>
                <th className="px-6 py-4">Nom du Projet</th>
                <th className="px-6 py-4">Chef de Projet</th>
                <th className="px-6 py-4">Statut</th>
                <th className="px-6 py-4">Progression</th>
                <th className="px-6 py-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {stats.recentProjects.map((project, i) => (
                <tr key={i} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 font-medium text-gray-900">{project.name}</td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-2">
                      <div className="w-6 h-6 rounded-full bg-gray-200 flex items-center justify-center text-xs font-bold text-gray-600">
                        {project.lead.charAt(0)}
                      </div>
                      {project.lead}
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-${project.color}-100 text-${project.color}-800`}>
                      {project.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 w-48">
                    <div className="flex items-center gap-2">
                      <div className="w-full bg-gray-200 rounded-full h-2">
                        <div className={`bg-${project.color}-600 h-2 rounded-full`} style={{ width: `${project.progress}%` }}></div>
                      </div>
                      <span className="text-xs font-medium text-gray-500">{project.progress}%</span>
                    </div>
                  </td>
                  <td className="px-6 py-4 text-right">
                    <button className="text-gray-400 hover:text-blue-600 transition-colors">
                      <MoreHorizontal size={18} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
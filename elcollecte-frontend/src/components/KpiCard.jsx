import React from 'react';
import { ArrowUpRight, ArrowDownRight } from 'lucide-react';

const KpiCard = ({ title, value, icon: Icon, color, trend, trendUp }) => {
  // Extraction de la couleur de base (ex: "bg-blue-600" -> "blue")
  const colorBase = color ? color.split('-')[1] : 'gray';

  return (
    <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-200 hover:shadow-md transition-all duration-200 group">
      <div className="flex justify-between items-start mb-4">
        <div className={`p-3 rounded-lg ${color} bg-opacity-10 text-${colorBase}-600 group-hover:scale-110 transition-transform`}>
          {Icon && <Icon size={24} />}
        </div>
        {trend && (
          <div className={`flex items-center gap-1 text-xs font-medium px-2 py-1 rounded-full ${trendUp ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'}`}>
            {trendUp ? <ArrowUpRight size={14} /> : <ArrowDownRight size={14} />}
            {trend}
          </div>
        )}
      </div>

      <div>
        <h3 className="text-2xl font-bold text-gray-900 tracking-tight">{value}</h3>
        <p className="text-sm font-medium text-gray-500 mt-1">{title}</p>
      </div>
    </div>
  );
};

export default KpiCard;
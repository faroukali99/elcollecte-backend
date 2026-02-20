import React from 'react';
import { TrendingUp, TrendingDown } from 'lucide-react';

/**
 * KpiCard — redesigned
 * Props:
 *   title    : string
 *   value    : string | number
 *   icon     : LucideIcon component
 *   color    : 'blue' | 'green' | 'violet' | 'amber'
 *   trend    : string  (e.g. "+12.5%")
 *   trendUp  : boolean
 *   delay    : number  (ms, animation stagger)
 */

const PALETTE = {
  blue:   { bg: '#dbeafe', text: '#1d4ed8', accent: '#2563eb' },
  green:  { bg: '#d1fae5', text: '#065f46', accent: '#059669' },
  violet: { bg: '#ede9fe', text: '#5b21b6', accent: '#7c3aed' },
  amber:  { bg: '#fef3c7', text: '#92400e', accent: '#d97706' },
};

// Map legacy Tailwind class names → palette key
const LEGACY_MAP = {
  'bg-blue-600':   'blue',
  'bg-emerald-500':'green',
  'bg-violet-500': 'violet',
  'bg-amber-500':  'amber',
};

const KpiCard = ({ title, value, icon: Icon, color = 'blue', trend, trendUp, delay = 0 }) => {
  const key = LEGACY_MAP[color] ?? color;
  const palette = PALETTE[key] ?? PALETTE.blue;

  return (
    <div
      className="animate-fade-up"
      style={{ animationDelay: `${delay}ms` }}
    >
      <div className="card" style={{ padding: '22px 24px' }}>
        {/* Top row */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 16 }}>
          {/* Icon bubble */}
          <div style={{
            width: 44, height: 44, borderRadius: 12,
            background: palette.bg,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            color: palette.accent,
            flexShrink: 0,
          }}>
            {Icon && <Icon size={20} strokeWidth={2} />}
          </div>

          {/* Trend badge */}
          {trend && (
            <span style={{
              display: 'inline-flex', alignItems: 'center', gap: 3,
              padding: '3px 8px', borderRadius: 99,
              fontSize: '0.72rem', fontWeight: 700,
              background: trendUp ? '#d1fae5' : '#fee2e2',
              color: trendUp ? '#065f46' : '#991b1b',
            }}>
              {trendUp
                ? <TrendingUp size={11} />
                : <TrendingDown size={11} />
              }
              {trend}
            </span>
          )}
        </div>

        {/* Value */}
        <div
          className="animate-count"
          style={{
            animationDelay: `${delay + 80}ms`,
            fontSize: '1.75rem', fontWeight: 700,
            color: 'var(--c-ink)', lineHeight: 1,
            marginBottom: 6,
            fontVariantNumeric: 'tabular-nums',
          }}
        >
          {value}
        </div>

        {/* Title */}
        <div style={{ fontSize: '0.8rem', color: 'var(--c-ink-2)', fontWeight: 500 }}>
          {title}
        </div>

        {/* Accent stripe */}
        <div style={{
          height: 3, borderRadius: 99,
          background: palette.accent,
          opacity: 0.25,
          marginTop: 16,
        }} />
      </div>
    </div>
  );
};

export default KpiCard;

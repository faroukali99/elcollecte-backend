import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import {
  LayoutDashboard,
  FolderKanban,
  ClipboardList,
  CheckCircle2,
  LogOut,
  Settings,
  PieChart,
  BarChart3,
} from 'lucide-react';
import { logout } from '../features/auth/authSlice';

const NAV = [
  { name: 'Tableau de bord', href: '/',           icon: LayoutDashboard },
  { name: 'Mes Projets',     href: '/projets',     icon: FolderKanban    },
  { name: 'Collectes',       href: '/collecte',    icon: ClipboardList   },
  { name: 'Validation',      href: '/validation',  icon: CheckCircle2    },
  { name: 'Analytique',      href: '/analytics',   icon: PieChart        },
  { name: 'Rapport',         href: 'rapport', label: 'Rapport', icon: BarChart3, path: '/rapport' },
];

const SYSTEM = [
  { name: 'Paramètres', href: '/settings', icon: Settings },
];

const Sidebar = ({ isOpen, closeSidebar }) => {
  const location  = useLocation();
  const navigate  = useNavigate();
  const dispatch  = useDispatch();

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  const NavLink = ({ item }) => {
    const active = location.pathname === item.href;
    const Icon   = item.icon;
    return (
      <Link
        to={item.href}
        onClick={closeSidebar}
        style={{ position: 'relative', textDecoration: 'none' }}
        className={active ? 'nav-item-active' : ''}
      >
        <div style={{
          display: 'flex', alignItems: 'center', gap: 10,
          padding: '10px 14px',
          borderRadius: 8,
          margin: '1px 0',
          fontSize: '0.875rem', fontWeight: 500,
          transition: 'background 0.18s, color 0.18s',
          background: active ? 'rgba(37,99,235,0.15)' : 'transparent',
          color: active ? '#93c5fd' : 'rgba(255,255,255,0.55)',
          cursor: 'pointer',
        }}
          onMouseEnter={e => {
            if (!active) {
              e.currentTarget.style.background = 'rgba(255,255,255,0.07)';
              e.currentTarget.style.color = 'rgba(255,255,255,0.85)';
            }
          }}
          onMouseLeave={e => {
            if (!active) {
              e.currentTarget.style.background = 'transparent';
              e.currentTarget.style.color = 'rgba(255,255,255,0.55)';
            }
          }}
        >
          <Icon size={16} strokeWidth={active ? 2.2 : 1.8} />
          <span>{item.name}</span>
          {active && (
            <div style={{
              position: 'absolute', right: 14, top: '50%', transform: 'translateY(-50%)',
              width: 6, height: 6, borderRadius: '50%',
              background: '#3b82f6',
              boxShadow: '0 0 6px #3b82f6',
            }} />
          )}
        </div>
      </Link>
    );
  };

  return (
    <>
      {/* Mobile overlay */}
      {isOpen && (
        <div
          onClick={closeSidebar}
          style={{
            position: 'fixed', inset: 0, zIndex: 20,
            background: 'rgba(0,0,0,0.45)',
            backdropFilter: 'blur(4px)',
            transition: 'opacity 0.3s',
          }}
        />
      )}

      <aside style={{
        position: 'fixed',
        inset: '0 auto 0 0',
        zIndex: 30,
        width: 240,
        background: '#0f1117',
        display: 'flex',
        flexDirection: 'column',
        transform: isOpen ? 'translateX(0)' : undefined,
        transition: 'transform 0.3s cubic-bezier(0.22,1,0.36,1)',
        borderRight: '1px solid rgba(255,255,255,0.06)',
      }}
        className={`${!isOpen ? '-translate-x-full lg:translate-x-0' : ''}`}
      >
        {/* Logo */}
        <div style={{
          height: 64, display: 'flex', alignItems: 'center',
          padding: '0 20px', gap: 10,
          borderBottom: '1px solid rgba(255,255,255,0.06)',
        }}>
          <div style={{
            width: 32, height: 32, borderRadius: 8,
            background: 'linear-gradient(135deg, #2563eb, #7c3aed)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            boxShadow: '0 4px 12px rgba(37,99,235,0.4)',
          }}>
            <ClipboardList size={16} color="#fff" strokeWidth={2.5} />
          </div>
          <span style={{
            fontWeight: 700, fontSize: '1rem',
            color: '#fff', letterSpacing: '-0.02em',
          }}>
            ElCollecte
          </span>
        </div>

        {/* Nav */}
        <nav style={{ flex: 1, padding: '16px 10px', overflowY: 'auto' }}>
          <div style={{ marginBottom: 8 }}>
            <p style={{
              fontSize: '0.65rem', fontWeight: 700, letterSpacing: '0.1em',
              textTransform: 'uppercase', color: 'rgba(255,255,255,0.25)',
              padding: '0 14px', marginBottom: 4,
            }}>
              Navigation
            </p>
            {NAV.map(item => <NavLink key={item.href} item={item} />)}
          </div>

          <div style={{ marginTop: 24 }}>
            <p style={{
              fontSize: '0.65rem', fontWeight: 700, letterSpacing: '0.1em',
              textTransform: 'uppercase', color: 'rgba(255,255,255,0.25)',
              padding: '0 14px', marginBottom: 4,
            }}>
              Système
            </p>
            {SYSTEM.map(item => <NavLink key={item.href} item={item} />)}
          </div>
        </nav>

        {/* Logout */}
        <div style={{
          padding: '12px 10px',
          borderTop: '1px solid rgba(255,255,255,0.06)',
        }}>
          <button
            onClick={handleLogout}
            style={{
              width: '100%', display: 'flex', alignItems: 'center', gap: 10,
              padding: '10px 14px', border: 'none', borderRadius: 8,
              background: 'transparent', cursor: 'pointer',
              fontSize: '0.875rem', fontWeight: 500,
              color: 'rgba(248,113,113,0.7)',
              transition: 'background 0.18s, color 0.18s',
              fontFamily: 'inherit',
            }}
            onMouseEnter={e => {
              e.currentTarget.style.background = 'rgba(239,68,68,0.1)';
              e.currentTarget.style.color = '#f87171';
            }}
            onMouseLeave={e => {
              e.currentTarget.style.background = 'transparent';
              e.currentTarget.style.color = 'rgba(248,113,113,0.7)';
            }}
          >
            <LogOut size={16} strokeWidth={1.8} />
            Se déconnecter
          </button>
        </div>
      </aside>
    </>
  );
};

export default Sidebar;

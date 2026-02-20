import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import { Search, Bell, Menu, X } from 'lucide-react';

const Header = ({ toggleSidebar }) => {
  const { user } = useSelector((state) => state.auth);
  const [searchFocused, setSearchFocused] = useState(false);
  const [hasNotif] = useState(true);

  const initials = user
    ? `${user.prenom?.charAt(0) ?? ''}${user.nom?.charAt(0) ?? ''}`
    : '??';

  return (
    <header style={{
      height: 64,
      background: 'var(--c-surface)',
      borderBottom: '1px solid var(--c-border)',
      display: 'flex', alignItems: 'center',
      padding: '0 24px', gap: 16,
      position: 'sticky', top: 0, zIndex: 20,
      boxShadow: 'var(--shadow-xs)',
    }}>
      {/* Mobile menu toggle */}
      <button
        onClick={toggleSidebar}
        aria-label="Ouvrir le menu"
        style={{
          display: 'none', /* shown via media query workaround below */
          alignItems: 'center', justifyContent: 'center',
          width: 36, height: 36, border: 'none', borderRadius: 8,
          background: 'transparent', cursor: 'pointer',
          color: 'var(--c-ink-2)',
          transition: 'background 0.15s',
        }}
        className="lg-hidden-show"
        onMouseEnter={e => e.currentTarget.style.background = '#f3f2ef'}
        onMouseLeave={e => e.currentTarget.style.background = 'transparent'}
      >
        <Menu size={20} />
      </button>

      {/* Search */}
      <div style={{
        display: 'flex', alignItems: 'center', gap: 8,
        padding: '0 12px',
        height: 38,
        border: `1.5px solid ${searchFocused ? 'var(--c-accent)' : 'var(--c-border)'}`,
        borderRadius: 10,
        background: searchFocused ? '#fff' : '#faf9f7',
        boxShadow: searchFocused ? '0 0 0 3px rgba(37,99,235,0.10)' : 'none',
        transition: 'border-color 0.2s, box-shadow 0.2s, background 0.2s',
        width: 280,
        flexShrink: 0,
      }}
        className="search-hidden-mobile"
      >
        <Search size={15} color={searchFocused ? 'var(--c-accent)' : 'var(--c-ink-3)'} />
        <input
          type="text"
          placeholder="Rechercher…"
          onFocus={() => setSearchFocused(true)}
          onBlur={() => setSearchFocused(false)}
          style={{
            border: 'none', outline: 'none',
            background: 'transparent',
            fontSize: '0.855rem', color: 'var(--c-ink)',
            fontFamily: 'var(--font-sans)',
            width: '100%',
          }}
        />
        <span style={{
          fontSize: '0.7rem', fontWeight: 600,
          color: 'var(--c-ink-3)',
          background: 'var(--c-border)',
          padding: '2px 6px', borderRadius: 5,
          flexShrink: 0,
          fontFamily: 'var(--font-mono)',
        }}>
          ⌘K
        </span>
      </div>

      {/* Spacer */}
      <div style={{ flex: 1 }} />

      {/* Notifications */}
      <button
        aria-label="Notifications"
        style={{
          position: 'relative',
          width: 36, height: 36, border: 'none', borderRadius: 8,
          background: 'transparent', cursor: 'pointer',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: 'var(--c-ink-2)',
          transition: 'background 0.15s, color 0.15s',
        }}
        onMouseEnter={e => {
          e.currentTarget.style.background = '#f3f2ef';
          e.currentTarget.style.color = 'var(--c-ink)';
        }}
        onMouseLeave={e => {
          e.currentTarget.style.background = 'transparent';
          e.currentTarget.style.color = 'var(--c-ink-2)';
        }}
      >
        <Bell size={18} />
        {hasNotif && (
          <span style={{
            position: 'absolute', top: 6, right: 6,
            width: 7, height: 7, borderRadius: '50%',
            background: '#ef4444',
            border: '1.5px solid var(--c-surface)',
          }} />
        )}
      </button>

      {/* Divider */}
      <div style={{ width: 1, height: 24, background: 'var(--c-border)', margin: '0 4px' }} />

      {/* User */}
      <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
        <div style={{ textAlign: 'right' }} className="user-name-hidden-mobile">
          <p style={{ fontSize: '0.855rem', fontWeight: 600, color: 'var(--c-ink)', margin: 0, lineHeight: 1.2 }}>
            {user?.prenom} {user?.nom}
          </p>
          <p style={{ fontSize: '0.72rem', color: 'var(--c-ink-2)', margin: 0, marginTop: 2 }}>
            {user?.role ?? 'Utilisateur'}
          </p>
        </div>
        <div style={{
          width: 36, height: 36, borderRadius: 10,
          background: 'linear-gradient(135deg, #2563eb, #7c3aed)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: '#fff', fontWeight: 700, fontSize: '0.8rem',
          letterSpacing: '0.02em',
          cursor: 'pointer',
          boxShadow: '0 2px 8px rgba(37,99,235,0.25)',
          flexShrink: 0,
          userSelect: 'none',
        }}>
          {initials}
        </div>
      </div>

      <style>{`
        @media (max-width: 1024px) {
          .lg-hidden-show { display: flex !important; }
          .search-hidden-mobile { width: 200px !important; }
          .user-name-hidden-mobile { display: none !important; }
        }
        @media (max-width: 640px) {
          .search-hidden-mobile { display: none !important; }
        }
      `}</style>
    </header>
  );
};

export default Header;

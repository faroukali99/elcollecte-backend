import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import Header from './Header';
import Sidebar from './Sidebar';
import OfflineBanner from './OfflineBanner';

const Layout = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  return (
    <div style={{ minHeight: '100vh', background: 'var(--c-bg)', display: 'flex', flexDirection: 'column' }}>
      <OfflineBanner />
      <div style={{ display: 'flex', flex: 1, overflow: 'hidden', height: '100vh' }}>
        <Sidebar
          isOpen={isSidebarOpen}
          closeSidebar={() => setIsSidebarOpen(false)}
        />

        {/* Main content — offset by sidebar width on desktop */}
        <div style={{
          flex: 1, display: 'flex', flexDirection: 'column',
          minWidth: 0, overflow: 'hidden',
          marginLeft: 240,  /* sidebar width */
        }}
          className="layout-main"
        >
          <Header toggleSidebar={() => setIsSidebarOpen(!isSidebarOpen)} />

          <main style={{
            flex: 1, overflowY: 'auto',
            padding: '0 28px 40px',
          }}>
            <div style={{ maxWidth: 1200, margin: '0 auto' }}>
              <Outlet />
            </div>
          </main>
        </div>
      </div>

      <style>{`
        @media (max-width: 1024px) {
          .layout-main { margin-left: 0 !important; }
        }
      `}</style>
    </div>
  );
};

export default Layout;

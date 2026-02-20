import React from 'react';
import { useSelector } from 'react-redux';
import { Search, Bell, HelpCircle, Menu } from 'lucide-react';

const Header = ({ toggleSidebar }) => {
  const { user } = useSelector((state) => state.auth); // Utilisation de useSelector

  return (
    <header className="bg-white border-b border-gray-200 h-16 flex items-center justify-between px-4 lg:px-8 sticky top-0 z-20">
      <div className="flex items-center gap-4">
        <button
          onClick={toggleSidebar}
          className="p-2 hover:bg-gray-100 rounded-lg lg:hidden text-gray-600"
        >
          <Menu size={20} />
        </button>

        {/* Search Bar */}
        <div className="hidden md:flex items-center bg-gray-50 border border-gray-200 rounded-lg px-3 py-2 w-64 focus-within:ring-2 focus-within:ring-blue-500 focus-within:border-transparent transition-all">
          <Search size={18} className="text-gray-400 mr-2" />
          <input
            type="text"
            placeholder="Rechercher..."
            className="bg-transparent border-none outline-none text-sm w-full text-gray-700 placeholder-gray-400"
          />
        </div>
      </div>

      <div className="flex items-center gap-2 sm:gap-4">
        <button className="p-2 text-gray-500 hover:bg-gray-100 rounded-full transition-colors relative">
          <Bell size={20} />
          <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-red-500 rounded-full border-2 border-white"></span>
        </button>

        <button className="p-2 text-gray-500 hover:bg-gray-100 rounded-full transition-colors hidden sm:block">
          <HelpCircle size={20} />
        </button>

        <div className="h-8 w-px bg-gray-200 mx-1 hidden sm:block"></div>

        <div className="flex items-center gap-3 pl-1">
          <div className="text-right hidden sm:block">
            <p className="text-sm font-semibold text-gray-800 leading-none">{user?.prenom} {user?.nom}</p>
            <p className="text-xs text-gray-500 mt-1">{user?.role || 'Utilisateur'}</p>
          </div>
          <div className="w-9 h-9 rounded-full bg-gradient-to-tr from-blue-600 to-blue-400 flex items-center justify-center text-white font-bold shadow-md cursor-pointer hover:ring-2 hover:ring-offset-2 hover:ring-blue-500 transition-all">
            {user?.prenom?.charAt(0)}{user?.nom?.charAt(0)}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
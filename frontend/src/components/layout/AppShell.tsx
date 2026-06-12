import { NavLink, Outlet } from 'react-router-dom';
import { Button } from '@/components/common/Button';
import { useAuth } from '@/hooks/useAuth';

const links = [
  { to: '/dashboard', label: 'Главная' },
  { to: '/generate', label: 'Генерация' },
  { to: '/history', label: 'История' },
  { to: '/characters', label: 'Персонажи' },
  { to: '/favorites', label: 'Избранное' },
];

export function Navbar() {
  const { user, logout } = useAuth();

  return (
    <header className="navbar">
      <div className="navbar-inner">
        <NavLink to="/dashboard" className="navbar-brand">
          DD Person
        </NavLink>
        <nav className="navbar-links">
          {links.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}
            >
              {link.label}
            </NavLink>
          ))}
        </nav>
        <div className="navbar-user">
          <span className="user-name">{user?.displayName}</span>
          <Button variant="ghost" type="button" onClick={() => void logout()}>
            Выйти
          </Button>
        </div>
      </div>
    </header>
  );
}

export function AppShell() {
  return (
    <div className="app-shell">
      <Navbar />
      <main className="app-main">
        <Outlet />
      </main>
    </div>
  );
}

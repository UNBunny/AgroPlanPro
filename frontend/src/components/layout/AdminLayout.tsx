import { ReactNode, useState } from "react";

interface AdminLayoutProps {
  children: ReactNode;
}

const AdminLayout = ({ children }: AdminLayoutProps) => {
  const [activeSidebar, setActiveSidebar] = useState(true);

  return (
    <div className="admin-layout">
      <header className="admin-header">
        <div className="logo">
          <h1>🌾 AgroPlanPro</h1>
        </div>
        <button 
          className="sidebar-toggle" 
          onClick={() => setActiveSidebar(!activeSidebar)}
        >
          {activeSidebar ? '◀' : '▶'}
        </button>
        <div className="admin-controls">
          <span className="admin-user">Администратор</span>
        </div>
      </header>
      
      <div className="admin-body">
        <nav className={`admin-sidebar ${activeSidebar ? 'active' : 'inactive'}`}>
          <ul className="nav-items">
            <li className="nav-item active">
              <a href="#" className="nav-link">
                <span className="icon">🌾</span>
                <span className="label">Поля</span>
              </a>
            </li>
            <li className="nav-item">
              <a href="#" className="nav-link">
                <span className="icon">🚜</span>
                <span className="label">Техника</span>
              </a>
            </li>
            <li className="nav-item">
              <a href="#" className="nav-link">
                <span className="icon">📊</span>
                <span className="label">Аналитика</span>
              </a>
            </li>
            <li className="nav-item">
              <a href="#" className="nav-link">
                <span className="icon">⚙️</span>
                <span className="label">Настройки</span>
              </a>
            </li>
          </ul>
        </nav>
        
        <main className="admin-content">
          {children}
        </main>
      </div>
    </div>
  );
};

export default AdminLayout;

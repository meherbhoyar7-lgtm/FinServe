import { NavLink } from 'react-router-dom'
import { LayoutDashboard, Search, FileText, Brain, Network, Shield, Cpu } from 'lucide-react'

const navItems = [
  { path: '/', label: 'Dashboard', icon: <LayoutDashboard size={14} />, end: true },
]

const modules = [
  { path: '/module1', label: 'String Algorithms', badge: 'CO1', icon: <Search size={14} />, cls: 'm1' },
  { path: '/module2', label: 'Suffix Structures', badge: 'CO2', icon: <FileText size={14} />, cls: 'm2' },
  { path: '/module3', label: 'Advanced DP', badge: 'CO3', icon: <Brain size={14} />, cls: 'm3' },
  { path: '/module4', label: 'Network Flow', badge: 'CO4', icon: <Network size={14} />, cls: 'm4' },
  { path: '/module5', label: 'NP-Completeness', badge: 'CO5', icon: <Shield size={14} />, cls: 'm5' },
  { path: '/module6', label: 'Randomised & Parallel', badge: 'CO6', icon: <Cpu size={14} />, cls: 'm6' },
]

export default function Sidebar() {
  return (
    <nav className="sidebar">
      <div className="sidebar-header">
        <div className="sidebar-brand">
          <div className="sidebar-brand-icon">FS</div>
          <div>
            <h1>FinServe</h1>
            <div className="sidebar-brand-sub">Banking Analytics</div>
          </div>
        </div>
      </div>

      <div className="sidebar-nav">
        <div className="nav-group-label">General</div>
        {navItems.map(item => (
          <NavLink
            key={item.path}
            to={item.path}
            end={item.end}
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
          >
            <div className="nav-icon">{item.icon}</div>
            {item.label}
          </NavLink>
        ))}

        <div className="nav-group-label" style={{ marginTop: 8 }}>Modules</div>
        {modules.map(m => (
          <NavLink
            key={m.path}
            to={m.path}
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
          >
            <div className="nav-icon" style={{ color: `var(--${m.cls})` }}>{m.icon}</div>
            {m.label}
            <span className="nav-badge">{m.badge}</span>
          </NavLink>
        ))}
      </div>

      <div className="sidebar-footer">
        <span className="dot dot-green" />
        Server Online
      </div>
    </nav>
  )
}

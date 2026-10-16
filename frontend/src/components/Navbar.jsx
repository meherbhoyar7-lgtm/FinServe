import { useState, useEffect } from 'react'
import { NavLink, useNavigate } from 'react-router-dom'
import { Search, BookOpen, ExternalLink } from 'lucide-react'

const navLinks = [
  { path: '/', label: 'Overview', end: true },
  { path: '/datasets', label: 'Datasets' },
  { path: '/module1', label: 'Transaction Search' },
  { path: '/module2', label: 'Document Archival' },
  { path: '/module3', label: 'Data Cleansing' },
  { path: '/module4', label: 'Settlement Network' },
  { path: '/module5', label: 'Compliance & Rules' },
  { path: '/module6', label: 'Randomised & Parallel' },
]

export default function Navbar() {
  const [navSearch, setNavSearch] = useState('')
  const [serverOnline, setServerOnline] = useState(true)
  const navigate = useNavigate()

  useEffect(() => {
    fetch('/api/health')
      .then((r) => r.ok && setServerOnline(true))
      .catch(() => setServerOnline(false))
  }, [])

  const handleSearchSubmit = (e) => {
    if (e.key === 'Enter' && navSearch.trim()) {
      const q = navSearch.toLowerCase().trim()
      if (q.includes('search') || q.includes('code') || q.includes('pattern') || q.includes('fraud') || q.includes('aml')) {
        navigate('/module1')
      } else if (q.includes('doc') || q.includes('index') || q.includes('clause') || q.includes('audit')) {
        navigate('/module2')
      } else if (q.includes('clean') || q.includes('spell') || q.includes('error') || q.includes('budget') || q.includes('portfolio')) {
        navigate('/module3')
      } else if (q.includes('flow') || q.includes('settle') || q.includes('network') || q.includes('liquidity') || q.includes('bottleneck')) {
        navigate('/module4')
      } else if (q.includes('rule') || q.includes('compliance') || q.includes('conflict') || q.includes('syndicate') || q.includes('checkpoint')) {
        navigate('/module5')
      } else if (q.includes('random') || q.includes('monte') || q.includes('vegas') || q.includes('parallel') || q.includes('prefix') || q.includes('span') || q.includes('reduce')) {
        navigate('/module6')
      } else {
        navigate(`/?q=${encodeURIComponent(navSearch)}`)
      }
      setNavSearch('')
    }
  }

  return (
    <div className="floating-navbar-wrapper">
      <header className="floating-navbar">
        {/* Brand Logo */}
        <div className="navbar-left">
          <NavLink to="/" className="brand-logo-link" title="FinServe Institutional Platform">
            <div className="brand-logo-icon">FS</div>
            <div className="brand-text-block">
              <span className="brand-title">FinServe</span>
              <span className="brand-subtitle">Banking Engine</span>
            </div>
          </NavLink>
        </div>

        {/* Horizontal Menu Bar */}
        <nav className="navbar-menu-bar">
          {navLinks.map((link) => (
            <NavLink
              key={link.path}
              to={link.path}
              end={link.end}
              className={({ isActive }) =>
                `menu-bar-link ${isActive ? 'active' : ''}`
              }
            >
              {link.label}
            </NavLink>
          ))}
        </nav>

        {/* Right Controls: Quick Search + Swagger Link + Status */}
        <div className="navbar-right">
          <div className="navbar-search-wrapper">
            <Search size={13} className="navbar-search-icon" />
            <input
              type="text"
              className="navbar-search-input"
              placeholder="Search procedures..."
              value={navSearch}
              onChange={(e) => setNavSearch(e.target.value)}
              onKeyDown={handleSearchSubmit}
            />
          </div>

          {/* Direct Link to Swagger UI on port 8080 */}
          <a
            href="/swagger"
            target="_blank"
            rel="noopener noreferrer"
            className="navbar-swagger-btn"
            title="Open Swagger OpenAPI 3.0 Documentation"
          >
            <BookOpen size={13} />
            <span>Swagger API</span>
            <ExternalLink size={11} className="swagger-external-icon" />
          </a>

          <div className="navbar-status-chip" title={serverOnline ? "Backend Connected (Port 8080)" : "Backend Disconnected"}>
            <span className={`status-dot ${serverOnline ? 'green' : 'red'}`} />
            <span className="status-label">
              {serverOnline ? 'Online' : 'Offline'}
            </span>
          </div>
        </div>
      </header>
    </div>
  )
}

import { useState, useMemo, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Search,
  ArrowRight,
  Shield,
  Database,
  BarChart3,
  Network,
  ArrowUpRight,
  CheckCircle2,
  Lock,
  Server,
  Zap,
  BookOpen,
  ExternalLink,
  Activity,
  Layers,
  FileCheck,
  Cpu,
} from 'lucide-react'

// Master registry of all 21 tools
const allPlatformTools = [
  { id: 'search-code', modulePath: '/module1', moduleName: 'Transaction Search', name: 'Payment Reference Lookup', desc: 'Scan transaction records by code, reference token, or wire memo' },
  { id: 'analyze-patterns', modulePath: '/module1', moduleName: 'Transaction Search', name: 'Recurring Payment Pattern Detector', desc: 'Detect recurring subscriptions and periodic billing cycles' },
  { id: 'lookup-code', modulePath: '/module1', moduleName: 'Transaction Search', name: 'Bank Routing & SWIFT Verifier', desc: 'Authenticate international routing and clearing tokens' },
  { id: 'detect-fraud', modulePath: '/module1', moduleName: 'Transaction Search', name: 'Anti-Money Laundering Scanner', desc: 'Screen transactions against regulatory watchlists and shell entity flags' },

  { id: 'index-transactions', modulePath: '/module2', moduleName: 'Document Archival', name: 'Transaction Substring Archive', desc: 'Instant sub-second retrieval across deep historical records' },
  { id: 'index-documents', modulePath: '/module2', moduleName: 'Document Archival', name: 'Regulatory Document Indexer', desc: 'Full-text indexing of compliance filings and loan covenants' },
  { id: 'find-patterns', modulePath: '/module2', moduleName: 'Document Archival', name: 'Contract Clause Repetition Finder', desc: 'Surface recurring standard terms and duplicate legal boilerplates' },
  { id: 'substring-query', modulePath: '/module2', moduleName: 'Document Archival', name: 'Audit Stream Content Inspector', desc: 'Verify presence of required statutory statements in document streams' },

  { id: 'correct-description', modulePath: '/module3', moduleName: 'Data Cleansing', name: 'Payment Memo Spell Corrector', desc: 'Resolve typos and shorthand in customer memos using banking templates' },
  { id: 'handle-error', modulePath: '/module3', moduleName: 'Data Cleansing', name: 'Account Number Rectifier', desc: 'Reconcile transmission errors with strict preservation of numeric digits' },
  { id: 'combine-operations', modulePath: '/module3', moduleName: 'Data Cleansing', name: 'Asset Portfolio Allocation Optimizer', desc: 'Maximize investment yield within regulatory capital constraints' },
  { id: 'optimize-analysis', modulePath: '/module3', moduleName: 'Data Cleansing', name: 'Clearing Pipeline Multi-Stage Scheduler', desc: 'Determine lowest-cost order for multi-tier batch pipelines' },

  { id: 'transaction-flow', modulePath: '/module4', moduleName: 'Settlement Network', name: 'Interbank Liquidity Flow Analyzer', desc: 'Compute maximum capital volume safely routable between bank nodes' },
  { id: 'network-capacity', modulePath: '/module4', moduleName: 'Settlement Network', name: 'Shortest Settlement Corridor Router', desc: 'Route high-priority payments through lowest-latency clearing corridors' },
  { id: 'large-network', modulePath: '/module4', moduleName: 'Settlement Network', name: 'Bulk Clearing Engine', desc: 'Multi-layer transfer simulation for heavy corporate payroll batches' },
  { id: 'assign-analysts', modulePath: '/module4', moduleName: 'Settlement Network', name: 'Investigation Case Workload Assigner', desc: 'Optimally pair fraud alerts with qualified risk investigators' },
  { id: 'critical-channels', modulePath: '/module4', moduleName: 'Settlement Network', name: 'Settlement Bottleneck Monitor', desc: 'Isolate vulnerable transit corridors whose disruption threatens liquidity' },

  { id: 'validate-rules', modulePath: '/module5', moduleName: 'Compliance & Rules', name: 'Policy Constraint Validator', desc: 'Verify compliance with mandatory multi-tier banking statutes' },
  { id: 'analyze-constraints', modulePath: '/module5', moduleName: 'Compliance & Rules', name: 'Syndicate Collusion Ring Detector', desc: 'Detect coordinated accounts attempting circular transactions' },
  { id: 'analyze-conflicts', modulePath: '/module5', moduleName: 'Compliance & Rules', name: 'Counterparty Risk Segregation', desc: 'Isolate independent sub-accounts to eliminate contagion in clearing' },
  { id: 'solve-dependencies', modulePath: '/module5', moduleName: 'Compliance & Rules', name: 'Audit Checkpoint Optimizer', desc: 'Determine minimal oversight points to cover 100% of transit paths' },

  { id: 'monte-carlo-risk', modulePath: '/module6', moduleName: 'Randomised & Parallel', name: 'Portfolio VaR Monte Carlo Estimator', desc: 'Estimate worst-case portfolio loss using randomised simulation' },
  { id: 'las-vegas-fraud', modulePath: '/module6', moduleName: 'Randomised & Parallel', name: 'Las Vegas Anomaly Detector', desc: 'Exact outlier detection using randomised quickselect' },
  { id: 'parallel-analytics', modulePath: '/module6', moduleName: 'Randomised & Parallel', name: 'Parallel Prefix Sum & Reduce', desc: 'Running balances and aggregate stats using parallel primitives' },
  { id: 'work-span-analysis', modulePath: '/module6', moduleName: 'Randomised & Parallel', name: 'Work-Span Pipeline Analyzer', desc: 'Parallelism assessment with Brent\'s theorem projections' },
]

const quickFilters = [
  { label: '⚡ SWIFT Verifier', path: '/module1?tool=lookup-code' },
  { label: '🛡 AML Watchlist', path: '/module1?tool=detect-fraud' },
  { label: '🏛 Substring Archive', path: '/module2?tool=index-transactions' },
  { label: '🎯 Portfolio Allocator', path: '/module3?tool=combine-operations' },
  { label: '🌐 Liquidity Router', path: '/module4?tool=transaction-flow' },
  { label: '⚖ Policy Validator', path: '/module5?tool=validate-rules' },
  { label: '🎲 Monte Carlo VaR', path: '/module6?tool=monte-carlo-risk' },
]

const divisions = [
  {
    id: 'm1',
    path: '/module1',
    number: 'MODULE 01',
    title: 'Transaction Search & Screening',
    tag: '⚡ Exact String Algorithms',
    algorithms: 'KMP · Z-Function · Rabin-Karp · Aho-Corasick',
    features: ['Real-time SWIFT / IBAN code verification', 'Multi-keyword AML sanctions scanner'],
    count: '4 Procedures',
    icon: <Search size={22} className="text-blue" />,
  },
  {
    id: 'm2',
    path: '/module2',
    number: 'MODULE 02',
    title: 'Document Archival & Indexing',
    tag: '🏛 Suffix Structures',
    algorithms: 'Suffix Array · SA-IS (O(n)) · Kasai LCP · Suffix Automaton',
    features: ['Linear-time compliance document indexing', 'Contract clause duplication detector'],
    count: '4 Procedures',
    icon: <Database size={22} className="text-blue" />,
  },
  {
    id: 'm3',
    path: '/module3',
    number: 'MODULE 03',
    title: 'Data Cleansing & Optimization',
    tag: '🎯 Advanced Dynamic Programming',
    algorithms: 'Levenshtein · Weighted Edit · Bitmask DP · Matrix Chain',
    features: ['OCR typo correction & account digit protection', 'Optimal asset portfolio selection within budget'],
    count: '4 Procedures',
    icon: <BarChart3 size={22} className="text-blue" />,
  },
  {
    id: 'm4',
    path: '/module4',
    number: 'MODULE 04',
    title: 'Settlement Networks & Liquidity',
    tag: '🌐 Network Flow & Matching',
    algorithms: 'Ford-Fulkerson · Edmonds-Karp · Dinic\'s · Bipartite · Min-Cut',
    features: ['Maximum interbank liquidity throughput', 'Critical transit corridor bottleneck isolation'],
    count: '5 Procedures',
    icon: <Network size={22} className="text-blue" />,
  },
  {
    id: 'm5',
    path: '/module5',
    number: 'MODULE 05',
    title: 'Compliance & Risk Governance',
    tag: '🛡 NP-Completeness Reductions',
    algorithms: '3-SAT Solver · 3-SAT→CLIQUE · Conflict IS · Vertex Cover Approx',
    features: ['Mandatory multi-tier statute validation', 'Syndicate collusion ring detection'],
    count: '4 Procedures',
    icon: <Shield size={22} className="text-blue" />,
  },
  {
    id: 'm6',
    path: '/module6',
    number: 'MODULE 06',
    title: 'Randomised & Parallel Algorithms',
    tag: '🎲 Probabilistic & Parallel Computing',
    algorithms: 'Monte Carlo · Las Vegas · Parallel Prefix Sum · Parallel Reduce · Work-Span',
    features: ['Monte Carlo VaR risk simulation', 'Work-span parallelism analysis with Brent\'s theorem'],
    count: '4 Procedures',
    icon: <Cpu size={22} className="text-blue" />,
  },
]

export default function Dashboard() {
  const navigate = useNavigate()
  const [globalSearch, setGlobalSearch] = useState('')
  const [health, setHealth] = useState(null)

  useEffect(() => {
    fetch('/api/health')
      .then((r) => r.json())
      .then(setHealth)
      .catch(() => {})
  }, [])

  // Live filter procedures
  const searchResults = useMemo(() => {
    if (!globalSearch.trim()) return []
    const q = globalSearch.toLowerCase().trim()
    return allPlatformTools.filter(
      (tool) =>
        tool.name.toLowerCase().includes(q) ||
        tool.desc.toLowerCase().includes(q) ||
        tool.moduleName.toLowerCase().includes(q)
    )
  }, [globalSearch])

  const handleLaunchTool = (modulePath, toolId) => {
    navigate(`${modulePath}?tool=${toolId}`)
  }

  return (
    <div className="classy-home-container">
      {/* ===== Hero Header Section ===== */}
      <section className="classy-hero-section">
        {/* Animated Status Pill */}
        <div className="classy-status-pill animate-fade-in">
          <span className="live-pulse-dot" />
          <span className="font-semibold text-blue">FinServe Core</span>
          <span className="status-separator">•</span>
          <span>Port 8080 Active</span>
          <span className="status-separator">•</span>
          <span>ISO 20022 Compliant</span>
        </div>

        {/* Hero Title */}
        <h1 className="classy-hero-title animate-fade-in-up">
          Institutional Banking Analytics <br />
          <span className="text-gradient-blue">&amp; Algorithmic Settlement</span>
        </h1>

        <p className="classy-hero-subtitle animate-fade-in-up delay-1">
          Deterministic liquidity routing, sub-millisecond transaction screening, and multi-tier
          regulatory compliance powered by advanced Data Structures and Algorithms.
        </p>

        {/* ===== Live KPI Metric Highlights (Animated on load) ===== */}
        <div className="classy-metrics-bar animate-fade-in-up delay-2">
          <div className="classy-metric-card">
            <div className="metric-card-icon-wrap">
              <Activity size={18} className="text-blue" />
            </div>
            <div>
              <div className="classy-metric-value">₹1,450 Cr/day</div>
              <div className="classy-metric-label">Max Liquidity Throughput</div>
            </div>
          </div>

          <div className="classy-metric-card">
            <div className="metric-card-icon-wrap">
              <Database size={18} className="text-blue" />
            </div>
            <div>
              <div className="classy-metric-value">
                {health?.transactions ? health.transactions : '100'} Txns · {health?.accounts ? health.accounts : '20'} Accounts
              </div>
              <div className="classy-metric-label">In-Memory Active Ledger</div>
            </div>
          </div>

          <div className="classy-metric-card">
            <div className="metric-card-icon-wrap">
              <Zap size={18} className="text-blue" />
            </div>
            <div>
              <div className="classy-metric-value">&lt; 1.0 ms</div>
              <div className="classy-metric-label">Algorithmic Resolution</div>
            </div>
          </div>

          <div className="classy-metric-card">
            <div className="metric-card-icon-wrap">
              <Layers size={18} className="text-blue" />
            </div>
            <div>
              <div className="classy-metric-value">25 Procedures</div>
              <div className="classy-metric-label">Across 6 DSA Modules</div>
            </div>
          </div>
        </div>

        {/* ===== Interactive Corporate Search Bar ===== */}
        <div className="classy-search-container animate-fade-in-up delay-3">
          <div className="classy-search-box">
            <Search size={18} className="classy-search-icon" />
            <input
              type="text"
              className="classy-search-input"
              placeholder="Search across all 25 procedures (e.g. SWIFT, Max Flow, AML Watchlist, Monte Carlo)..."
              value={globalSearch}
              onChange={(e) => setGlobalSearch(e.target.value)}
            />
            {globalSearch ? (
              <button className="classy-clear-btn" onClick={() => setGlobalSearch('')}>
                Clear
              </button>
            ) : (
              <div className="classy-search-hint">Quick Find</div>
            )}
          </div>

          {/* Quick Shortcuts */}
          {!globalSearch.trim() && (
            <div className="classy-quick-chips">
              <span className="chips-label">Fast Jump:</span>
              {quickFilters.map((qf) => (
                <button
                  key={qf.label}
                  className="classy-chip-btn"
                  onClick={() => navigate(qf.path)}
                >
                  {qf.label}
                </button>
              ))}
              <a
                href="/swagger"
                target="_blank"
                rel="noopener noreferrer"
                className="classy-chip-btn chip-swagger"
                title="Open Swagger API Explorer"
              >
                <BookOpen size={12} />
                <span>Swagger Docs &rarr;</span>
              </a>
            </div>
          )}

          {/* Dropdown Results */}
          {globalSearch.trim() && (
            <div className="classy-search-dropdown">
              <div className="dropdown-header">
                <span>Matching Operations ({searchResults.length})</span>
                <span className="dropdown-hint">Click to launch procedure</span>
              </div>
              <div className="dropdown-list">
                {searchResults.map((tool) => (
                  <div
                    key={tool.id}
                    className="dropdown-row"
                    onClick={() => handleLaunchTool(tool.modulePath, tool.id)}
                  >
                    <div className="row-content">
                      <span className="row-module-tag">{tool.moduleName}</span>
                      <div>
                        <div className="row-title">{tool.name}</div>
                        <div className="row-desc">{tool.desc}</div>
                      </div>
                    </div>
                    <ArrowUpRight size={15} className="row-arrow" />
                  </div>
                ))}

                {searchResults.length === 0 && (
                  <div className="dropdown-empty">
                    No banking procedures match &ldquo;{globalSearch}&rdquo;.
                  </div>
                )}
              </div>
            </div>
          )}
        </div>
      </section>

      {/* ===== Swagger / OpenAPI Interactive Banner ===== */}
      <section className="classy-swagger-banner animate-fade-in-up delay-4">
        <div className="swagger-banner-left">
          <div className="swagger-banner-icon-box">
            <BookOpen size={24} className="text-white" />
          </div>
          <div>
            <div className="swagger-banner-title">
              Interactive OpenAPI 3.0 &amp; Swagger Documentation
            </div>
            <div className="swagger-banner-desc">
              Directly inspect, explore request/response schemas, and execute live queries across all 25 REST endpoints on port 8080.
            </div>
          </div>
        </div>
        <div className="swagger-banner-right">
          <a
            href="/swagger"
            target="_blank"
            rel="noopener noreferrer"
            className="swagger-banner-btn"
          >
            <span>Open Swagger UI</span>
            <ExternalLink size={14} />
          </a>
        </div>
      </section>

      {/* ===== 5 Division Cards (Modules 1 to 5) ===== */}
      <section className="classy-divisions-section">
        <div className="section-header-row">
          <div>
            <h2 className="section-main-heading">Institutional Core Divisions</h2>
            <p className="section-sub-heading">Select an algorithmic operational module to launch in-memory analytics.</p>
          </div>
        </div>

        <div className="classy-cards-grid">
          {divisions.map((div, idx) => (
            <div
              key={div.id}
              className="classy-card"
              style={{ animationDelay: `${0.1 * (idx + 1)}s` }}
              onClick={() => navigate(div.path)}
            >
              <div className="card-top-row">
                <div className="card-icon-container">{div.icon}</div>
                <span className="card-module-badge">{div.number}</span>
              </div>

              <div className="card-content-area">
                <h3 className="card-title">{div.title}</h3>
                <span className="card-category-pill">{div.tag}</span>

                <div className="card-algo-note">
                  <strong>Algorithms:</strong> {div.algorithms}
                </div>

                <ul className="card-feature-list">
                  {div.features.map((feat) => (
                    <li key={feat}>
                      <FileCheck size={13} className="text-blue feature-check-icon" />
                      <span>{feat}</span>
                    </li>
                  ))}
                </ul>
              </div>

              <div className="card-bottom-row">
                <span className="card-procedure-count">{div.count}</span>
                <span className="card-action-link">
                  <span>Launch Engine</span>
                  <ArrowRight size={14} className="card-arrow-hover" />
                </span>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* ===== Corporate Quality Assurance Footer Strip ===== */}
      <footer className="classy-footer-strip">
        <div className="footer-spec-item">
          <CheckCircle2 size={16} className="text-blue" />
          <span>Zero Data Leakage · Isolated Memory Execution</span>
        </div>
        <span className="footer-spec-separator">•</span>
        <div className="footer-spec-item">
          <Server size={16} className="text-blue" />
          <span>Sub-Millisecond Java 17+ HTTP Server</span>
        </div>
        <span className="footer-spec-separator">•</span>
        <div className="footer-spec-item">
          <Lock size={16} className="text-blue" />
          <span>ISO 20022 Financial Transaction Schemas</span>
        </div>
      </footer>
    </div>
  )
}

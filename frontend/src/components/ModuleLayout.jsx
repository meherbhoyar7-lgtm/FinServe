import { useState, useMemo, useEffect, useRef } from 'react'
import { useSearchParams } from 'react-router-dom'
import {
  Search,
  Play,
  RotateCcw,
  ShieldCheck,
  Zap,
  Server,
  ChevronRight,
  PanelLeftClose,
  PanelLeftOpen,
  Database,
  Cpu,
  ChevronDown,
  ChevronUp,
  BookOpen,
} from 'lucide-react'
import { useApi, ResultModal } from './ApiResult'

export default function ModuleLayout({
  moduleTitle,
  moduleCategory,
  moduleDescription,
  tools,
}) {
  const [searchParams, setSearchParams] = useSearchParams()
  const initialToolId = searchParams.get('tool') || (tools.length > 0 ? tools[0].id : '')

  const [selectedToolId, setSelectedToolId] = useState(initialToolId)
  const [toolSearch, setToolSearch] = useState('')
  const [formInputs, setFormInputs] = useState({})
  const [modalOpen, setModalOpen] = useState(false)
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false)
  const [showTheory, setShowTheory] = useState(false)
  const theoryRef = useRef(null)

  // Auto-scroll to theory panel when expanded
  useEffect(() => {
    if (showTheory && theoryRef.current) {
      setTimeout(() => {
        theoryRef.current?.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
      }, 50)
    }
  }, [showTheory])

  const { loading, result, error, callApi, clear } = useApi()

  // Sync selected tool if URL changes
  useEffect(() => {
    const urlTool = searchParams.get('tool')
    if (urlTool && tools.some((t) => t.id === urlTool)) {
      setSelectedToolId(urlTool)
    }
  }, [searchParams, tools])

  // Filter tools based on user typing in the module search bar
  const filteredTools = useMemo(() => {
    if (!toolSearch.trim()) return tools
    const q = toolSearch.toLowerCase()
    return tools.filter(
      (t) =>
        t.name.toLowerCase().includes(q) ||
        t.shortDesc.toLowerCase().includes(q)
    )
  }, [tools, toolSearch])

  // Get active tool object
  const activeTool = useMemo(() => {
    return tools.find((t) => t.id === selectedToolId) || tools[0]
  }, [tools, selectedToolId])

  // Select tool handler
  const handleSelectTool = (toolId) => {
    setSelectedToolId(toolId)
    setSearchParams({ tool: toolId })
  }

  // Input change handler
  const handleInputChange = (fieldKey, value) => {
    setFormInputs((prev) => ({
      ...prev,
      [`${activeTool.id}_${fieldKey}`]: value,
    }))
  }

  // Get current input value (or fallback to default)
  const getInputValue = (fieldKey, defaultValue = '') => {
    const key = `${activeTool.id}_${fieldKey}`
    return formInputs[key] !== undefined ? formInputs[key] : defaultValue
  }

  // Reset form inputs for active tool
  const handleResetInputs = () => {
    setFormInputs((prev) => {
      const next = { ...prev }
      activeTool.fields?.forEach((f) => {
        delete next[`${activeTool.id}_${f.key}`]
      })
      return next
    })
  }

  // Execute the selected tool
  const handleExecute = async () => {
    if (!activeTool) return
    setModalOpen(true)

    // Build payload
    const payload = {}
    activeTool.fields?.forEach((f) => {
      const val = getInputValue(f.key, f.default)
      payload[f.key] = val
    })

    await callApi(activeTool.endpoint, Object.keys(payload).length > 0 ? payload : null)
  }

  const handleCloseModal = () => {
    setModalOpen(false)
    clear()
  }

  return (
    <div className="module-container">
      {/* ===== Dedicated Collapsible Module Sidebar ===== */}
      <aside className={`module-sidebar ${sidebarCollapsed ? 'collapsed' : ''}`}>
        {/* Sidebar Header with Collapse Button */}
        <div className="sidebar-header">
          {!sidebarCollapsed && (
            <div className="sidebar-header-info">
              <span className="sidebar-category-badge">{moduleCategory}</span>
              <h2 className="sidebar-module-title">{moduleTitle}</h2>
            </div>
          )}
          <button
            className="sidebar-collapse-toggle"
            onClick={() => setSidebarCollapsed(!sidebarCollapsed)}
            title={sidebarCollapsed ? 'Expand sidebar' : 'Collapse sidebar'}
            aria-label={sidebarCollapsed ? 'Expand sidebar' : 'Collapse sidebar'}
          >
            {sidebarCollapsed ? <PanelLeftOpen size={18} /> : <PanelLeftClose size={18} />}
          </button>
        </div>

        {/* Module Internal Search Bar (hidden when collapsed) */}
        {!sidebarCollapsed && (
          <div className="sidebar-search-box">
            <Search size={15} className="sidebar-search-icon" />
            <input
              type="text"
              placeholder="Filter procedures..."
              value={toolSearch}
              onChange={(e) => setToolSearch(e.target.value)}
            />
            {toolSearch && (
              <button className="clear-filter-btn" onClick={() => setToolSearch('')}>
                ×
              </button>
            )}
          </div>
        )}

        {/* List of Module Tools (Clean & Minimal: Icon + Title only) */}
        <nav className="sidebar-tools-list">
          {filteredTools.map((tool) => {
            const isActive = tool.id === activeTool.id
            return (
              <button
                key={tool.id}
                className={`sidebar-tool-item ${isActive ? 'active' : ''} ${
                  sidebarCollapsed ? 'is-collapsed' : ''
                }`}
                onClick={() => handleSelectTool(tool.id)}
                title={sidebarCollapsed ? tool.name : undefined}
              >
                <div className="tool-indicator" />
                <div className="tool-icon-wrapper">
                  {tool.icon || <Zap size={18} />}
                </div>
                {!sidebarCollapsed && (
                  <div className="tool-text-content">
                    <div className="tool-item-name">{tool.name}</div>
                  </div>
                )}
              </button>
            )
          })}

          {!sidebarCollapsed && filteredTools.length === 0 && (
            <div className="sidebar-empty">
              <p>No operations match &ldquo;{toolSearch}&rdquo;</p>
              <button onClick={() => setToolSearch('')} className="btn-link">
                Clear
              </button>
            </div>
          )}
        </nav>

        {/* Sidebar Footer */}
        {!sidebarCollapsed && (
          <div className="sidebar-footer">
            <div className="sidebar-status-tag">
              <span className="status-dot green" />
              <span>Dedicated Node Active</span>
            </div>
          </div>
        )}
      </aside>

      {/* ===== Main Tool Workspace ===== */}
      <main className="module-workspace">
        {activeTool ? (
          <div className="workspace-content">
            {/* Breadcrumb Navigation */}
            <div className="workspace-breadcrumb-bar">
              <div className="workspace-breadcrumb">
                {sidebarCollapsed && (
                  <button
                    className="breadcrumb-expand-sidebar-btn"
                    onClick={() => setSidebarCollapsed(false)}
                    title="Expand sidebar"
                  >
                    <PanelLeftOpen size={16} />
                    <span>Sidebar</span>
                  </button>
                )}
                <span>Overview</span>
                <ChevronRight size={14} />
                <span>{moduleTitle}</span>
                <ChevronRight size={14} />
                <span className="current">{activeTool.name}</span>
              </div>
            </div>

            {/* Tool Header Card (Clean & Focused) */}
            <div className="tool-header-card">
              <div className="tool-title-row">
                <div className="tool-icon-box">
                  {activeTool.icon || <Zap size={28} />}
                </div>
                <div className="tool-title-content">
                  <h1 className="tool-main-title">{activeTool.name}</h1>
                  <p className="tool-business-desc">{activeTool.shortDesc || activeTool.fullDesc}</p>
                </div>
              </div>
            </div>

            {/* Interactive User Input Form Card */}
            <div className="tool-card">
              <div className="card-header-row">
                <div>
                  <h3 className="card-heading">Operation Parameters</h3>
                  <p className="card-subheading">
                    {activeTool.fields && activeTool.fields.length > 0
                      ? 'Customize input values below or run with verified banking defaults.'
                      : 'Evaluates live in-memory transaction and network topologies. Click below to execute.'}
                  </p>
                </div>
                {activeTool.fields && activeTool.fields.length > 0 && (
                  <button className="btn-text" onClick={handleResetInputs}>
                    <RotateCcw size={14} /> Reset
                  </button>
                )}
              </div>

              {/* Form Fields Container */}
              {activeTool.fields && activeTool.fields.length > 0 && (
                <div className="form-fields-container">
                  {activeTool.fields.map((f) => {
                    const currentVal = getInputValue(f.key, f.default)
                    return (
                      <div key={f.key} className="form-field-group">
                        <label className="field-label">
                          {f.label}
                          {f.required && <span className="text-red">*</span>}
                        </label>
                        <input
                          type={f.type || 'text'}
                          className="field-input"
                          placeholder={f.placeholder}
                          value={currentVal}
                          onChange={(e) => handleInputChange(f.key, e.target.value)}
                        />
                      </div>
                    )
                  })}
                </div>
              )}

              {/* Action Buttons Row */}
              <div className="form-action-row">
                <button
                  className="btn-primary"
                  onClick={handleExecute}
                  disabled={loading}
                >
                  <Play size={16} />
                  <span>{activeTool.buttonText || 'Execute Analysis'}</span>
                </button>
                <button
                  type="button"
                  className="btn-theory-toggle"
                  onClick={() => setShowTheory(!showTheory)}
                >
                  <BookOpen size={15} />
                  <span>{showTheory ? 'Hide Algorithm Theory' : 'View Algorithm Theory'}</span>
                  {showTheory ? <ChevronUp size={14} /> : <ChevronDown size={14} />}
                </button>
              </div>
            </div>

            {/* Algorithm Theory & Details (Collapsible: Shown when the user wants theory) */}
            {showTheory && (
              <div ref={theoryRef} className="pipeline-card theory-animated-panel">
                <div className="pipeline-header">
                  <Cpu size={18} className="text-purple" />
                  <span>Algorithm Theory & Execution Model</span>
                </div>
                <p className="theory-full-desc">{activeTool.fullDesc}</p>
                <div className="pipeline-steps">
                  <div className="pipeline-step">
                    <div className="step-num">01</div>
                    <div className="step-info">
                      <div className="step-title">Ingest & Topology Parse</div>
                      <div className="step-desc">Tokenize accounts, wire references, or policy rules into isolated memory</div>
                    </div>
                  </div>
                  <div className="pipeline-arrow">→</div>
                  <div className="pipeline-step">
                    <div className="step-num">02</div>
                    <div className="step-info">
                      <div className="step-title">Algorithmic Resolution</div>
                      <div className="step-desc">Deterministic evaluation against exact mathematical constraints</div>
                    </div>
                  </div>
                  <div className="pipeline-arrow">→</div>
                  <div className="pipeline-step">
                    <div className="step-num">03</div>
                    <div className="step-info">
                      <div className="step-title">Statutory Attestation</div>
                      <div className="step-desc">Formulate cryptographically verified decision matrix and emit audit log</div>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* Clean Quality Assurance Strip */}
            <div className="specs-card">
              <div className="spec-item">
                <ShieldCheck size={20} className="spec-icon text-blue" />
                <div>
                  <div className="spec-title">Zero Data Leakage</div>
                  <div className="spec-desc">Secure internal memory compute · Isolated execution</div>
                </div>
              </div>
              <div className="spec-item">
                <Server size={20} className="spec-icon text-green" />
                <div>
                  <div className="spec-title">Deterministic Engine</div>
                  <div className="spec-desc">Sub-millisecond query execution · O(N+M) complexity</div>
                </div>
              </div>
              <div className="spec-item">
                <Zap size={20} className="spec-icon text-purple" />
                <div>
                  <div className="spec-title">ISO 20022 Aligned</div>
                  <div className="spec-desc">Institutional banking schemas · Immutable audit trails</div>
                </div>
              </div>
            </div>
          </div>
        ) : (
          <div className="no-selection-box">
            <p>Please select an operation from the sidebar to view details.</p>
          </div>
        )}
      </main>

      {/* Modal Popup for Results */}
      <ResultModal
        open={modalOpen}
        onClose={handleCloseModal}
        title={activeTool?.name}
        subtitle={activeTool?.shortDesc}
        result={result}
        loading={loading}
        error={error}
      />
    </div>
  )
}

import { useState, useCallback } from 'react'
import {
  X,
  CheckCircle,
  AlertCircle,
  Copy,
  Check,
  Code,
  FileText,
  ChevronRight,
  Maximize2,
  Minimize2,
  Users,
  ShieldAlert,
  ShieldCheck,
  Lock,
  ArrowRight,
  AlertTriangle,
  GitBranch,
  Cpu,
  Dice5,
  Target,
  BarChart4,
  TrendingDown,
  Layers,
  Activity,
} from 'lucide-react'

export function useApi() {
  const [loading, setLoading] = useState(false)
  const [result, setResult] = useState(null)
  const [error, setError] = useState(null)

  const callApi = useCallback(async (endpoint, body = null) => {
    setLoading(true)
    setError(null)
    setResult(null)
    try {
      const opts = body
        ? {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
          }
        : { method: 'GET' }

      const res = await fetch(`/api${endpoint}`, opts)
      if (!res.ok) {
        throw new Error(`Server returned HTTP ${res.status}`)
      }
      const data = await res.json()
      setResult(data)
    } catch (err) {
      setError(
        'Unable to connect to the backend server. Please verify the Java API server is running on port 8080.'
      )
    } finally {
      setLoading(false)
    }
  }, [])

  const clear = useCallback(() => {
    setResult(null)
    setError(null)
  }, [])

  return { loading, result, error, callApi, clear }
}

export function ResultModal({ open, onClose, title, subtitle, result, loading, error }) {
  const [activeTab, setActiveTab] = useState('summary')
  const [copied, setCopied] = useState(false)
  const [isMaximized, setIsMaximized] = useState(false)

  if (!open) return null

  const handleCopy = () => {
    if (!result) return
    navigator.clipboard.writeText(JSON.stringify(result, null, 2))
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div
        className={`modal-container ${isMaximized ? 'maximized' : ''}`}
        onClick={(e) => e.stopPropagation()}
      >
        {/* Modal Header */}
        <div className="modal-header">
          <div className="modal-header-left">
            <div className="modal-title-row">
              <span className="modal-status-badge">
                {loading ? 'PROCESSING' : error ? 'ERROR' : 'COMPLETED'}
              </span>
              <h2 className="modal-title">{title || 'Analysis Results'}</h2>
            </div>
            {subtitle && <p className="modal-subtitle">{subtitle}</p>}
          </div>

          <div className="modal-header-right">
            {result && !loading && (
              <div className="modal-tabs">
                <button
                  className={`modal-tab-btn ${activeTab === 'summary' ? 'active' : ''}`}
                  onClick={() => setActiveTab('summary')}
                >
                  <FileText size={13} /> Report View
                </button>
                <button
                  className={`modal-tab-btn ${activeTab === 'json' ? 'active' : ''}`}
                  onClick={() => setActiveTab('json')}
                >
                  <Code size={13} /> Raw Data
                </button>
              </div>
            )}

            {/* Maximize / Restore Toggle Button */}
            <button
              className="modal-icon-btn"
              onClick={() => setIsMaximized(!isMaximized)}
              title={isMaximized ? 'Restore window size' : 'Maximize window'}
              aria-label={isMaximized ? 'Restore window size' : 'Maximize window'}
            >
              {isMaximized ? <Minimize2 size={16} /> : <Maximize2 size={16} />}
            </button>

            {/* Close Button */}
            <button className="modal-close-btn" onClick={onClose} aria-label="Close modal">
              <X size={18} />
            </button>
          </div>
        </div>

        {/* Modal Body */}
        <div className="modal-body">
          {loading && (
            <div className="modal-state-box">
              <div className="corporate-spinner" />
              <h3>Executing Analysis...</h3>
              <p>Scanning in-memory banking records and compiling results.</p>
            </div>
          )}

          {error && (
            <div className="modal-state-box error-state">
              <AlertCircle size={32} className="state-icon text-red" />
              <h3>Connection Failure</h3>
              <p>{error}</p>
              <div className="cmd-hint">
                <span>Start backend:</span>
                <code>cd /Users/meherbhoyar/Desktop/FinServe && bash run.sh</code>
              </div>
            </div>
          )}

          {result && !loading && (
            <>
              {activeTab === 'summary' ? (
                <div className="results-wrapper">
                  <FormattedResultView data={result} isMaximized={isMaximized} />
                </div>
              ) : (
                <div className="json-wrapper">
                  <pre className="json-code">{JSON.stringify(result, null, 2)}</pre>
                </div>
              )}
            </>
          )}
        </div>

        {/* Modal Footer */}
        <div className="modal-footer">
          <div className="modal-footer-meta">
            {result && (
              <span>
                Status: <strong>Verified</strong> · Operational Timestamp: {new Date().toLocaleTimeString()}
              </span>
            )}
          </div>
          <div className="modal-footer-actions">
            {result && (
              <button className="btn-secondary" onClick={handleCopy}>
                {copied ? <Check size={14} className="text-green" /> : <Copy size={14} />}
                {copied ? 'Copied to Clipboard' : 'Copy JSON'}
              </button>
            )}
            <button className="btn-primary" onClick={onClose}>
              Close
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export const OutputModal = ResultModal

/* ===== Formatted Result Renderer ===== */
function FormattedResultView({ data, isMaximized }) {
  // Keys handled specifically or technical keys to suppress from generic dumping
  const handledKeys = new Set([
    'algorithm',
    'useCase',
    'timeComplexity',
    'spaceComplexity',
    'failureFunction',
    'zArray',
    'bitmask',
    'kmpComparisons',
    'bruteForceComparisons',
    'dpMatrixSample',
    'matches',
    'results',
    'flaggedTransactions',
    'sampleTransactions',
    'flowDistribution',
    'bottlenecks',
    'rules',
    'clauses',
    'optimalCombination',
    'availableOperations',
    'suggestions',
    'corrections',
    'maxClique',
    'maxIndependentSet',
    'minVertexCover',
    'suspiciousLinks',
    'dependencies',
    'twoApproximation',
    'greedyApproach',
    'comparison',
    'assignments',
    'analystCapabilities',
    'unassignedCases',
    'entities',
    'systems',
    'nodes',
    'edges',
    'errorAnalysis',
    'costWeights',
    'theoremVerification',
    'cliqueInterpretation',
    'reductionExplanation',
    'interpretation',
    'satisfyingAssignment',
    'monteCarloResults',
    'convergenceAnalysis',
    'portfolio',
    'statisticalThresholds',
    'lasVegasGuarantee',
    'parallelPrefixSum',
    'parallelReduce',
    'workSpanAnalysis',
    'pipelineOperations',
    'endToEndPipeline',
    'theoreticalFramework',
    'totalTransactions',
    'flaggedCount',
    'normalCount',
    'flagRate',
    'portfolioValue',
    'currency',
    'openingBalance',
    'tradingDays',
  ])

  // Extract primary high-level metrics
  const topMetrics = []
  if (data.totalMatches !== undefined) topMetrics.push({ label: 'Matches Found', val: data.totalMatches })
  if (data.totalTransactionsSearched !== undefined) topMetrics.push({ label: 'Transactions Searched', val: data.totalTransactionsSearched })
  if (data.portfolioValue !== undefined) topMetrics.push({ label: 'Portfolio Asset Value', val: `${data.currency || 'INR'} ${Number(data.portfolioValue).toLocaleString()}` })
  if (data.flaggedCount !== undefined && data.totalTransactions !== undefined) topMetrics.push({ label: 'Anomaly Rate', val: `${data.flaggedCount} / ${data.totalTransactions} Txns (${data.flagRate || ''})` })
  if (data.endToEndPipeline?.overallParallelism !== undefined) topMetrics.push({ label: 'Pipeline Parallelism (W/S)', val: data.endToEndPipeline.overallParallelism })
  if (data.parallelPrefixSum?.closingBalance !== undefined) topMetrics.push({ label: 'Closing Balance', val: `₹${Number(data.parallelPrefixSum.closingBalance).toLocaleString()}` })
  if (data.parallelReduce?.totalNetFlow !== undefined) topMetrics.push({ label: 'Total Net Flow', val: `₹${Number(data.parallelReduce.totalNetFlow).toLocaleString()}` })
  if (data.totalEntities !== undefined) topMetrics.push({ label: 'Entities Analyzed', val: data.totalEntities })
  if (data.totalSystems !== undefined) topMetrics.push({ label: 'Connected Systems', val: data.totalSystems })
  if (data.totalDependencies !== undefined) topMetrics.push({ label: 'Dependencies Evaluated', val: data.totalDependencies })
  if (data.totalAssigned !== undefined && data.totalCases !== undefined) topMetrics.push({ label: 'Workload Allocated', val: `${data.totalAssigned} / ${data.totalCases} Cases` })
  if (data.matchingEfficiency !== undefined) topMetrics.push({ label: 'Matching Efficiency', val: data.matchingEfficiency })
  if (data.maxFlow !== undefined) topMetrics.push({ label: 'Max Liquidity Throughput', val: `${data.maxFlow} ${data.maxFlowUnit || 'Units'}` })
  if (data.totalCost !== undefined) topMetrics.push({ label: 'Capital Budget Used', val: `$${Number(data.totalCost).toLocaleString()}` })
  if (data.totalBenefit !== undefined) topMetrics.push({ label: 'Expected Return', val: `$${Number(data.totalBenefit).toLocaleString()}` })
  if (data.roi !== undefined) topMetrics.push({ label: 'Net Projected ROI', val: data.roi })
  if (data.satisfiable !== undefined) topMetrics.push({ label: 'Compliance Status', val: data.satisfiable ? 'Fully Satisfied' : 'Violations Detected' })
  if (data.weightedEditDistance !== undefined) topMetrics.push({ label: 'Weighted Reconciliation Cost', val: data.weightedEditDistance })
  if (data.standardEditDistance !== undefined) topMetrics.push({ label: 'Character Edits Needed', val: data.standardEditDistance })
  if (data.efficiencyGain !== undefined) topMetrics.push({ label: 'Performance Multiplier', val: data.efficiencyGain })

  // Extract major sections
  const hasRiskGroups = data.maxClique || data.maxIndependentSet || data.minVertexCover
  const linksList = data.suspiciousLinks || data.dependencies
  const matchesList = data.matches || data.results || (!data.statisticalThresholds ? data.flaggedTransactions : null) || data.sampleTransactions
  const flowList = data.flowDistribution
  const bottlenecks = data.bottlenecks
  const rules = data.rules || data.clauses
  const optimalCombo = data.optimalCombination
  const availableOps = data.availableOperations
  const suggestions = data.suggestions || data.corrections
  const assignments = data.assignments
  const entityPills = data.entities || data.systems

  return (
    <div className={`report-content ${isMaximized ? 'is-maximized' : ''}`}>
      {/* Top Banner if Use Case exists */}
      {data.useCase && (
        <div className="business-summary-banner">
          <div className="banner-icon">
            <CheckCircle size={18} />
          </div>
          <div>
            <div className="banner-heading">Operational Objective</div>
            <div className="banner-text">{data.useCase}</div>
          </div>
        </div>
      )}

      {/* Primary Metrics Grid */}
      {topMetrics.length > 0 && (
        <div className="metrics-grid">
          {topMetrics.map((m, idx) => (
            <div key={idx} className="metric-tile">
              <div className="metric-label">{m.label}</div>
              <div className="metric-val">{String(m.val)}</div>
            </div>
          ))}
        </div>
      )}

      {/* ===== RISK GROUPS (Clique, Independent Set, Vertex Cover) ===== */}
      {hasRiskGroups && (
        <div className="report-section">
          <h3 className="section-title">Identified Risk Sub-Portfolios & Counterparty Clusters</h3>
          <div className="risk-groups-grid">
            {/* Max Clique */}
            {data.maxClique && (
              <div className="risk-group-card risk-border-red">
                <div className="risk-group-header">
                  <div className="risk-group-title-row">
                    <ShieldAlert size={16} className="text-red" />
                    <span className="risk-group-name">Coordinated Risk Syndicate</span>
                  </div>
                  <span className="badge badge-red">{data.maxClique.maxCliqueSize || data.maxClique.entities?.length || 3} Entities</span>
                </div>
                <p className="risk-group-meaning">
                  {data.maxClique.meaning || 'Largest group of accounts where all entities are mutually linked — highly probable collusion ring.'}
                </p>
                <div className="risk-group-entities">
                  <div className="group-label">Associated Suspect Accounts:</div>
                  <div className="entity-pills-wrap">
                    {data.maxClique.entities?.map((ent, i) => (
                      <span key={i} className="entity-pill pill-red">
                        {ent}
                      </span>
                    ))}
                  </div>
                </div>
              </div>
            )}

            {/* Max Independent Set */}
            {data.maxIndependentSet && (
              <div className="risk-group-card risk-border-green">
                <div className="risk-group-header">
                  <div className="risk-group-title-row">
                    <ShieldCheck size={16} className="text-green" />
                    <span className="risk-group-name">Safe Isolated Accounts</span>
                  </div>
                  <span className="badge badge-green">{data.maxIndependentSet.maxIndependentSetSize || data.maxIndependentSet.entities?.length || 3} Entities</span>
                </div>
                <p className="risk-group-meaning">
                  {data.maxIndependentSet.meaning || 'Largest subset with zero mutual risk links — safe for independent clearing without contagion.'}
                </p>
                <div className="risk-group-entities">
                  <div className="group-label">Non-Conflicting Accounts:</div>
                  <div className="entity-pills-wrap">
                    {data.maxIndependentSet.entities?.map((ent, i) => (
                      <span key={i} className="entity-pill pill-green">
                        {ent}
                      </span>
                    ))}
                  </div>
                </div>
              </div>
            )}

            {/* Min Vertex Cover */}
            {data.minVertexCover && (
              <div className="risk-group-card risk-border-purple">
                <div className="risk-group-header">
                  <div className="risk-group-title-row">
                    <Lock size={16} className="text-purple" />
                    <span className="risk-group-name">Audit Surveillance Checkpoints</span>
                  </div>
                  <span className="badge badge-blue">{data.minVertexCover.minVertexCoverSize || data.minVertexCover.entities?.length || 4} Accounts</span>
                </div>
                <p className="risk-group-meaning">
                  {data.minVertexCover.meaning || 'Minimum accounts to monitor to guarantee 100% oversight coverage over all suspicious channels.'}
                </p>
                <div className="risk-group-entities">
                  <div className="group-label">Key Surveillance Points:</div>
                  <div className="entity-pills-wrap">
                    {data.minVertexCover.entities?.map((ent, i) => (
                      <span key={i} className="entity-pill pill-purple">
                        {ent}
                      </span>
                    ))}
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      )}

      {/* ===== RELATIONSHIP / SUSPICIOUS LINKS TABLE ===== */}
      {linksList && linksList.length > 0 && (
        <div className="report-section">
          <h3 className="section-title">
            Suspicious Relationship Links & Dependency Paths ({linksList.length})
          </h3>
          <div className="table-responsive">
            <table className="corporate-table">
              <thead>
                <tr>
                  <th>Origin Counterparty / System</th>
                  <th>Destination Counterparty / System</th>
                  <th>Risk Indicator / Connection Type</th>
                </tr>
              </thead>
              <tbody>
                {linksList.map((link, idx) => (
                  <tr key={idx}>
                    <td className="font-semibold text-main">
                      {link.entity1 || link.system1 || link.from || '—'}
                    </td>
                    <td className="font-semibold text-main">
                      {link.entity2 || link.system2 || link.to || '—'}
                    </td>
                    <td>
                      <span className="badge badge-yellow">
                        {link.linkType || 'Suspicious Activity Link'}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* ===== INVESTIGATED ENTITIES LIST (Pills, Not Raw JSON) ===== */}
      {entityPills && Array.isArray(entityPills) && entityPills.length > 0 && (
        <div className="report-section">
          <h3 className="section-title">
            All Monitored Accounts & Clearing Nodes ({entityPills.length})
          </h3>
          <div className="entity-pills-wrap">
            {entityPills.map((ent, i) => (
              <span key={i} className="entity-pill pill-neutral">
                {ent}
              </span>
            ))}
          </div>
        </div>
      )}

      {/* ===== CASE ASSIGNMENT MATRIX (Module 4) ===== */}
      {assignments && assignments.length > 0 && (
        <div className="report-section">
          <h3 className="section-title">Investigation Case Assignment Matrix ({assignments.length} Assigned)</h3>
          <div className="table-responsive">
            <table className="corporate-table">
              <thead>
                <tr>
                  <th>Investigation Case</th>
                  <th>Assigned Specialist</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {assignments.map((item, idx) => (
                  <tr key={idx}>
                    <td className="font-semibold">{item.case}</td>
                    <td className="text-blue font-semibold">{item.assignedTo}</td>
                    <td><span className="badge badge-green">Assigned</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* ===== SYSTEM RECONCILIATION & ERROR HANDLING (Module 3) ===== */}
      {data.errorAnalysis && (
        <div className="report-section">
          <h3 className="section-title">Account Record Transmission Reconciliation</h3>
          <div className="reconciliation-card">
            <div className="recon-strings-row">
              <div className="recon-box">
                <span className="recon-tag text-red">Received Distorted String</span>
                <span className="recon-val font-mono">{data.errorData}</span>
              </div>
              <ArrowRight size={18} className="text-muted" />
              <div className="recon-box">
                <span className="recon-tag text-green">Target Banking Reference</span>
                <span className="recon-val font-mono">{data.correctData}</span>
              </div>
            </div>
            <div className="recon-metrics-row">
              <div>
                <span className="recon-label">Severity Level</span>
                <span className={`badge ${data.errorAnalysis.severity === 'HIGH' ? 'badge-red' : 'badge-yellow'}`}>
                  {data.errorAnalysis.severity}
                </span>
              </div>
              <div>
                <span className="recon-label">Digit Errors</span>
                <span className="recon-stat font-mono">{data.errorAnalysis.digitErrors}</span>
              </div>
              <div>
                <span className="recon-label">Letter Errors</span>
                <span className="recon-stat font-mono">{data.errorAnalysis.letterErrors}</span>
              </div>
              <div>
                <span className="recon-label">Preservation Priority</span>
                <span className="recon-stat text-blue">Numeric Digits Protected</span>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* ===== OPTIMAL ASSET PORTFOLIO (Module 3) ===== */}
      {optimalCombo && (
        <div className="report-section">
          <h3 className="section-title">Optimal Asset Portfolio Selection</h3>
          <div className="optimal-card">
            <div className="optimal-stats">
              <div>
                <span className="opt-label">Total Capital Required</span>
                <span className="opt-val">${Number(optimalCombo.totalCost).toLocaleString()}</span>
              </div>
              <div>
                <span className="opt-label">Projected Annual Return</span>
                <span className="opt-val text-green">${Number(optimalCombo.totalBenefit).toLocaleString()}</span>
              </div>
              <div>
                <span className="opt-label">Return on Investment</span>
                <span className="opt-val text-blue">{optimalCombo.roi}</span>
              </div>
            </div>
            <div className="opt-items-list">
              <div className="opt-items-label">Selected Portfolio Holdings:</div>
              <div className="pill-group">
                {optimalCombo.selectedOperations?.map((op, i) => (
                  <span key={i} className="asset-pill">
                    <Check size={12} className="text-green" /> {op}
                  </span>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* ===== INTERBANK FLOW (Module 4) ===== */}
      {flowList && flowList.length > 0 && (
        <div className="report-section">
          <h3 className="section-title">Interbank Route Allocation ({flowList.length} channels)</h3>
          <div className="table-responsive">
            <table className="corporate-table">
              <thead>
                <tr>
                  <th>Origin Bank / Clearing Node</th>
                  <th>Destination Corridor</th>
                  <th>Routed Flow</th>
                  <th>Total Capacity</th>
                  <th>Channel Load</th>
                </tr>
              </thead>
              <tbody>
                {flowList.map((row, i) => (
                  <tr key={i}>
                    <td>{row.from}</td>
                    <td>{row.to}</td>
                    <td className="font-mono font-semibold">{row.flow} Lakhs</td>
                    <td className="font-mono text-muted">{row.capacity} Lakhs</td>
                    <td>
                      <span className={`badge ${row.utilization === '100%' ? 'badge-red' : 'badge-green'}`}>
                        {row.utilization}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* ===== BOTTLENECKS (Module 4) ===== */}
      {bottlenecks && bottlenecks.length > 0 && (
        <div className="report-section">
          <h3 className="section-title text-red">Critical Transit Bottlenecks Detected</h3>
          <div className="bottleneck-list">
            {bottlenecks.map((b, i) => (
              <div key={i} className="bottleneck-item">
                <span className="bottleneck-dot" />
                <span>{b}</span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ===== RULES CHECKLIST (Module 5) ===== */}
      {rules && rules.length > 0 && (
        <div className="report-section">
          <h3 className="section-title">Regulatory Policy Rules Evaluated ({rules.length})</h3>
          <div className="rules-grid">
            {rules.map((r, i) => (
              <div key={i} className="rule-card">
                <div className="rule-header">
                  <span className="rule-id">{r.ruleId || r.clauseId || `Rule #${i+1}`}</span>
                  <span className="badge badge-green">VERIFIED</span>
                </div>
                <div className="rule-desc">{r.description}</div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ===== MATCHED RECORDS / TRANSACTIONS ===== */}
      {matchesList && Array.isArray(matchesList) && matchesList.length > 0 && (
        <div className="report-section">
          <h3 className="section-title">Matched Records ({matchesList.length})</h3>
          <div className="table-responsive">
            <table className="corporate-table">
              <thead>
                <tr>
                  <th>Transaction ID</th>
                  <th>Description</th>
                  <th>Amount</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {matchesList.slice(0, 20).map((txn, idx) => (
                  <tr key={idx}>
                    <td className="font-mono font-semibold text-blue">{txn.transactionId || txn.id || `TXN-${idx + 1}`}</td>
                    <td>{txn.description || txn.title || txn.text || txn.content || '—'}</td>
                    <td className="font-mono">
                      {txn.amount !== undefined ? `$${Number(txn.amount).toLocaleString(undefined, { minimumFractionDigits: 2 })}` : '—'}
                    </td>
                    <td>
                      <span
                        className={`badge ${
                          txn.status === 'COMPLETED'
                            ? 'badge-green'
                            : txn.status === 'FLAGGED'
                            ? 'badge-red'
                            : 'badge-yellow'
                        }`}
                      >
                        {txn.status || 'MATCHED'}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {matchesList.length > 20 && (
              <p className="more-records-note">+ {matchesList.length - 20} additional records matching query</p>
            )}
          </div>
        </div>
      )}

      {/* ===== SUGGESTIONS / CORRECTIONS ===== */}
      {suggestions && suggestions.length > 0 && (
        <div className="report-section">
          <h3 className="section-title">Recommended Corrections</h3>
          <div className="suggestions-list">
            {suggestions.map((s, i) => (
              <div key={i} className="suggestion-item">
                <ChevronRight size={14} className="text-blue" />
                <span>{typeof s === 'string' ? s : JSON.stringify(s)}</span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ===== MONTE CARLO RISK SIMULATION (Module 6) ===== */}
      {data.monteCarloResults && (
        <div className="report-section">
          <h3 className="section-title">Value-at-Risk (VaR) & Expected Shortfall (CVaR) Simulations</h3>
          <div className="table-responsive">
            <table className="corporate-table">
              <thead>
                <tr>
                  <th>Simulations</th>
                  <th>Confidence</th>
                  <th>VaR (95%)</th>
                  <th>CVaR / Expected Shortfall</th>
                  <th>Mean Return</th>
                  <th>Worst Case</th>
                  <th>Best Case</th>
                  <th>Latency</th>
                </tr>
              </thead>
              <tbody>
                {data.monteCarloResults.map((row, idx) => (
                  <tr key={idx}>
                    <td className="font-semibold text-blue font-mono">{Number(row.simulations).toLocaleString()} runs</td>
                    <td><span className="badge badge-green">{(row.confidence * 100).toFixed(0)}%</span></td>
                    <td className="font-mono text-red font-semibold">{typeof row.varAmountINR === 'number' ? `₹${row.varAmountINR.toLocaleString(undefined, { minimumFractionDigits: 2 })}` : row.varPercentile}</td>
                    <td className="font-mono text-yellow font-semibold">{typeof row.cvarAmountINR === 'number' ? `₹${row.cvarAmountINR.toLocaleString(undefined, { minimumFractionDigits: 2 })}` : '—'}</td>
                    <td className="font-mono">{row.meanReturn}</td>
                    <td className="font-mono text-red">{row.worstCase}</td>
                    <td className="font-mono text-green">{row.bestCase}</td>
                    <td className="font-mono text-muted">{row.executionTimeMs} ms</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {data.portfolio && data.portfolio.length > 0 && (
            <div style={{ marginTop: '16px' }}>
              <div className="group-label" style={{ marginBottom: '8px', fontSize: '11px', fontWeight: 600, color: 'var(--text-secondary)' }}>
                Target Portfolio Asset Weights:
              </div>
              <div className="entity-pills-wrap">
                {data.portfolio.map((p, i) => (
                  <span key={i} className="entity-pill pill-neutral">
                    <strong>{p.asset}</strong>: {(p.weight * 100).toFixed(0)}% (₹{Number(p.allocation).toLocaleString()})
                  </span>
                ))}
              </div>
            </div>
          )}

          {data.convergenceAnalysis && (
            <div className="business-summary-banner" style={{ marginTop: '16px' }}>
              <div className="banner-icon">
                <Dice5 size={18} />
              </div>
              <div>
                <div className="banner-heading">Monte Carlo Convergence Law ({data.convergenceAnalysis.property})</div>
                <div className="banner-text">{data.convergenceAnalysis.guarantee} (Theoretical bound: {data.convergenceAnalysis.errorBound})</div>
              </div>
            </div>
          )}
        </div>
      )}

      {/* ===== LAS VEGAS ANOMALY FRAUD DETECTOR (Module 6) ===== */}
      {data.statisticalThresholds && (
        <div className="report-section">
          <h3 className="section-title">Statistical Quickselect Anomaly Thresholds (IQR)</h3>
          <div className="metrics-grid">
            <div className="metric-tile">
              <div className="metric-label">Median (50th Percentile)</div>
              <div className="metric-val">₹{Number(data.statisticalThresholds.median).toLocaleString()}</div>
            </div>
            <div className="metric-tile">
              <div className="metric-label">Interquartile Range (IQR)</div>
              <div className="metric-val">₹{Number(data.statisticalThresholds.interquartileRange).toLocaleString()}</div>
            </div>
            <div className="metric-tile">
              <div className="metric-label">Lower Outlier Fence</div>
              <div className="metric-val">₹{Number(data.statisticalThresholds.lowerFence).toLocaleString()}</div>
            </div>
            <div className="metric-tile">
              <div className="metric-label">Upper Outlier Fence</div>
              <div className="metric-val text-red font-semibold">₹{Number(data.statisticalThresholds.upperFence).toLocaleString()}</div>
            </div>
          </div>

          {data.lasVegasGuarantee && (
            <div className="business-summary-banner" style={{ marginTop: '14px' }}>
              <div className="banner-icon">
                <Target size={18} />
              </div>
              <div>
                <div className="banner-heading">Las Vegas Guarantee ({data.lasVegasGuarantee.correctness})</div>
                <div className="banner-text">{data.lasVegasGuarantee.vsMonteCarloComparison}</div>
              </div>
            </div>
          )}

          {data.flaggedTransactions && data.flaggedTransactions.length > 0 && (
            <div style={{ marginTop: '16px' }}>
              <h4 style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)', textTransform: 'uppercase', marginBottom: '8px' }}>
                Flagged Statistical Outliers ({data.flaggedTransactions.length})
              </h4>
              <div className="table-responsive">
                <table className="corporate-table">
                  <thead>
                    <tr>
                      <th>Transaction Reference</th>
                      <th>Transaction Amount</th>
                      <th>Deviation from Median</th>
                      <th>Anomaly Type</th>
                      <th>Risk Score</th>
                    </tr>
                  </thead>
                  <tbody>
                    {data.flaggedTransactions.map((tx, idx) => (
                      <tr key={idx}>
                        <td className="font-mono font-semibold text-blue">TXN-REF-{tx.transactionIndex ?? (idx + 1)}</td>
                        <td className="font-mono font-semibold text-red">₹{Number(tx.amount).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td>
                        <td className="font-mono">{tx.deviationFromMedian}</td>
                        <td><span className="badge badge-red">{tx.anomalyType}</span></td>
                        <td><span className="badge badge-yellow">{tx.riskScore} / 100</span></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>
      )}

      {/* ===== PARALLEL PREFIX SUM & REDUCE (Module 6) ===== */}
      {(data.parallelPrefixSum || data.parallelReduce) && (
        <div className="report-section">
          <h3 className="section-title">Parallel Stream Analytics & Reduction</h3>
          <div className="risk-groups-grid">
            {data.parallelPrefixSum && (
              <div className="risk-group-card risk-border-purple">
                <div className="risk-group-header">
                  <div className="risk-group-title-row">
                    <Activity size={16} className="text-purple" />
                    <span className="risk-group-name">Blelloch Parallel Scan</span>
                  </div>
                  <span className="badge badge-blue">Prefix Sum</span>
                </div>
                <p className="risk-group-meaning">
                  Two-pass parallel scan (Up-sweep Reduce + Down-sweep Distribution). Computes running ledger balances in parallel.
                </p>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '6px', fontSize: '11px', marginTop: '10px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span className="text-muted">Closing Balance:</span>
                    <strong className="font-mono text-green">₹{Number(data.parallelPrefixSum.closingBalance).toLocaleString()}</strong>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span className="text-muted">Algorithm Phases:</span>
                    <span className="font-mono">{data.parallelPrefixSum.phases}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span className="text-muted">Execution Latency:</span>
                    <span className="font-mono">{data.parallelPrefixSum.executionTimeNs?.toLocaleString()} ns</span>
                  </div>
                </div>
              </div>
            )}

            {data.parallelReduce && (
              <div className="risk-group-card risk-border-green">
                <div className="risk-group-header">
                  <div className="risk-group-title-row">
                    <BarChart4 size={16} className="text-green" />
                    <span className="risk-group-name">Parallel Tree Reduction</span>
                  </div>
                  <span className="badge badge-green">Tree Reduce</span>
                </div>
                <p className="risk-group-meaning">
                  Binary tree aggregation over flow arrays. Evaluates associative reduction operators in O(log n) span.
                </p>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '6px', fontSize: '11px', marginTop: '10px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span className="text-muted">Total Net Flow:</span>
                    <strong className="font-mono text-blue">₹{Number(data.parallelReduce.totalNetFlow).toLocaleString()}</strong>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span className="text-muted">Daily Flow Range:</span>
                    <span className="font-mono">₹{data.parallelReduce.minDailyFlow?.toLocaleString()} to ₹{data.parallelReduce.maxDailyFlow?.toLocaleString()}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span className="text-muted">Tree Depth:</span>
                    <span className="font-mono">{data.parallelReduce.reductionLevels} levels</span>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      )}

      {/* ===== WORK-SPAN PIPELINE PARALLELISM (Module 6) ===== */}
      {data.pipelineOperations && data.pipelineOperations.length > 0 && (
        <div className="report-section">
          <h3 className="section-title">Batch Pipeline Work-Span & Parallelism Assessment</h3>
          <div className="table-responsive">
            <table className="corporate-table">
              <thead>
                <tr>
                  <th>Banking Operation</th>
                  <th>Input (n)</th>
                  <th>Work W(n)</th>
                  <th>Span S(n)</th>
                  <th>Parallelism (P=W/S)</th>
                  <th>4 Cores</th>
                  <th>8 Cores</th>
                  <th>16 Cores</th>
                </tr>
              </thead>
              <tbody>
                {data.pipelineOperations.map((op, idx) => {
                  const brent = op.brentProjections || []
                  const p4 = brent.find((b) => b.processors_p === 4)?.speedup || '—'
                  const p8 = brent.find((b) => b.processors_p === 8)?.speedup || '—'
                  const p16 = brent.find((b) => b.processors_p === 16)?.speedup || '—'
                  return (
                    <tr key={idx}>
                      <td>
                        <div className="font-semibold text-main">{op.operation}</div>
                        <div style={{ fontSize: '10px', color: 'var(--text-muted)' }}>{op.algorithm}</div>
                      </td>
                      <td className="font-mono">{Number(op.inputSize).toLocaleString()}</td>
                      <td className="font-mono">{typeof op.work_W === 'number' ? op.work_W.toLocaleString() : op.work_W}</td>
                      <td className="font-mono">{typeof op.span_S === 'number' ? op.span_S.toLocaleString() : op.span_S}</td>
                      <td><span className="badge badge-purple">{op.parallelism_P}x</span></td>
                      <td className="font-mono text-green">{p4}</td>
                      <td className="font-mono text-green">{p8}</td>
                      <td className="font-mono text-green">{p16}</td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>

          {data.endToEndPipeline && (
            <div className="optimal-card" style={{ marginTop: '16px' }}>
              <div className="optimal-stats">
                <div>
                  <span className="opt-label">End-to-End Pipeline Work</span>
                  <span className="opt-val font-mono">{data.endToEndPipeline.totalWork}</span>
                </div>
                <div>
                  <span className="opt-label">Critical Path Span</span>
                  <span className="opt-val font-mono text-purple">{data.endToEndPipeline.totalSpan}</span>
                </div>
                <div>
                  <span className="opt-label">Overall Parallelism</span>
                  <span className="opt-val text-blue font-semibold">{data.endToEndPipeline.overallParallelism}</span>
                </div>
              </div>
            </div>
          )}

          {data.theoreticalFramework && (
            <div className="business-summary-banner" style={{ marginTop: '16px' }}>
              <div className="banner-icon">
                <Cpu size={18} />
              </div>
              <div>
                <div className="banner-heading">Theoretical Framework: Brent&apos;s Theorem</div>
                <div className="banner-text">
                  {data.theoreticalFramework.brentsTheorem} · {data.theoreticalFramework.schedulingStrategy}
                </div>
              </div>
            </div>
          )}
        </div>
      )}

      {/* ===== REMAINING OPERATIONAL METRICS (Clean Key-Value) ===== */}
      {Object.entries(data).filter(([k]) => !handledKeys.has(k)).length > 0 && (
        <div className="report-section">
          <h3 className="section-title">Additional Operational Parameters</h3>
          <div className="clean-kv-list">
            {Object.entries(data)
              .filter(([k]) => !handledKeys.has(k))
              .map(([k, v]) => (
                <div key={k} className="clean-kv-row">
                  <span className="clean-kv-label">{formatKeyLabel(k)}</span>
                  <span className="clean-kv-value">{renderValue(v)}</span>
                </div>
              ))}
          </div>
        </div>
      )}
    </div>
  )
}

function formatKeyLabel(key) {
  return key
    .replace(/([A-Z])/g, ' $1')
    .replace(/_/g, ' ')
    .replace(/^./, (str) => str.toUpperCase())
    .trim()
}

function renderValue(val) {
  if (val === null || val === undefined) return '—'
  if (typeof val === 'boolean') return val ? '✓ Verified' : '✗ Unverified'
  if (typeof val === 'number') {
    return Number.isInteger(val) ? val.toLocaleString() : val.toFixed(4)
  }
  if (Array.isArray(val)) {
    if (val.length === 0) return 'None'
    if (typeof val[0] === 'string' || typeof val[0] === 'number') {
      return val.join(', ')
    }
    return `${val.length} items`
  }
  if (typeof val === 'object') {
    return Object.entries(val).map(([k, v]) => `${k}: ${v}`).join(' · ')
  }
  return String(val)
}

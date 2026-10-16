import { useState, useEffect, useRef } from 'react'
import {
  Database,
  Upload,
  CheckCircle2,
  AlertTriangle,
  RefreshCw,
  FileText,
  BarChart3,
  ArrowRight,
  Shield,
  Globe,
  ShoppingCart,
  Layers,
  TrendingUp,
  Activity,
  Loader2,
  X,
  ChevronDown,
  ChevronUp,
  FileUp,
  Sparkles,
  Download,
  Zap,
  Search,
  Filter,
  UserCheck,
  Building,
  FileCheck2,
  ArrowUpDown,
  ChevronLeft,
  ChevronRight,
  FileSpreadsheet,
} from 'lucide-react'

const KAGGLE_SAMPLE_CSV = `TransactionID,CustomerID,TransactionAmount,TransactionType,Channel,Location,MerchantID,IsFraud,Timestamp
KAG-TXN-1001,CUST-7721,48500.00,Transfer,Mobile,Mumbai,MERCH-IND-01,0,2024-03-15 10:14:22
KAG-TXN-1002,CUST-9932,1250000.00,Wire,Branch,Cayman Islands,MERCH-OFF-99,1,2024-03-15 11:20:05
KAG-TXN-1003,CUST-3312,2400.50,Card,POS,Bengaluru,MERCH-RET-12,0,2024-03-15 12:45:10
KAG-TXN-1004,CUST-5541,890000.00,Internal,Online,Panama City,MERCH-OFF-44,1,2024-03-15 14:02:18
KAG-TXN-1005,CUST-1120,15400.00,UPI,Mobile,New Delhi,MERCH-UPI-05,0,2024-03-15 15:30:45
KAG-TXN-1006,CUST-8834,950000.00,Wire,Online,Zurich,MERCH-SWISS-02,1,2024-03-15 16:15:30
KAG-TXN-1007,CUST-2290,45000.00,NEFT,Online,Hyderabad,MERCH-TECH-08,0,2024-03-15 17:05:12
KAG-TXN-1008,CUST-6643,185000.00,RTGS,Branch,Chennai,MERCH-CORP-19,0,2024-03-15 18:22:00
KAG-TXN-1009,CUST-4411,495000.00,Transfer,Online,Dubai,MERCH-GULF-03,1,2024-03-15 19:40:15
KAG-TXN-1010,CUST-7755,3200.00,Card,ATM,Mumbai,MERCH-ATM-01,0,2024-03-15 20:10:30
KAG-TXN-1011,CUST-9921,780000.00,SWIFT,Branch,Singapore,MERCH-ASIA-14,0,2024-03-15 21:05:40
KAG-TXN-1012,CUST-1044,499000.00,Internal,Mobile,British Virgin Islands,MERCH-SHELL-01,1,2024-03-15 22:15:00
KAG-TXN-1013,CUST-5532,12000.00,UPI,Mobile,Pune,MERCH-FOOD-22,0,2024-03-15 22:45:18
KAG-TXN-1014,CUST-3882,650000.00,Wire,Online,London,MERCH-UK-07,0,2024-03-15 23:10:55
KAG-TXN-1015,CUST-9012,980000.00,Transfer,Mobile,Hong Kong,MERCH-HK-11,1,2024-03-16 01:25:33`

const presetIcons = {
  default: <Database size={22} className="text-blue" />,
  'bank-transactions-2': <Database size={22} className="text-blue" />,
  'aml-suspicious': <Shield size={22} className="text-red" />,
  'swift-forex': <Globe size={22} className="text-purple" />,
  'retail-highvol': <ShoppingCart size={22} className="text-green" />,
  'synthetic-100': <Layers size={22} className="text-blue" />,
}

const presetAccentClasses = {
  default: 'preset-accent-blue',
  'bank-transactions-2': 'preset-accent-blue',
  'aml-suspicious': 'preset-accent-red',
  'swift-forex': 'preset-accent-purple',
  'retail-highvol': 'preset-accent-green',
  'synthetic-100': 'preset-accent-blue',
}

export default function DatasetPage() {
  const [activeTab, setActiveTab] = useState('overview') // 'overview', 'ledger', 'accounts', 'documents', 'upload'
  const [presets, setPresets] = useState([])
  const [activeDataset, setActiveDataset] = useState(null)
  const [loading, setLoading] = useState(true)
  const [switching, setSwitching] = useState(null)

  // Transaction Ledger state
  const [transactions, setTransactions] = useState([])
  const [txTotal, setTxTotal] = useState(0)
  const [txPage, setTxPage] = useState(1)
  const [txLimit, setTxLimit] = useState(25)
  const [txTotalPages, setTxTotalPages] = useState(1)
  const [txSearch, setTxSearch] = useState('')
  const [txTypeFilter, setTxTypeFilter] = useState('ALL')
  const [txStatusFilter, setTxStatusFilter] = useState('ALL')
  const [txLoading, setTxLoading] = useState(false)

  // Accounts & Documents
  const [accounts, setAccounts] = useState([])
  const [accountsLoading, setAccountsLoading] = useState(false)
  const [documents, setDocuments] = useState([])
  const [documentsLoading, setDocumentsLoading] = useState(false)

  // Upload state
  const [uploadDrag, setUploadDrag] = useState(false)
  const [uploadStatus, setUploadStatus] = useState(null)
  const [uploading, setUploading] = useState(false)
  const fileInputRef = useRef(null)

  useEffect(() => {
    loadOverview()
  }, [])

  useEffect(() => {
    if (activeTab === 'ledger') {
      loadTransactions(1)
    } else if (activeTab === 'accounts') {
      loadAccounts()
    } else if (activeTab === 'documents') {
      loadDocuments()
    }
  }, [activeTab, txTypeFilter, txStatusFilter])

  const loadOverview = async () => {
    setLoading(true)
    try {
      const [presetsRes, activeRes] = await Promise.all([
        fetch('/api/dataset/presets'),
        fetch('/api/dataset/active'),
      ])
      const presetsData = await presetsRes.json()
      const activeData = await activeRes.json()
      setPresets(presetsData.presets || [])
      setActiveDataset(activeData)
    } catch (e) {
      console.error('Failed to load dataset info:', e)
    } finally {
      setLoading(false)
    }
  }

  const loadTransactions = async (page = txPage) => {
    setTxLoading(true)
    try {
      const query = new URLSearchParams({
        page: String(page),
        limit: String(txLimit),
        search: txSearch,
        type: txTypeFilter,
        status: txStatusFilter,
      })
      const res = await fetch(`/api/dataset/transactions?${query}`)
      const data = await res.json()
      setTransactions(data.transactions || [])
      setTxTotal(data.total || 0)
      setTxPage(data.page || 1)
      setTxTotalPages(data.totalPages || 1)
    } catch (e) {
      console.error('Failed to load transactions:', e)
    } finally {
      setTxLoading(false)
    }
  }

  const loadAccounts = async () => {
    setAccountsLoading(true)
    try {
      const res = await fetch('/api/dataset/accounts')
      const data = await res.json()
      setAccounts(data.accounts || [])
    } catch (e) {
      console.error('Failed to load accounts:', e)
    } finally {
      setAccountsLoading(false)
    }
  }

  const loadDocuments = async () => {
    setDocumentsLoading(true)
    try {
      const res = await fetch('/api/dataset/documents')
      const data = await res.json()
      setDocuments(data.documents || [])
    } catch (e) {
      console.error('Failed to load documents:', e)
    } finally {
      setDocumentsLoading(false)
    }
  }

  const handleSwitchPreset = async (presetId) => {
    if (switching) return
    setSwitching(presetId)
    setUploadStatus(null)
    try {
      const res = await fetch('/api/dataset/switch', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ presetId }),
      })
      const data = await res.json()
      if (data.success) {
        setActiveDataset(data.dataset)
        setUploadStatus({ type: 'success', message: `Switched to ${data.dataset.datasetName} — all 6 modules are now analyzing this data.` })
        // If on ledger, reload
        if (activeTab === 'ledger') loadTransactions(1)
        if (activeTab === 'accounts') loadAccounts()
        if (activeTab === 'documents') loadDocuments()
      }
    } catch (e) {
      setUploadStatus({ type: 'error', message: 'Failed to switch dataset' })
    } finally {
      setSwitching(null)
    }
  }

  const handleFileUpload = async (file) => {
    if (!file) return
    setUploading(true)
    setUploadStatus(null)

    try {
      const text = await file.text()
      const isJson = file.name.endsWith('.json')
      const datasetName = file.name.replace(/\.(csv|json)$/i, '')

      let body
      if (isJson) {
        body = text
      } else {
        body = JSON.stringify({ name: datasetName, data: text })
      }

      const res = await fetch('/api/dataset/upload', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body,
      })
      const data = await res.json()
      if (data.success) {
        setActiveDataset(data.dataset)
        setUploadStatus({ type: 'success', message: data.message })
        if (activeTab === 'ledger') loadTransactions(1)
        if (activeTab === 'accounts') loadAccounts()
        if (activeTab === 'documents') loadDocuments()
      } else {
        setUploadStatus({ type: 'error', message: data.error || 'Upload failed' })
      }
    } catch (e) {
      setUploadStatus({ type: 'error', message: 'Failed to upload file: ' + e.message })
    } finally {
      setUploading(false)
    }
  }

  const handleDrop = (e) => {
    e.preventDefault()
    setUploadDrag(false)
    const file = e.dataTransfer?.files?.[0]
    if (file && (file.name.endsWith('.csv') || file.name.endsWith('.json'))) {
      handleFileUpload(file)
    } else {
      setUploadStatus({ type: 'error', message: 'Please upload a .csv or .json file' })
    }
  }

  const handleFileSelect = (e) => {
    const file = e.target.files?.[0]
    if (file) handleFileUpload(file)
    e.target.value = ''
  }

  const handleLoadKaggleSample = async () => {
    setUploading(true)
    setUploadStatus(null)
    try {
      const res = await fetch('/api/dataset/upload', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: 'Kaggle Fraud Detection Sample (15 txns)',
          data: KAGGLE_SAMPLE_CSV,
        }),
      })
      const data = await res.json()
      if (data.success) {
        setActiveDataset(data.dataset)
        setUploadStatus({
          type: 'success',
          message: 'Loaded Kaggle Fraud Detection sample dataset! All 6 algorithm modules are now running against this data.',
        })
        if (activeTab === 'ledger') loadTransactions(1)
        if (activeTab === 'accounts') loadAccounts()
        if (activeTab === 'documents') loadDocuments()
      } else {
        setUploadStatus({ type: 'error', message: data.error || 'Failed to load sample' })
      }
    } catch (e) {
      setUploadStatus({ type: 'error', message: 'Failed to load sample: ' + e.message })
    } finally {
      setUploading(false)
    }
  }

  const handleDownloadSampleCsv = () => {
    const blob = new Blob([KAGGLE_SAMPLE_CSV], { type: 'text/csv;charset=utf-8;' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', 'kaggle_bank_transactions_sample.csv')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }

  const handleExportActiveDataset = (format = 'csv') => {
    window.open(`/api/dataset/export?format=${format}`, '_blank')
  }

  if (loading) {
    return (
      <div className="dataset-page">
        <div className="dataset-loading">
          <Loader2 size={32} className="spin" />
          <span>Loading FinServe Dataset Engine...</span>
        </div>
      </div>
    )
  }

  const stats = activeDataset?.statistics

  return (
    <div className="dataset-page">
      {/* ===== Header & Global Actions ===== */}
      <div className="dataset-page-header">
        <div className="dataset-header-info">
          <span className="dataset-category-badge">ENTERPRISE DATA ENGINE</span>
          <h1 className="dataset-page-title">Dataset Manager & Kaggle Ingestion</h1>
          <p className="dataset-page-subtitle">
            Curate, switch, inspect, and upload real-world banking datasets. Every algorithm across
            all 6 modules (KMP, Suffix Arrays, Max-Flow, SAT Solver, Monte Carlo, Parallel Prefix Sum)
            dynamically executes against the currently active dataset.
          </p>
        </div>
        <div className="dataset-top-actions">
          <button
            className="dataset-action-btn primary"
            onClick={handleLoadKaggleSample}
            disabled={uploading}
            title="Instant 1-Click Load Kaggle Sample"
          >
            <Zap size={14} />
            <span>Load Kaggle Sample</span>
          </button>
          <button
            className="dataset-action-btn"
            onClick={() => handleExportActiveDataset('csv')}
            title="Export Active Dataset as CSV"
          >
            <FileSpreadsheet size={14} />
            <span>Export CSV</span>
          </button>
          <button
            className="dataset-refresh-btn"
            onClick={loadOverview}
            title="Refresh Dataset Engine"
          >
            <RefreshCw size={15} />
          </button>
        </div>
      </div>

      {/* ===== Feedback Notification ===== */}
      {uploadStatus && (
        <div className={`dataset-toast ${uploadStatus.type}`}>
          {uploadStatus.type === 'success' ? (
            <CheckCircle2 size={16} />
          ) : (
            <AlertTriangle size={16} />
          )}
          <span>{uploadStatus.message}</span>
          <button className="toast-dismiss" onClick={() => setUploadStatus(null)}>
            <X size={14} />
          </button>
        </div>
      )}

      {/* ===== Active Dataset Banner ===== */}
      {activeDataset && (
        <div className="active-dataset-card">
          <div className="active-dataset-header">
            <div className="active-badge-row">
              <span className="active-dot" />
              <span className="active-label">CURRENT ACTIVE DATASET</span>
            </div>
            <h2 className="active-dataset-name">{activeDataset.datasetName}</h2>
            <p className="active-dataset-desc">{activeDataset.description}</p>
          </div>

          <div className="active-dataset-metrics">
            <div className="metric-card">
              <Activity size={18} className="metric-icon" />
              <div className="metric-value">{activeDataset.totalTransactions?.toLocaleString()}</div>
              <div className="metric-label">Transactions</div>
            </div>
            <div className="metric-card">
              <Database size={18} className="metric-icon" />
              <div className="metric-value">{activeDataset.totalAccounts?.toLocaleString()}</div>
              <div className="metric-label">Accounts Indexed</div>
            </div>
            <div className="metric-card">
              <FileText size={18} className="metric-icon" />
              <div className="metric-value">{activeDataset.totalDocuments?.toLocaleString()}</div>
              <div className="metric-label">Audit Filings</div>
            </div>
            {stats && (
              <>
                <div className="metric-card">
                  <TrendingUp size={18} className="metric-icon" />
                  <div className="metric-value">
                    {stats.totalVolume >= 10000000
                      ? `₹${(stats.totalVolume / 10000000).toFixed(1)} Cr`
                      : stats.totalVolume >= 100000
                      ? `₹${(stats.totalVolume / 100000).toFixed(1)} L`
                      : `₹${stats.totalVolume.toLocaleString()}`}
                  </div>
                  <div className="metric-label">Gross Volume</div>
                </div>
                <div className="metric-card">
                  <BarChart3 size={18} className="metric-icon" />
                  <div className="metric-value">
                    ₹{stats.averageAmount?.toLocaleString(undefined, { maximumFractionDigits: 0 })}
                  </div>
                  <div className="metric-label">Avg Transaction</div>
                </div>
              </>
            )}
          </div>
        </div>
      )}

      {/* ===== Navigation Tabs ===== */}
      <div className="dataset-nav-tabs">
        <button
          className={`dataset-tab-btn ${activeTab === 'overview' ? 'active' : ''}`}
          onClick={() => setActiveTab('overview')}
        >
          <Layers size={16} />
          <span>Presets & Overview</span>
        </button>
        <button
          className={`dataset-tab-btn ${activeTab === 'ledger' ? 'active' : ''}`}
          onClick={() => setActiveTab('ledger')}
        >
          <Activity size={16} />
          <span>Live Transaction Ledger</span>
          {activeDataset && (
            <span className="tab-pill">{activeDataset.totalTransactions}</span>
          )}
        </button>
        <button
          className={`dataset-tab-btn ${activeTab === 'accounts' ? 'active' : ''}`}
          onClick={() => setActiveTab('accounts')}
        >
          <Building size={16} />
          <span>Accounts & Entities</span>
          {activeDataset && (
            <span className="tab-pill">{activeDataset.totalAccounts}</span>
          )}
        </button>
        <button
          className={`dataset-tab-btn ${activeTab === 'documents' ? 'active' : ''}`}
          onClick={() => setActiveTab('documents')}
        >
          <FileCheck2 size={16} />
          <span>Regulatory Dossiers</span>
          {activeDataset && (
            <span className="tab-pill">{activeDataset.totalDocuments}</span>
          )}
        </button>
        <button
          className={`dataset-tab-btn ${activeTab === 'upload' ? 'active' : ''}`}
          onClick={() => setActiveTab('upload')}
        >
          <Upload size={16} />
          <span>Import Custom / Kaggle CSV</span>
        </button>
      </div>

      {/* ===== TAB 1: OVERVIEW & PRESETS ===== */}
      {activeTab === 'overview' && (
        <div className="tab-pane-content">
          <div className="dataset-section">
            <h3 className="dataset-section-title">
              <Sparkles size={18} />
              <span>Curated Banking Datasets</span>
            </h3>
            <p className="dataset-section-desc">
              Select any institutional dataset below to instantly reload the platform ledger and re-run all algorithms.
            </p>

            <div className="preset-grid">
              {presets.map((preset) => {
                const isActive = activeDataset?.datasetId === preset.id
                const isSwitching = switching === preset.id
                return (
                  <div
                    key={preset.id}
                    className={`preset-card ${presetAccentClasses[preset.id] || ''} ${
                      isActive ? 'is-active' : ''
                    }`}
                  >
                    <div className="preset-card-header">
                      <div className="preset-icon-wrap">
                        {presetIcons[preset.id] || <Database size={22} />}
                      </div>
                      {isActive && (
                        <span className="preset-active-badge">
                          <CheckCircle2 size={12} /> Active
                        </span>
                      )}
                    </div>
                    <h4 className="preset-name">{preset.name}</h4>
                    <p className="preset-desc">{preset.description}</p>
                    <div className="preset-stats-row">
                      <span>{preset.transactions} txns</span>
                      <span>{preset.accounts} accounts</span>
                      <span>{preset.documents} dossiers</span>
                    </div>
                    {preset.tags && (
                      <div className="preset-tags">
                        {preset.tags.map((tag) => (
                          <span key={tag} className="preset-tag">
                            {tag}
                          </span>
                        ))}
                      </div>
                    )}
                    <button
                      className={`preset-switch-btn ${isActive ? 'active' : ''}`}
                      onClick={() => handleSwitchPreset(preset.id)}
                      disabled={isActive || !!switching}
                    >
                      {isSwitching ? (
                        <>
                          <Loader2 size={14} className="spin" /> Switching...
                        </>
                      ) : isActive ? (
                        <>
                          <CheckCircle2 size={14} /> Currently Active
                        </>
                      ) : (
                        <>
                          <ArrowRight size={14} /> Switch to this Dataset
                        </>
                      )}
                    </button>
                  </div>
                )
              })}
            </div>
          </div>

          {/* Statistical Distribution breakdown */}
          {stats && (
            <div className="dataset-section">
              <h3 className="dataset-section-title">
                <BarChart3 size={18} />
                <span>Dataset Topology & Distributions</span>
              </h3>
              <div className="stats-breakdown-panel visible">
                {stats.byType && (
                  <div className="breakdown-group">
                    <h4>Volume by Payment Channel</h4>
                    <div className="chips-wrap">
                      {Object.entries(stats.byType).map(([type, count]) => (
                        <div key={type} className="breakdown-chip">
                          <span className="chip-label">{type}</span>
                          <span className="chip-count">{count}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
                {stats.byStatus && (
                  <div className="breakdown-group">
                    <h4>Execution & Risk Status</h4>
                    <div className="chips-wrap">
                      {Object.entries(stats.byStatus).map(([status, count]) => (
                        <div key={status} className="breakdown-chip">
                          <span className="chip-label">{status}</span>
                          <span className={`chip-count ${status === 'FLAGGED' ? 'warn' : status === 'REJECTED' ? 'err' : ''}`}>
                            {count}
                          </span>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
                {stats.byCurrency && (
                  <div className="breakdown-group">
                    <h4>Currency Corridors</h4>
                    <div className="chips-wrap">
                      {Object.entries(stats.byCurrency).map(([ccy, count]) => (
                        <div key={ccy} className="breakdown-chip">
                          <span className="chip-label">{ccy}</span>
                          <span className="chip-count">{count}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            </div>
          )}
        </div>
      )}

      {/* ===== TAB 2: LIVE TRANSACTION LEDGER ===== */}
      {activeTab === 'ledger' && (
        <div className="tab-pane-content">
          <div className="ledger-controls-card">
            <div className="ledger-search-box">
              <Search size={16} className="search-icon" />
              <input
                type="text"
                placeholder="Search transactions by ID, account, description, or code..."
                value={txSearch}
                onChange={(e) => setTxSearch(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && loadTransactions(1)}
              />
              <button
                className="btn-filter-apply"
                onClick={() => loadTransactions(1)}
              >
                Search
              </button>
            </div>

            <div className="ledger-filters-row">
              <div className="filter-select-wrap">
                <label>Channel:</label>
                <select
                  value={txTypeFilter}
                  onChange={(e) => setTxTypeFilter(e.target.value)}
                >
                  <option value="ALL">All Channels</option>
                  <option value="WIRE">WIRE</option>
                  <option value="UPI">UPI</option>
                  <option value="SWIFT">SWIFT</option>
                  <option value="CARD">CARD</option>
                  <option value="RTGS">RTGS</option>
                  <option value="NEFT">NEFT</option>
                  <option value="ACH">ACH</option>
                  <option value="INTERNAL">INTERNAL</option>
                </select>
              </div>

              <div className="filter-select-wrap">
                <label>Status:</label>
                <select
                  value={txStatusFilter}
                  onChange={(e) => setTxStatusFilter(e.target.value)}
                >
                  <option value="ALL">All Statuses</option>
                  <option value="COMPLETED">COMPLETED</option>
                  <option value="FLAGGED">FLAGGED (High Risk)</option>
                  <option value="REJECTED">REJECTED</option>
                  <option value="PENDING">PENDING</option>
                </select>
              </div>

              <div className="ledger-count-badge">
                Showing <strong>{transactions.length}</strong> of <strong>{txTotal}</strong> records
              </div>
            </div>
          </div>

          <div className="sample-table-wrapper">
            {txLoading ? (
              <div className="table-loading-overlay">
                <Loader2 size={24} className="spin" />
                <span>Loading transaction records...</span>
              </div>
            ) : transactions.length === 0 ? (
              <div className="table-empty-state">
                <AlertTriangle size={24} />
                <span>No transactions match the selected filters.</span>
              </div>
            ) : (
              <table className="sample-table">
                <thead>
                  <tr>
                    <th>Transaction ID</th>
                    <th>Sender</th>
                    <th>Beneficiary</th>
                    <th>Amount</th>
                    <th>Channel</th>
                    <th>Status</th>
                    <th>Currency</th>
                    <th>Narration</th>
                    <th>Timestamp</th>
                  </tr>
                </thead>
                <tbody>
                  {transactions.map((tx) => (
                    <tr key={tx.transactionId}>
                      <td className="mono">{tx.transactionId}</td>
                      <td className="mono muted">{tx.senderAccount}</td>
                      <td className="mono muted">{tx.receiverAccount}</td>
                      <td className="mono amount-cell">
                        {tx.amount?.toLocaleString(undefined, {
                          minimumFractionDigits: 2,
                          maximumFractionDigits: 2,
                        })}
                      </td>
                      <td>
                        <span className="type-badge">{tx.transactionType}</span>
                      </td>
                      <td>
                        <span
                          className={`status-badge ${
                            tx.status === 'FLAGGED'
                              ? 'sb-warn'
                              : tx.status === 'REJECTED'
                              ? 'sb-err'
                              : tx.status === 'COMPLETED'
                              ? 'sb-ok'
                              : 'sb-pending'
                          }`}
                        >
                          {tx.status}
                        </span>
                      </td>
                      <td className="mono">{tx.currency}</td>
                      <td className="desc-cell" title={tx.description}>
                        {tx.description}
                      </td>
                      <td className="mono muted timestamp-cell">
                        {tx.timestamp ? tx.timestamp.replace('T', ' ') : ''}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>

          {/* Pagination Controls */}
          {txTotalPages > 1 && (
            <div className="ledger-pagination">
              <button
                className="page-btn"
                disabled={txPage <= 1}
                onClick={() => loadTransactions(txPage - 1)}
              >
                <ChevronLeft size={16} /> Previous
              </button>
              <span className="page-indicator">
                Page {txPage} of {txTotalPages}
              </span>
              <button
                className="page-btn"
                disabled={txPage >= txTotalPages}
                onClick={() => loadTransactions(txPage + 1)}
              >
                Next <ChevronRight size={16} />
              </button>
            </div>
          )}
        </div>
      )}

      {/* ===== TAB 3: ACCOUNTS & ENTITIES ===== */}
      {activeTab === 'accounts' && (
        <div className="tab-pane-content">
          <div className="dataset-section">
            <h3 className="dataset-section-title">
              <Building size={18} />
              <span>Registered Accounts & Banking Topologies</span>
            </h3>
            <p className="dataset-section-desc">
              All financial accounts connected to the active dataset. Synthesized accounts contain realistic
              balances, branch locations, and IFSC routing codes used for settlement flow analysis.
            </p>

            {accountsLoading ? (
              <div className="dataset-loading">
                <Loader2 size={24} className="spin" />
                <span>Loading bank accounts...</span>
              </div>
            ) : (
              <div className="accounts-grid">
                {accounts.map((acc) => (
                  <div key={acc.accountId} className="account-card">
                    <div className="account-card-header">
                      <span className="account-id mono">{acc.accountId}</span>
                      <span className="account-type-badge">{acc.accountType}</span>
                    </div>
                    <h4 className="account-holder">{acc.holderName}</h4>
                    <div className="account-balance-row">
                      <span className="acc-bal-label">Available Balance</span>
                      <span className="acc-bal-value mono">
                        ₹{acc.balance?.toLocaleString(undefined, { minimumFractionDigits: 2 })}
                      </span>
                    </div>
                    <div className="account-meta-row">
                      <span><strong>Branch:</strong> {acc.branch}</span>
                      <span><strong>IFSC:</strong> {acc.ifscCode}</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* ===== TAB 4: REGULATORY DOSSIERS ===== */}
      {activeTab === 'documents' && (
        <div className="tab-pane-content">
          <div className="dataset-section">
            <h3 className="dataset-section-title">
              <FileCheck2 size={18} />
              <span>Compliance Filings & Regulatory Audit Reports</span>
            </h3>
            <p className="dataset-section-desc">
              Unstructured financial texts and supervisory filings referenced by Module 2 (Suffix Indexing)
              and Module 5 (Constraint Satisfaction).
            </p>

            {documentsLoading ? (
              <div className="dataset-loading">
                <Loader2 size={24} className="spin" />
                <span>Loading compliance documents...</span>
              </div>
            ) : (
              <div className="dossiers-list">
                {documents.map((doc) => (
                  <div key={doc.documentId} className="dossier-card">
                    <div className="dossier-header">
                      <div className="dossier-id-box">
                        <FileText size={18} className="text-blue" />
                        <span className="mono">{doc.documentId}</span>
                      </div>
                      <span className="dossier-type-badge">{doc.documentType}</span>
                    </div>
                    <h4 className="dossier-title">{doc.title}</h4>
                    <p className="dossier-content">{doc.content}</p>
                    <div className="dossier-footer">
                      <span><strong>Author:</strong> {doc.author}</span>
                      <span><strong>Date Filed:</strong> {doc.date}</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* ===== TAB 5: IMPORT / KAGGLE INGESTION ===== */}
      {activeTab === 'upload' && (
        <div className="tab-pane-content">
          <div className="dataset-section">
            <h3 className="dataset-section-title">
              <Upload size={18} />
              <span>Upload Custom Dataset / Kaggle CSV</span>
            </h3>
            <p className="dataset-section-desc">
              Upload any banking or fraud detection dataset in CSV or JSON format. FinServe's intelligent
              column normalizer maps fields dynamically and automatically synthesizes bank account topologies.
            </p>

            <div
              className={`upload-dropzone ${uploadDrag ? 'dragging' : ''} ${
                uploading ? 'uploading' : ''
              }`}
              onDragOver={(e) => {
                e.preventDefault()
                setUploadDrag(true)
              }}
              onDragLeave={() => setUploadDrag(false)}
              onDrop={handleDrop}
              onClick={() => !uploading && fileInputRef.current?.click()}
            >
              <input
                ref={fileInputRef}
                type="file"
                accept=".csv,.json"
                style={{ display: 'none' }}
                onChange={handleFileSelect}
              />
              {uploading ? (
                <div className="upload-progress">
                  <Loader2 size={36} className="spin" />
                  <span>Ingesting and synthesizing dataset entities...</span>
                </div>
              ) : (
                <div className="upload-content">
                  <FileUp size={44} className="upload-icon" />
                  <span className="upload-title">Drop CSV or JSON dataset here</span>
                  <span className="upload-hint">or click to browse local files</span>
                  <div className="upload-format-info">
                    <span className="format-badge">CSV</span>
                    <span className="format-badge">JSON</span>
                    <span className="format-note">
                      Compatible with Kaggle Bank Fraud, Paysim, Credit Card Fraud schemas
                    </span>
                  </div>
                </div>
              )}
            </div>

            {/* Quick Ingestion Buttons */}
            <div className="dataset-quick-actions">
              <button
                className="btn-quick-sample primary-gradient"
                onClick={handleLoadKaggleSample}
                disabled={uploading}
              >
                <Zap size={15} />
                <span>Load Kaggle Bank Fraud Sample Dataset</span>
              </button>
              <button
                className="btn-quick-sample outline-btn"
                onClick={handleDownloadSampleCsv}
              >
                <Download size={15} />
                <span>Download Sample CSV Template</span>
              </button>
            </div>

            {/* CSV Schema Reference */}
            <div className="schema-reference">
              <h4>Supported CSV & JSON Column Schema Mappings</h4>
              <div className="schema-grid">
                <div className="schema-col">
                  <span className="schema-key">TransactionID</span>
                  <span className="schema-alt">transaction_id, id, txnid, transid</span>
                </div>
                <div className="schema-col">
                  <span className="schema-key">SenderAccount</span>
                  <span className="schema-alt">customer_id, sender, account_id, from_account</span>
                </div>
                <div className="schema-col">
                  <span className="schema-key">ReceiverAccount</span>
                  <span className="schema-alt">merchant_id, receiver, recipient_id, to_account</span>
                </div>
                <div className="schema-col">
                  <span className="schema-key">Amount</span>
                  <span className="schema-alt">transaction_amount, value, amt, transfer_amount</span>
                </div>
                <div className="schema-col">
                  <span className="schema-key">TransactionType</span>
                  <span className="schema-alt">channel, type, payment_method, method</span>
                </div>
                <div className="schema-col">
                  <span className="schema-key">Status / Fraud Flag</span>
                  <span className="schema-alt">is_fraud (0/1), status, fraud, flagged</span>
                </div>
                <div className="schema-col">
                  <span className="schema-key">Description</span>
                  <span className="schema-alt">narration, desc, memo, location, merchant</span>
                </div>
                <div className="schema-col">
                  <span className="schema-key">Timestamp</span>
                  <span className="schema-alt">date, datetime, timestamp, time</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

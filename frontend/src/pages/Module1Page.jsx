import ModuleLayout from '../components/ModuleLayout'
import { Search, Activity, ShieldAlert, CheckCircle } from 'lucide-react'

const module1Tools = [
  {
    id: 'search-code',
    name: 'Payment Reference & Code Lookup',
    shortDesc: 'Locate transactions by payment reference, wire code, or memo',
    fullDesc:
      'Instantly scan across banking transaction records to locate entries matching specific payment tokens, internal ledger references, or wire transfer markers.',
    endpoint: '/module1/search-code',
    icon: <Search size={20} className="text-blue" />,
    buttonText: 'Search Transactions',
    fields: [
      {
        key: 'query',
        label: 'Payment Reference or Transaction Code',
        placeholder: 'e.g. PAY-2024, wire, transfer, SWIFT',
        default: 'PAY-2024',
        help: 'Enter full or partial reference token to scan historical transaction records.',
      },
    ],
  },
  {
    id: 'analyze-patterns',
    name: 'Recurring Payment Pattern Detector',
    shortDesc: 'Identify recurring subscription and standing billing patterns',
    fullDesc:
      'Analyzes transaction memos and descriptions to surface recurring payments, periodic standing instructions, and automated subscription debits.',
    endpoint: '/module1/analyze-patterns',
    icon: <Activity size={20} className="text-purple" />,
    buttonText: 'Detect Recurring Patterns',
    fields: [
      {
        key: 'query',
        label: 'Transaction Description Keyword',
        placeholder: 'e.g. payment, transfer, payroll, bill',
        default: 'payment',
        help: 'Prefix or descriptor used to group and identify periodic transactions.',
      },
    ],
  },
  {
    id: 'lookup-code',
    name: 'Bank Identifier & Routing Verifier',
    shortDesc: 'Verify SWIFT, IFSC, and interbank clearing tokens',
    fullDesc:
      'Performs high-speed verification of bank routing codes, international SWIFT BIC identifiers, and national clearing network credentials.',
    endpoint: '/module1/lookup-code',
    icon: <CheckCircle size={20} className="text-green" />,
    buttonText: 'Verify Clearing Identifier',
    fields: [
      {
        key: 'query',
        label: 'Network Clearing Identifier',
        placeholder: 'e.g. SWIFT, NEFT, IFSC, ACH',
        default: 'SWIFT',
        help: 'Banking network token or routing prefix to authenticate against active records.',
      },
    ],
  },
  {
    id: 'detect-fraud',
    name: 'Anti-Money Laundering Watchlist Scanner',
    shortDesc: 'Multi-indicator screening for suspicious transaction activity',
    fullDesc:
      'Simultaneously cross-examines transaction records against comprehensive anti-money laundering watchlists, shell corporation flags, and regulatory sanction triggers.',
    endpoint: '/module1/detect-fraud',
    icon: <ShieldAlert size={20} className="text-red" />,
    buttonText: 'Execute Watchlist Screening',
    fields: [
      {
        key: 'patterns',
        label: 'Watchlist Flag Terms (comma-separated)',
        placeholder: 'e.g. shell company, money laundering, structuring, offshore',
        default: 'shell company, money laundering, structuring, offshore',
        help: 'Provide specific watchlist terms to screen across all current banking transactions.',
      },
    ],
  },
]

export default function Module1Page() {
  return (
    <ModuleLayout
      moduleTitle="Transaction Search & Screening"
      moduleCategory="TRANSACTION OPERATIONS"
      moduleDescription="High-speed transaction code lookup, recurring payment detection, and multi-indicator fraud screening."
      tools={module1Tools}
    />
  )
}

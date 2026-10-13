import ModuleLayout from '../components/ModuleLayout'
import { SpellCheck, ShieldAlert, PieChart, Workflow } from 'lucide-react'

const module3Tools = [
  {
    id: 'correct-description',
    name: 'Payment Memo Spell Corrector',
    shortDesc: 'Automatically resolve typos and shorthand in customer payment memos',
    fullDesc:
      'Compares customer-entered payment memos and OCR transaction text against verified banking transaction templates to suggest accurate standardized descriptions.',
    endpoint: '/module3/correct-description',
    icon: <SpellCheck size={20} className="text-blue" />,
    buttonText: 'Correct Payment Memo',
    fields: [
      {
        key: 'query',
        label: 'Customer Payment Memo (with typos or abbreviations)',
        placeholder: 'e.g. Salry credit fom TechCrp',
        default: 'Salry credit fom TechCrp',
        help: 'Test how the platform maps distorted memo strings to authentic banking templates.',
      },
    ],
  },
  {
    id: 'handle-error',
    name: 'Critical Account Number Rectifier',
    shortDesc: 'Reconcile transmission errors with strict preservation of numeric digits',
    fullDesc:
      'Evaluates differences between transmitted and authentic account records using weighted cost rules that assign severe penalties to digit distortions in critical accounts.',
    endpoint: '/module3/handle-error',
    icon: <ShieldAlert size={20} className="text-purple" />,
    buttonText: 'Reconcile Account Discrepancy',
    fields: [
      {
        key: 'errorData',
        label: 'Received Distorted Account String',
        placeholder: 'e.g. ACC1234567890',
        default: 'ACC1234567890',
        help: 'Account identifier received with suspected transmission discrepancy.',
      },
      {
        key: 'correctData',
        label: 'Reference Account Template',
        placeholder: 'e.g. ACC1234557890',
        default: 'ACC1234557890',
        help: 'Target authentic account record stored in the core banking master table.',
      },
    ],
  },
  {
    id: 'combine-operations',
    name: 'Asset Portfolio Allocation Optimizer',
    shortDesc: 'Maximize investment yield within regulatory risk and capital constraints',
    fullDesc:
      'Calculates the highest-yielding combination of banking investments, debt instruments, and liquidity reserves that satisfy regulatory capital caps and conflict rules.',
    endpoint: '/module3/combine-operations',
    icon: <PieChart size={20} className="text-green" />,
    buttonText: 'Optimize Portfolio Allocation',
    fields: [
      {
        key: 'budget',
        label: 'Available Capital Budget ($)',
        type: 'number',
        placeholder: 'e.g. 500000',
        default: '500000',
        help: 'Total available liquid investment capital for asset portfolio distribution.',
      },
    ],
  },
  {
    id: 'optimize-analysis',
    name: 'Clearing Pipeline Multi-Stage Scheduler',
    shortDesc: 'Determine lowest-cost order for multi-stage transaction batch pipelines',
    fullDesc:
      'Calculates the optimal execution sequence for multi-tier banking data transformation pipelines, minimizing compute overhead and intermediary clearing latency.',
    endpoint: '/module3/optimize-analysis',
    icon: <Workflow size={20} className="text-cyan" />,
    buttonText: 'Schedule Processing Pipeline',
    fields: [],
  },
]

export default function Module3Page() {
  return (
    <ModuleLayout
      moduleTitle="Data Cleansing & Operations Optimization"
      moduleCategory="OPERATIONAL DATA QUALITY"
      moduleDescription="Intelligent typo correction, account number reconciliation, and portfolio allocation optimization."
      tools={module3Tools}
    />
  )
}

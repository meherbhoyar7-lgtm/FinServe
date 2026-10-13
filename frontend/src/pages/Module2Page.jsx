import ModuleLayout from '../components/ModuleLayout'
import { FileText, Database, CopyCheck, SearchCheck } from 'lucide-react'

const module2Tools = [
  {
    id: 'index-transactions',
    name: 'Historical Transaction Substring Search',
    shortDesc: 'Locate partial words, names, or reference codes in archives',
    fullDesc:
      'Builds an indexed archive of historical transaction data for sub-second retrieval of partial merchant descriptions, payment notes, and counterparty names.',
    endpoint: '/module2/index-transactions',
    icon: <Database size={20} className="text-blue" />,
    buttonText: 'Query Historical Archive',
    fields: [
      {
        key: 'query',
        label: 'Substring Query Term',
        placeholder: 'e.g. transfer, payment, deposit, salary',
        default: 'transfer',
        help: 'Target phrase or partial fragment to locate in historical records.',
      },
    ],
  },
  {
    id: 'index-documents',
    name: 'Regulatory Document Full-Text Indexer',
    shortDesc: 'Index compliance filings, customer contracts, and audit reports',
    fullDesc:
      'Constructs high-speed linear indices across large-scale financial documents and audit reports to facilitate instantaneous compliance discovery.',
    endpoint: '/module2/index-documents',
    icon: <FileText size={20} className="text-purple" />,
    buttonText: 'Index & Search Documents',
    fields: [
      {
        key: 'query',
        label: 'Compliance Search Term',
        placeholder: 'e.g. compliance, reserve, capital, audit',
        default: 'compliance',
        help: 'Statutory or compliance keyword to search across indexed regulatory filings.',
      },
    ],
  },
  {
    id: 'find-patterns',
    name: 'Contract Clause Repetition Finder',
    shortDesc: 'Surface recurring standard terms and duplicate legal boilerplates',
    fullDesc:
      'Scans bank loan agreements, disclosures, and contractual documents to identify the longest identical clauses and duplicate legal boilerplate text.',
    endpoint: '/module2/find-patterns',
    icon: <CopyCheck size={20} className="text-green" />,
    buttonText: 'Discover Repeated Clauses',
    fields: [],
  },
  {
    id: 'substring-query',
    name: 'Audit Stream Content Inspector',
    shortDesc: 'Verify presence of required regulatory clauses and disclosures',
    fullDesc:
      'Inspects document content streams in real time to verify whether required statutory statements or risk disclosures exist within published records.',
    endpoint: '/module2/substring-query',
    icon: <SearchCheck size={20} className="text-cyan" />,
    buttonText: 'Inspect Document Stream',
    fields: [
      {
        key: 'query',
        label: 'Audit Verification Phrase',
        placeholder: 'e.g. suspicious, capital requirement, risk',
        default: 'suspicious',
        help: 'Statutory phrase to verify in incoming financial filing streams.',
      },
    ],
  },
]

export default function Module2Page() {
  return (
    <ModuleLayout
      moduleTitle="Document Management & Archival"
      moduleCategory="DOCUMENT INTELLIGENCE"
      moduleDescription="Fast document indexing, full-text regulatory search, and legal contract boilerplate analysis."
      tools={module2Tools}
    />
  )
}

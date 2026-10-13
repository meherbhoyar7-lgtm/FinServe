import ModuleLayout from '../components/ModuleLayout'
import { ShieldCheck, UserCheck, Shuffle, CheckSquare } from 'lucide-react'

const module5Tools = [
  {
    id: 'validate-rules',
    name: 'Regulatory Policy Constraint Validator',
    shortDesc: 'Verify operational compliance with mandatory multi-tier banking statutes',
    fullDesc:
      'Evaluates interconnected regulatory policy rules, AML requirements, and account tier constraints to verify whether banking operations satisfy statutory mandates simultaneously.',
    endpoint: '/module5/validate-rules',
    icon: <ShieldCheck size={20} className="text-blue" />,
    buttonText: 'Validate Regulatory Constraints',
    fields: [],
  },
  {
    id: 'analyze-constraints',
    name: 'Syndicate & Collusion Ring Detector',
    shortDesc: 'Detect tightly coordinated accounts attempting circular transactions',
    fullDesc:
      'Uncovers coordinated clusters of accounts engaged in mutual transfers, identifying potential fraud rings, coordinated defaults, and synthetic identity networks.',
    endpoint: '/module5/analyze-constraints',
    icon: <UserCheck size={20} className="text-red" />,
    buttonText: 'Scan for Collusion Rings',
    fields: [],
  },
  {
    id: 'analyze-conflicts',
    name: 'Risk Isolation & Counterparty Segregation',
    shortDesc: 'Isolate mutually independent accounts to eliminate contagion risk',
    fullDesc:
      'Partitions accounts with overlapping dispute histories into independent sub-portfolios, allowing continuous settlement processing without cross-party contagion.',
    endpoint: '/module5/analyze-conflicts',
    icon: <Shuffle size={20} className="text-purple" />,
    buttonText: 'Analyze Counterparty Segregation',
    fields: [],
  },
  {
    id: 'solve-dependencies',
    name: 'Audit Checkpoint Optimizer',
    shortDesc: 'Determine minimal oversight points required to monitor all transit paths',
    fullDesc:
      'Calculates the minimum set of audit inspection checkpoints necessary to maintain 100% supervisory coverage across all interbank transaction channels with minimal compliance cost.',
    endpoint: '/module5/solve-dependencies',
    icon: <CheckSquare size={20} className="text-green" />,
    buttonText: 'Optimize Audit Checkpoints',
    fields: [],
  },
]

export default function Module5Page() {
  return (
    <ModuleLayout
      moduleTitle="Compliance & Risk Governance"
      moduleCategory="RISK & REGULATORY GOVERNANCE"
      moduleDescription="Mandatory policy constraint checking, collusion syndicate detection, and audit inspection point optimization."
      tools={module5Tools}
    />
  )
}

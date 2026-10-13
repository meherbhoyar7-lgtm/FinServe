import ModuleLayout from '../components/ModuleLayout'
import { Network, Route, Layers, Users, AlertTriangle } from 'lucide-react'

const module4Tools = [
  {
    id: 'transaction-flow',
    name: 'Interbank Liquidity Flow Analyzer',
    shortDesc: 'Compute maximum capital volume safely routable between bank nodes',
    fullDesc:
      'Evaluates the interbank clearing topology to determine the absolute maximum transaction volume that can traverse from origin banks to destination entities without congestion.',
    endpoint: '/module4/transaction-flow',
    icon: <Network size={20} className="text-blue" />,
    buttonText: 'Compute Liquidity Throughput',
    fields: [],
  },
  {
    id: 'network-capacity',
    name: 'Shortest Settlement Corridor Router',
    shortDesc: 'Route high-priority payments through lowest-hop clearing corridors',
    fullDesc:
      'Identifies the optimal settlement path minimizing intermediate bank hops, expediting end-to-end clearing for time-critical institutional wire transfers.',
    endpoint: '/module4/network-capacity',
    icon: <Route size={20} className="text-purple" />,
    buttonText: 'Find Optimal Settlement Corridor',
    fields: [],
  },
  {
    id: 'large-network',
    name: 'High-Volume Bulk Clearing Engine',
    shortDesc: 'Multi-layer transfer simulation for heavy corporate payroll batches',
    fullDesc:
      'Simulates high-density batch transfers using layered channel allocation to process corporate payroll and vendor batches without exceeding channel capacities.',
    endpoint: '/module4/large-network',
    icon: <Layers size={20} className="text-green" />,
    buttonText: 'Simulate Bulk Settlement',
    fields: [],
  },
  {
    id: 'assign-analysts',
    name: 'Investigation Case Workload Assigner',
    shortDesc: 'Optimally assign fraud alerts and compliance cases to investigators',
    fullDesc:
      'Pairs open compliance alerts and disputed transaction files with qualified risk investigators to achieve 100% case resolution with zero unassigned backlogs.',
    endpoint: '/module4/assign-analysts',
    icon: <Users size={20} className="text-cyan" />,
    buttonText: 'Allocate Investigation Cases',
    fields: [],
  },
  {
    id: 'critical-channels',
    name: 'Settlement Bottleneck & Vulnerability Monitor',
    shortDesc: 'Isolate critical transit corridors whose disruption threatens liquidity',
    fullDesc:
      'Pinpoints vulnerable interbank clearing links that operate at peak saturation, highlighting systemic stress points requiring liquidity reinforcement.',
    endpoint: '/module4/critical-channels',
    icon: <AlertTriangle size={20} className="text-red" />,
    buttonText: 'Identify Network Bottlenecks',
    fields: [],
  },
]

export default function Module4Page() {
  return (
    <ModuleLayout
      moduleTitle="Settlement Networks & Liquidity"
      moduleCategory="TREASURY & CLEARING"
      moduleDescription="Interbank capital throughput, settlement corridor routing, and systemic liquidity bottleneck detection."
      tools={module4Tools}
    />
  )
}

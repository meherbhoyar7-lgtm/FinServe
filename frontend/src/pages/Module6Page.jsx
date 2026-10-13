import ModuleLayout from '../components/ModuleLayout'
import { Dice5, Target, BarChart4, Cpu } from 'lucide-react'

const module6Tools = [
  {
    id: 'monte-carlo-risk',
    name: 'Portfolio Value-at-Risk Estimator',
    shortDesc: 'Estimate worst-case portfolio loss using Monte Carlo random sampling',
    fullDesc:
      'Runs thousands of randomised simulations sampling from return distributions to estimate portfolio Value-at-Risk (VaR) and Expected Shortfall (CVaR) at configurable confidence levels.',
    endpoint: '/module6/monte-carlo-risk',
    icon: <Dice5 size={20} className="text-purple" />,
    buttonText: 'Run Monte Carlo Simulation',
    fields: [],
  },
  {
    id: 'las-vegas-fraud',
    name: 'Statistical Anomaly Fraud Detector',
    shortDesc: 'Detect outlier transactions using Las Vegas randomised quickselect',
    fullDesc:
      'Uses randomised quickselect (expected O(n)) to compute exact median and IQR thresholds, then flags statistically anomalous transactions. Always returns the correct result — runtime is probabilistic.',
    endpoint: '/module6/las-vegas-fraud',
    icon: <Target size={20} className="text-red" />,
    buttonText: 'Detect Anomalous Transactions',
    fields: [],
  },
  {
    id: 'parallel-analytics',
    name: 'Parallel Prefix Sum & Reduce Engine',
    shortDesc: 'Compute running balances and aggregate stats using parallel primitives',
    fullDesc:
      'Applies Blelloch parallel prefix sum (scan) for running balance computation and tree-based parallel reduce for aggregate portfolio statistics. Includes full work-span analysis (W, S, P).',
    endpoint: '/module6/parallel-analytics',
    icon: <BarChart4 size={20} className="text-green" />,
    buttonText: 'Compute Parallel Analytics',
    fields: [],
  },
  {
    id: 'work-span-analysis',
    name: 'Work-Span Pipeline Parallelism Assessor',
    shortDesc: 'Analyze parallelism potential across banking batch pipelines',
    fullDesc:
      "Evaluates work W(n), span S(n), and parallelism P = W/S for multiple banking batch operations. Projects execution times on varying processor counts using Brent's theorem (T_p ≤ W/p + S).",
    endpoint: '/module6/work-span-analysis',
    icon: <Cpu size={20} className="text-cyan" />,
    buttonText: 'Analyze Pipeline Parallelism',
    fields: [],
  },
]

export default function Module6Page() {
  return (
    <ModuleLayout
      moduleTitle="Randomised & Parallel Algorithms"
      moduleCategory="PROBABILISTIC & PARALLEL COMPUTING"
      moduleDescription="Monte Carlo risk simulation, Las Vegas anomaly detection, parallel prefix sum, parallel reduce, and work-span analysis."
      tools={module6Tools}
    />
  )
}

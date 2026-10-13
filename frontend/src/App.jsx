import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import BackgroundEffect from './components/BackgroundEffect'
import Dashboard from './pages/Dashboard'
import Module1Page from './pages/Module1Page'
import Module2Page from './pages/Module2Page'
import Module3Page from './pages/Module3Page'
import Module4Page from './pages/Module4Page'
import Module5Page from './pages/Module5Page'
import Module6Page from './pages/Module6Page'

function App() {
  return (
    <div className="app-root">
      {/* Living background playing behind the entire project */}
      <BackgroundEffect />

      {/* Floating Menu Bar */}
      <Navbar />

      {/* Main Pages */}
      <div className="app-main">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/module1" element={<Module1Page />} />
          <Route path="/module2" element={<Module2Page />} />
          <Route path="/module3" element={<Module3Page />} />
          <Route path="/module4" element={<Module4Page />} />
          <Route path="/module5" element={<Module5Page />} />
          <Route path="/module6" element={<Module6Page />} />
        </Routes>
      </div>
    </div>
  )
}

export default App

import { useEffect, useRef } from 'react'

/**
 * Classy Institutional Background Effect:
 * - Pure white and soft slate canvas
 * - Subtle ambient top-lit executive blue glow
 * - Minimal, elegant flowing vector wave lines (no background picture)
 * - Interactive micro-constellation particles with delicate connective threads
 */
export default function BackgroundEffect() {
  const canvasRef = useRef(null)

  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas) return
    const ctx = canvas.getContext('2d')
    let animationFrameId
    let width = (canvas.width = window.innerWidth)
    let height = (canvas.height = window.innerHeight)

    let mouse = {
      x: width * 0.5,
      y: height * 0.35,
      targetX: width * 0.5,
      targetY: height * 0.35,
    }

    const handleResize = () => {
      if (!canvas) return
      width = canvas.width = window.innerWidth
      height = canvas.height = window.innerHeight
    }

    const handleMouseMove = (e) => {
      mouse.targetX = e.clientX
      mouse.targetY = e.clientY
    }

    window.addEventListener('resize', handleResize)
    window.addEventListener('mousemove', handleMouseMove)

    // Elegant, clean mathematical wave harmonics in subtle blue
    const waves = [
      { y: height * 0.38, length: 0.0022, amplitude: 35, speed: 0.008, color: 'rgba(37, 99, 235, 0.06)' },
      { y: height * 0.48, length: 0.0018, amplitude: 45, speed: 0.006, color: 'rgba(30, 64, 175, 0.04)' },
      { y: height * 0.58, length: 0.0025, amplitude: 28, speed: 0.010, color: 'rgba(59, 130, 246, 0.05)' },
    ]

    // Professional micro-particles (small, crisp, blue)
    const particles = []
    const numParticles = 28
    for (let i = 0; i < numParticles; i++) {
      particles.push({
        x: Math.random() * width,
        y: Math.random() * height,
        vx: (Math.random() - 0.5) * 0.35,
        vy: (Math.random() - 0.5) * 0.3,
        radius: Math.random() * 1.6 + 1.0,
        color: i % 2 === 0 ? 'rgba(37, 99, 235, ' : 'rgba(30, 64, 175, ',
        baseAlpha: Math.random() * 0.25 + 0.12,
      })
    }

    let t = 0

    const render = () => {
      t += 1
      ctx.clearRect(0, 0, width, height)

      // Smooth mouse easing
      mouse.x += (mouse.targetX - mouse.x) * 0.04
      mouse.y += (mouse.targetY - mouse.y) * 0.04

      // 1. Subtle, classy cursor spotlight in delicate royal blue
      const spotGrad = ctx.createRadialGradient(
        mouse.x, mouse.y, 10,
        mouse.x, mouse.y, Math.min(width, height) * 0.38
      )
      spotGrad.addColorStop(0, 'rgba(37, 99, 235, 0.045)')
      spotGrad.addColorStop(0.6, 'rgba(59, 130, 246, 0.015)')
      spotGrad.addColorStop(1, 'rgba(255, 255, 255, 0)')
      ctx.fillStyle = spotGrad
      ctx.fillRect(0, 0, width, height)

      // 2. Classy vector wave curves (pure mathematical flow)
      waves.forEach((w) => {
        ctx.beginPath()
        ctx.moveTo(0, height)
        ctx.lineTo(0, w.y)

        for (let x = 0; x <= width; x += 16) {
          const dx = x - mouse.x
          const distToMouse = Math.abs(dx)
          const mouseLift = Math.max(0, 1 - distToMouse / 450) * 18 * Math.sin(t * 0.02)

          const y = w.y +
            Math.sin(x * w.length + t * w.speed) * w.amplitude +
            Math.cos(x * w.length * 0.7 + t * w.speed * 0.9) * (w.amplitude * 0.4) +
            mouseLift

          ctx.lineTo(x, y)
        }

        ctx.lineTo(width, height)
        ctx.closePath()

        ctx.fillStyle = w.color
        ctx.fill()

        // Subtle crest hairline
        ctx.beginPath()
        for (let x = 0; x <= width; x += 16) {
          const y = w.y +
            Math.sin(x * w.length + t * w.speed) * w.amplitude +
            Math.cos(x * w.length * 0.7 + t * w.speed * 0.9) * (w.amplitude * 0.4)
          if (x === 0) ctx.moveTo(x, y)
          else ctx.lineTo(x, y)
        }
        ctx.strokeStyle = 'rgba(37, 99, 235, 0.12)'
        ctx.lineWidth = 1
        ctx.stroke()
      })

      // 3. Crisp constellation particles with connecting filaments
      for (let i = 0; i < particles.length; i++) {
        const p1 = particles[i]
        p1.x += p1.vx
        p1.y += p1.vy

        if (p1.x < 0) p1.x = width
        if (p1.x > width) p1.x = 0
        if (p1.y < 0) p1.y = height
        if (p1.y > height) p1.y = 0

        const pulse = Math.sin(t * 0.03 + i) * 0.15 + 1
        ctx.beginPath()
        ctx.arc(p1.x, p1.y, p1.radius * pulse, 0, Math.PI * 2)
        ctx.fillStyle = `${p1.color}${p1.baseAlpha})`
        ctx.fill()

        // Faint connecting lines
        for (let j = i + 1; j < particles.length; j++) {
          const p2 = particles[j]
          const dx = p1.x - p2.x
          const dy = p1.y - p2.y
          const dist = Math.sqrt(dx * dx + dy * dy)

          if (dist < 120) {
            ctx.beginPath()
            ctx.moveTo(p1.x, p1.y)
            ctx.lineTo(p2.x, p2.y)
            const alpha = (1 - dist / 120) * 0.08
            ctx.strokeStyle = `rgba(37, 99, 235, ${alpha})`
            ctx.lineWidth = 0.7
            ctx.stroke()
          }
        }
      }

      animationFrameId = requestAnimationFrame(render)
    }

    render()

    return () => {
      window.removeEventListener('resize', handleResize)
      window.removeEventListener('mousemove', handleMouseMove)
      cancelAnimationFrame(animationFrameId)
    }
  }, [])

  return (
    <div className="global-classy-background">
      {/* Top subtle blue institutional gradient illumination */}
      <div className="bg-classy-gradient" />
      {/* Crisp 60fps vector wave & constellation canvas */}
      <canvas ref={canvasRef} className="bg-classy-canvas" />
    </div>
  )
}

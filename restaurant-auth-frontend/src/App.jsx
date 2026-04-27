import { useState } from 'react'
import { toast } from 'react-toastify'
import { parseError } from './api'
import Customers from './pages/Customers'
import Menu from './pages/Menu'
import Orders from './pages/Orders'
import Receipts from './pages/Receipts'
import Locations from './pages/Locations'

const NAV = ['Orders', 'Menu', 'Customers', 'Receipts', 'Locations']

function App() {
  const [authMode, setAuthMode] = useState('login')
  const [isAuthLoading, setIsAuthLoading] = useState(false)
  const [currentUser, setCurrentUser] = useState(() => {
    const s = sessionStorage.getItem('authenticatedUser')
    return s ? JSON.parse(s) : null
  })
  const [loginForm, setLoginForm] = useState({ username: '', password: '' })
  const [registerForm, setRegisterForm] = useState({ username: '', password: '', fullName: '', role: 'WAITER', phone: '', email: '' })
  const [page, setPage] = useState('Orders')

  const unwrap = (payload) => (payload && 'data' in payload ? payload.data : payload)

  const handleLogin = async (e) => {
    e.preventDefault()
    setIsAuthLoading(true)
    try {
      const res = await fetch('/api/users/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: loginForm.username.trim(), password: loginForm.password }),
      })
      const payload = await res.json()
      if (!res.ok) throw new Error(parseError(payload))
      const user = unwrap(payload)
      setCurrentUser(user)
      sessionStorage.setItem('authenticatedUser', JSON.stringify(user))
      toast.success(`Welcome back, ${user.fullName}!`)
    } catch (e) { toast.error(e.message) } finally { setIsAuthLoading(false) }
  }

  const handleRegister = async (e) => {
    e.preventDefault()
    setIsAuthLoading(true)
    try {
      const phone = registerForm.phone.trim()
      const email = registerForm.email.trim()
      const res = await fetch('/api/users/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username: registerForm.username.trim(),
          password: registerForm.password,
          fullName: registerForm.fullName.trim(),
          role: registerForm.role,
          phone: phone || null,
          email: email || null,
        }),
      })
      const payload = await res.json()
      if (!res.ok) throw new Error(parseError(payload))
      const user = unwrap(payload)
      setCurrentUser(user)
      sessionStorage.setItem('authenticatedUser', JSON.stringify(user))
      toast.success('Account created successfully.')
    } catch (e) { toast.error(e.message) } finally { setIsAuthLoading(false) }
  }

  const logout = () => {
    setCurrentUser(null)
    sessionStorage.clear()
    toast.info('Signed out.')
  }

  if (!currentUser) {
    return (
      <main className="flex min-h-screen items-center justify-center px-6 py-10">
        <section className="w-full max-w-md rounded-3xl border border-emerald-200/80 bg-gradient-to-br from-cyan-50/80 via-emerald-50/70 to-lime-50/80 p-7 shadow-xl sm:p-9">
          <header className="mb-6">
            <p className="mb-3 inline-block rounded-full bg-emerald-950 px-3 py-1 text-[10px] font-semibold tracking-[0.2em] text-emerald-50 uppercase">Restaurant MIS</p>
            <h1 className="text-3xl font-bold tracking-tight text-emerald-950">User Authentication</h1>
          </header>

          <div className="mb-4 grid grid-cols-2 rounded-xl border border-emerald-200 bg-white p-1">
            {['login', 'register'].map(m => (
              <button key={m} onClick={() => setAuthMode(m)}
                className={`rounded-lg px-4 py-2 text-sm font-semibold transition ${authMode === m ? 'bg-emerald-800 text-emerald-50' : 'text-emerald-900 hover:bg-emerald-50'}`}>
                {m === 'login' ? 'Login' : 'Register'}
              </button>
            ))}
          </div>

          {authMode === 'login' ? (
            <form className="grid gap-3" onSubmit={handleLogin}>
              <input type="text" placeholder="Username" value={loginForm.username} onChange={e => setLoginForm(p => ({ ...p, username: e.target.value }))} className="input" required />
              <input type="password" placeholder="Password" value={loginForm.password} onChange={e => setLoginForm(p => ({ ...p, password: e.target.value }))} className="input" required />
              <button type="submit" disabled={isAuthLoading} className="btn-primary">{isAuthLoading ? 'Signing in...' : 'Sign In'}</button>
            </form>
          ) : (
            <form className="grid gap-3" onSubmit={handleRegister}>
              <input type="text" placeholder="Full Name *" value={registerForm.fullName} onChange={e => setRegisterForm(p => ({ ...p, fullName: e.target.value }))} className="input" required />
              <input type="text" placeholder="Username *" value={registerForm.username} onChange={e => setRegisterForm(p => ({ ...p, username: e.target.value }))} className="input" required />
              <input type="password" placeholder="Password *" value={registerForm.password} onChange={e => setRegisterForm(p => ({ ...p, password: e.target.value }))} className="input" required />
              <select value={registerForm.role} onChange={e => setRegisterForm(p => ({ ...p, role: e.target.value }))} className="input">
                <option value="MANAGER">Manager</option>
                <option value="WAITER">Waiter</option>
                <option value="CASHIER">Cashier</option>
              </select>
              <input type="tel" placeholder="Phone (optional, e.g. +250781234567)" value={registerForm.phone} onChange={e => setRegisterForm(p => ({ ...p, phone: e.target.value }))} className="input" />
              <input type="email" placeholder="Email (optional)" value={registerForm.email} onChange={e => setRegisterForm(p => ({ ...p, email: e.target.value }))} className="input" />
              <button type="submit" disabled={isAuthLoading} className="btn-primary">{isAuthLoading ? 'Creating...' : 'Create Account'}</button>
            </form>
          )}
        </section>
      </main>
    )
  }

  const pages = { Orders: <Orders />, Menu: <Menu />, Customers: <Customers />, Receipts: <Receipts />, Locations: <Locations /> }

  return (
    <div className="min-h-screen bg-gradient-to-br from-cyan-50/60 via-emerald-50/50 to-lime-50/60">
      <nav className="sticky top-0 z-10 border-b border-emerald-200 bg-white/90 backdrop-blur">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-3">
          <div className="flex items-center gap-1">
            <span className="mr-4 rounded-full bg-emerald-950 px-3 py-1 text-[10px] font-semibold tracking-widest text-emerald-50 uppercase">Restaurant MIS</span>
            {NAV.map(n => (
              <button key={n} onClick={() => setPage(n)}
                className={`rounded-lg px-3 py-1.5 text-sm font-semibold transition ${page === n ? 'bg-emerald-800 text-emerald-50' : 'text-emerald-900 hover:bg-emerald-100'}`}>
                {n}
              </button>
            ))}
          </div>
          <div className="flex items-center gap-3">
            <span className="text-sm text-emerald-900">{currentUser.fullName} <span className="text-xs text-emerald-600">({currentUser.role})</span></span>
            <button onClick={logout} className="btn-secondary text-sm">Logout</button>
          </div>
        </div>
      </nav>

      <main className="mx-auto max-w-6xl px-6 py-8">
        {pages[page]}
      </main>
    </div>
  )
}

export default App

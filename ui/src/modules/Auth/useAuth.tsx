import React, { createContext, useContext, useEffect, useMemo, useRef, useState } from 'react'
import api from '../../codex-example/api/axios'

type AuthContextType = {
  token: string | null
  login: (email: string, password: string) => Promise<void>
  register: (email: string, password: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

const INACTIVITY_MINUTES = Number(import.meta.env.VITE_INACTIVITY_MINUTES || 30)

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('token'))
  const lastActivity = useRef<number>(Date.now())

  const setAndStoreToken = (t: string | null) => {
    setToken(t)
    if (t) localStorage.setItem('token', t)
    else localStorage.removeItem('token')
  }

  const login = async (email: string, password: string) => {
    const res = await api.post('/auth/login', { email, password })
    setAndStoreToken(res.data.token)
  }

  const register = async (email: string, password: string) => {
    const res = await api.post('/auth/register', { email, password })
    setAndStoreToken(res.data.token)
  }

  const logout = () => setAndStoreToken(null)

  useEffect(() => {
    const mark = () => { lastActivity.current = Date.now() }
    const events = ['mousemove', 'keydown', 'click', 'scroll']
    events.forEach(e => window.addEventListener(e, mark))
    const interval = setInterval(() => {
      if (!token) return
      const minutes = (Date.now() - lastActivity.current) / 60000
      if (minutes >= INACTIVITY_MINUTES) {
        logout()
      }
    }, 60000)
    return () => {
      events.forEach(e => window.removeEventListener(e, mark))
      clearInterval(interval)
    }
  }, [token])

  const value = useMemo(() => ({ token, login, register, logout }), [token])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}


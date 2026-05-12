import React, { useState } from 'react'
import { Button, Container, Link, Stack, TextField, Typography } from '@mui/material'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../useAuth'

const Login: React.FC = () => {
  const { login } = useAuth()
  const nav = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    try {
      await login(email, password)
      nav('/')
    } catch (e:any) {
      setError(e?.response?.data?.message || 'Login failed')
    }
  }

  return (
    <Container maxWidth="sm" sx={{ mt: 8 }}>
      <Typography variant="h4" gutterBottom>Sign In</Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
        Demo user: <strong>demo@example.com</strong> / <strong>Password123!</strong>
      </Typography>
      <form onSubmit={onSubmit}>
        <Stack spacing={2}>
          <TextField label="Email" type="email" value={email} onChange={e=>setEmail(e.target.value)} required />
          <TextField label="Password" type="password" value={password} onChange={e=>setPassword(e.target.value)} required />
          {error && <Typography color="error">{error}</Typography>}
          <Button type="submit" variant="contained">Sign In</Button>
          <Stack direction="row" spacing={2}>
            <Link component="button" onClick={()=>nav('/register')}>Create account</Link>
            <Link component="button" onClick={()=>nav('/forgot-password')}>Forgot password?</Link>
          </Stack>
        </Stack>
      </form>
    </Container>
  )
}

export default Login

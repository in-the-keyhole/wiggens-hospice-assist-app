import React, { useState } from 'react'
import { Button, Container, Stack, TextField, Typography } from '@mui/material'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../useAuth'

const Register: React.FC = () => {
  const { register } = useAuth()
  const nav = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    try {
      await register(email, password)
      nav('/')
    } catch (e:any) {
      setError(e?.response?.data?.message || 'Registration failed')
    }
  }

  return (
    <Container maxWidth="sm" sx={{ mt: 8 }}>
      <Typography variant="h4" gutterBottom>Create Account</Typography>
      <form onSubmit={onSubmit}>
        <Stack spacing={2}>
          <TextField label="Email" type="email" value={email} onChange={e=>setEmail(e.target.value)} required />
          <TextField label="Password" type="password" value={password} onChange={e=>setPassword(e.target.value)} required helperText="At least 8 characters"/>
          {error && <Typography color="error">{error}</Typography>}
          <Button type="submit" variant="contained">Create Account</Button>
        </Stack>
      </form>
    </Container>
  )
}

export default Register


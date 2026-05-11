import React, { useState } from 'react'
import { Button, Container, Stack, TextField, Typography } from '@mui/material'
import api from '../../../codex-example/api/axios'

const ResetPassword: React.FC = () => {
  const [token, setToken] = useState('')
  const [password, setPassword] = useState('')
  const [done, setDone] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    try {
      await api.post('/auth/reset-password', { token, newPassword: password })
      setDone(true)
    } catch (e:any) {
      setError(e?.response?.data?.message || 'Reset failed')
    }
  }

  return (
    <Container maxWidth="sm" sx={{ mt: 8 }}>
      <Typography variant="h4" gutterBottom>Reset Password</Typography>
      {done ? (
        <Typography>Password has been reset.</Typography>
      ) : (
        <form onSubmit={onSubmit}>
          <Stack spacing={2}>
            <TextField label="Token" value={token} onChange={e=>setToken(e.target.value)} required />
            <TextField label="New Password" type="password" value={password} onChange={e=>setPassword(e.target.value)} required />
            {error && <Typography color="error">{error}</Typography>}
            <Button type="submit" variant="contained">Reset Password</Button>
          </Stack>
        </form>
      )}
    </Container>
  )
}

export default ResetPassword


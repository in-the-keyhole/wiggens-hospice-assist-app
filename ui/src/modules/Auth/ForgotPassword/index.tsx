import React, { useState } from 'react'
import { Button, Container, Stack, TextField, Typography } from '@mui/material'
import api from '../../../codex-example/api/axios'

const ForgotPassword: React.FC = () => {
  const [email, setEmail] = useState('')
  const [sent, setSent] = useState(false)

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    await api.post('/auth/forgot-password', { email })
    setSent(true)
  }

  return (
    <Container maxWidth="sm" sx={{ mt: 8 }}>
      <Typography variant="h4" gutterBottom>Forgot Password</Typography>
      {sent ? (
        <Typography>Check your email for reset instructions (dev: token stored).</Typography>
      ) : (
        <form onSubmit={onSubmit}>
          <Stack spacing={2}>
            <TextField label="Email" type="email" value={email} onChange={e=>setEmail(e.target.value)} required />
            <Button type="submit" variant="contained">Send Reset Link</Button>
          </Stack>
        </form>
      )}
    </Container>
  )
}

export default ForgotPassword


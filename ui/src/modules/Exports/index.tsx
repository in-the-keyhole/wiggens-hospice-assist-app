import React, { useState } from 'react'
import { AppBar, Button, Container, Stack, TextField, Toolbar, Typography } from '@mui/material'
import { useAuth } from '../Auth/useAuth'
import { useNavigate } from 'react-router-dom'

const Exports: React.FC = () => {
  const { token, logout } = useAuth()
  const nav = useNavigate()
  const [from, setFrom] = useState<string>(new Date(Date.now()-7*24*3600*1000).toISOString())
  const [to, setTo] = useState<string>(new Date().toISOString())

  const download = async () => {
    const base = import.meta.env.VITE_API_URL || 'http://localhost:8080/codex-example/api/v1'
    const url = `${base}/exports/summary.csv?from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}`
    const token = localStorage.getItem('token')
    const res = await fetch(url, { headers: { Authorization: `Bearer ${token}` } })
    const blob = await res.blob()
    const a = document.createElement('a')
    a.href = URL.createObjectURL(blob)
    a.download = 'summary.csv'
    a.click()
    URL.revokeObjectURL(a.href)
  }

  return (
    <>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>Secure Sharing & Export</Typography>
          <Button color="inherit" onClick={()=>nav('/')}>Dashboard</Button>
          {token ? (
            <Button color="inherit" onClick={logout}>Logout</Button>
          ) : (
            <Button color="inherit" onClick={()=>nav('/login')}>Login</Button>
          )}
        </Toolbar>
      </AppBar>
      <Container sx={{ mt: 3 }}>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField label="From (ISO)" value={from} onChange={e=>setFrom(e.target.value)} fullWidth />
          <TextField label="To (ISO)" value={to} onChange={e=>setTo(e.target.value)} fullWidth />
          <Button variant="contained" onClick={download}>Download CSV</Button>
        </Stack>
        <Typography variant="body2" sx={{ mt: 2 }}>Photos are excluded by default.</Typography>
      </Container>
    </>
  )
}

export default Exports


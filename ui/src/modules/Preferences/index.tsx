import React, { useEffect, useState } from 'react'
import { AppBar, Box, Button, Checkbox, Container, FormControlLabel, Stack, TextField, Toolbar, Typography } from '@mui/material'
import { useAuth } from '../Auth/useAuth'
import { useNavigate } from 'react-router-dom'

type Prefs = { channels: { inapp: boolean; email: boolean; sms: boolean; push: boolean }; quiet: { start: number; end: number; bypassUrgent: boolean } }

const Preferences: React.FC = () => {
  const { token, logout } = useAuth()
  const nav = useNavigate()
  const [prefs, setPrefs] = useState<Prefs>({ channels: { inapp:true, email:false, sms:false, push:false }, quiet: { start:22, end:7, bypassUrgent:true } })

  useEffect(() => {
    try {
      const raw = localStorage.getItem('notificationPrefs')
      if (raw) setPrefs(JSON.parse(raw))
    } catch {}
  }, [])

  const save = () => {
    localStorage.setItem('notificationPrefs', JSON.stringify(prefs))
    alert('Preferences saved')
  }

  const testNotify = () => {
    alert(`Test notification via: ${Object.entries(prefs.channels).filter(([,v])=>v).map(([k])=>k).join(', ') || 'none'}`)
  }

  return (
    <>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>Notification Preferences</Typography>
          <Button color="inherit" onClick={()=>nav('/')}>Dashboard</Button>
          {token ? (
            <Button color="inherit" onClick={logout}>Logout</Button>
          ) : (
            <Button color="inherit" onClick={()=>nav('/login')}>Login</Button>
          )}
        </Toolbar>
      </AppBar>
      <Container sx={{ mt: 3 }}>
        <Typography variant="h6">Channels</Typography>
        <Stack>
          <FormControlLabel control={<Checkbox checked={prefs.channels.inapp} onChange={e=>setPrefs(p=>({...p, channels:{...p.channels, inapp:e.target.checked}}))} />} label="In-app" />
          <FormControlLabel control={<Checkbox checked={prefs.channels.email} onChange={e=>setPrefs(p=>({...p, channels:{...p.channels, email:e.target.checked}}))} />} label="Email (if configured)" />
          <FormControlLabel control={<Checkbox checked={prefs.channels.sms} onChange={e=>setPrefs(p=>({...p, channels:{...p.channels, sms:e.target.checked}}))} />} label="SMS (if configured)" />
          <FormControlLabel control={<Checkbox checked={prefs.channels.push} onChange={e=>setPrefs(p=>({...p, channels:{...p.channels, push:e.target.checked}}))} />} label="Push (if supported)" />
        </Stack>
        <Typography variant="h6" sx={{ mt: 2 }}>Quiet Hours</Typography>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField label="Start Hour (0-23)" type="number" value={prefs.quiet.start} onChange={e=>setPrefs(p=>({...p, quiet:{...p.quiet, start:Number(e.target.value)}}))} />
          <TextField label="End Hour (0-23)" type="number" value={prefs.quiet.end} onChange={e=>setPrefs(p=>({...p, quiet:{...p.quiet, end:Number(e.target.value)}}))} />
          <FormControlLabel control={<Checkbox checked={prefs.quiet.bypassUrgent} onChange={e=>setPrefs(p=>({...p, quiet:{...p.quiet, bypassUrgent:e.target.checked}}))} />} label="Bypass urgent alerts" />
        </Stack>
        <Box sx={{ mt: 2 }}>
          <Button sx={{ mr: 1 }} variant="contained" onClick={save}>Save</Button>
          <Button variant="outlined" onClick={testNotify}>Send Test Notification</Button>
        </Box>
      </Container>
    </>
  )
}

export default Preferences


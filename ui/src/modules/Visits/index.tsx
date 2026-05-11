import React, { useEffect, useMemo, useRef, useState } from 'react'
import { AppBar, Box, Button, Container, Dialog, DialogActions, DialogContent, DialogTitle, Stack, TextField, Toolbar, Typography } from '@mui/material'
import { addVisit, completeVisit, listPastVisits, listUpcomingVisits, Visit } from '../../codex-example/api/visits'
import { useAuth } from '../Auth/useAuth'
import { useNavigate } from 'react-router-dom'

const Visits: React.FC = () => {
  const { token, logout } = useAuth()
  const nav = useNavigate()
  const [upcoming, setUpcoming] = useState<Visit[]>([])
  const [past, setPast] = useState<Visit[]>([])
  const [form, setForm] = useState<{ at: string; providerRole: string; notes?: string }>({ at: '', providerRole: 'Nurse', notes: '' })
  const [completeFor, setCompleteFor] = useState<Visit | null>(null)
  const [completeForm, setCompleteForm] = useState<{ visitNotes?: string; vitals?: string; careChanges?: string }>({})
  const remindMins = useMemo(() => ((import.meta.env.VITE_VISIT_REMIND_MINUTES as string | undefined) || '60,1440').split(',').map(n => Number(n.trim())), [])
  const alerted = useRef<Record<number, number>>({})

  const load = async () => {
    if (!token) return
    const [u, p] = await Promise.all([listUpcomingVisits(), listPastVisits()])
    setUpcoming(u)
    setPast(p)
  }
  useEffect(() => { load() }, [token])

  const onAdd = async (e: React.FormEvent) => {
    e.preventDefault()
    await addVisit(form)
    setForm({ at: '', providerRole: 'Nurse', notes: '' })
    load()
  }

  const onComplete = async () => {
    if (!completeFor) return
    await completeVisit(completeFor.id, completeForm)
    setCompleteFor(null); setCompleteForm({})
    load()
  }

  useEffect(() => {
    const tick = () => {
      const now = Date.now()
      for (const v of upcoming) {
        const at = new Date(v.at).getTime()
        for (const m of remindMins) {
          const t = at - m*60000
          if (now >= t && (!alerted.current[v.id] || alerted.current[v.id] < m)) {
            alerted.current[v.id] = m
            alert(`Upcoming visit in ${m} minutes: ${v.providerRole} at ${new Date(v.at).toLocaleString()}`)
          }
        }
      }
    }
    const id = setInterval(tick, 60000)
    tick()
    return () => clearInterval(id)
  }, [upcoming, remindMins])

  return (
    <>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>Nurse Visits</Typography>
          <Button color="inherit" onClick={()=>nav('/')}>Dashboard</Button>
          <Button color="inherit" onClick={()=>nav('/contacts')}>Contacts</Button>
          <Button color="inherit" onClick={()=>nav('/medications')}>Medications</Button>
          {token ? (
            <Button color="inherit" onClick={logout}>Logout</Button>
          ) : (
            <Button color="inherit" onClick={()=>nav('/login')}>Login</Button>
          )}
        </Toolbar>
      </AppBar>
      <Container sx={{ mt:4 }}>
        <Typography variant="h5" gutterBottom>Schedule a Visit</Typography>
        <Box component="form" onSubmit={onAdd} sx={{ mb:3 }}>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
            <TextField type="datetime-local" label="Date/Time" InputLabelProps={{ shrink: true }} value={form.at} onChange={e=>setForm(f=>({...f, at: e.target.value}))} required />
            <TextField label="Provider Role" value={form.providerRole} onChange={e=>setForm(f=>({...f, providerRole: e.target.value}))} required />
            <TextField label="Notes" value={form.notes} onChange={e=>setForm(f=>({...f, notes: e.target.value}))} />
            <Button type="submit" variant="contained">Add</Button>
          </Stack>
        </Box>

        <Typography variant="h6">Upcoming</Typography>
        <Stack sx={{ mb:3 }}>
          {upcoming.map(v => (
            <Box key={v.id} sx={{ p:2, border:'1px solid #ddd', borderRadius:1, display:'flex', alignItems:'center', justifyContent:'space-between' }}>
              <Box>
                <Typography variant="subtitle1">{v.providerRole} — {new Date(v.at).toLocaleString()}</Typography>
                <Typography variant="body2" color="text.secondary">{v.notes}</Typography>
              </Box>
              <Button variant="outlined" onClick={()=>{ setCompleteFor(v); setCompleteForm({}) }}>Complete</Button>
            </Box>
          ))}
        </Stack>

        <Typography variant="h6">Past</Typography>
        <Stack>
          {past.map(v => (
            <Box key={v.id} sx={{ p:2, border:'1px solid #ddd', borderRadius:1 }}>
              <Typography variant="subtitle1">{v.providerRole} — {new Date(v.at).toLocaleString()} (Completed)</Typography>
              <Typography variant="body2">{v.visitNotes}</Typography>
              <Typography variant="body2" color="text.secondary">{v.vitals} | {v.careChanges}</Typography>
            </Box>
          ))}
        </Stack>
      </Container>

      <Dialog open={!!completeFor} onClose={()=>setCompleteFor(null)}>
        <DialogTitle>Complete Visit</DialogTitle>
        <DialogContent>
          <Stack spacing={2} sx={{ mt:1 }}>
            <TextField label="Visit Notes" value={completeForm.visitNotes||''} onChange={e=>setCompleteForm(f=>({...f, visitNotes: e.target.value}))} />
            <TextField label="Vitals" value={completeForm.vitals||''} onChange={e=>setCompleteForm(f=>({...f, vitals: e.target.value}))} />
            <TextField label="Care Changes" value={completeForm.careChanges||''} onChange={e=>setCompleteForm(f=>({...f, careChanges: e.target.value}))} />
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={()=>setCompleteFor(null)}>Cancel</Button>
          <Button variant="contained" onClick={onComplete}>Save</Button>
        </DialogActions>
      </Dialog>
    </>
  )
}

export default Visits


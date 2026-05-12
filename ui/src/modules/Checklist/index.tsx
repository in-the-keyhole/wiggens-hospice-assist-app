import React, { useEffect, useMemo, useRef, useState } from 'react'
import { AppBar, Box, Button, Container, MenuItem, Stack, TextField, Toolbar, Typography } from '@mui/material'
import { useAuth } from '../Auth/useAuth'
import { useNavigate } from 'react-router-dom'
import { addMyTask, completeTask, listHistory, listMyTasks, CareTask, FrequencyType } from '../../codex-example/api/checklist'
import { enqueueCompletion, flush as flushOffline } from '../../codex-example/api/offlineChecklist'

const Checklist: React.FC = () => {
  const { token, logout } = useAuth()
  const nav = useNavigate()
  const [tasks, setTasks] = useState<CareTask[]>([])
  const [history, setHistory] = useState<{ taskName: string; completedAt: string }[]>([])
  const [form, setForm] = useState<{ name: string; frequencyType: FrequencyType; timesPerDay?: number; daysOfWeek?: string; notes?: string }>({ name: '', frequencyType: 'ONCE' })

  const load = async () => {
    if (!token) return
    setTasks(await listMyTasks())
    const hist = await listHistory()
    setHistory(hist)
  }

  useEffect(() => { load() }, [token])

  useEffect(() => {
    const run = () => flushOffline(async (e) => completeTask(e.id, e.at))
    run()
    const id = setInterval(run, 10000)
    return () => clearInterval(id)
  }, [])

  const onAdd = async (e: React.FormEvent) => {
    e.preventDefault()
    const created = await addMyTask(form)
    setTasks(prev => [...prev, created])
    setForm({ name: '', frequencyType: 'ONCE' })
  }

  const onComplete = async (t: CareTask) => {
    try {
      await completeTask(t.id, new Date().toISOString())
      await load()
    } catch {
      enqueueCompletion({ id: t.id, at: new Date().toISOString() })
      alert('Offline: completion queued')
    }
  }

  return (
    <>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>Daily Care Checklist</Typography>
          <Button color="inherit" onClick={()=>nav('/')}>Dashboard</Button>
          <Button color="inherit" onClick={()=>nav('/symptoms')}>Symptoms</Button>
          {token ? (
            <Button color="inherit" onClick={logout}>Logout</Button>
          ) : (
            <Button color="inherit" onClick={()=>nav('/login')}>Login</Button>
          )}
        </Toolbar>
      </AppBar>
      <Container sx={{ mt: 4 }}>
        <Typography variant="h5" gutterBottom>Add Care Task</Typography>
        <Box component="form" onSubmit={onAdd} sx={{ mb: 3 }}>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
            <TextField label="Name" value={form.name} onChange={e=>setForm(f=>({...f, name: e.target.value}))} required />
            <TextField select label="Frequency" value={form.frequencyType} onChange={e=>setForm(f=>({...f, frequencyType: e.target.value as FrequencyType}))}>
              <MenuItem value="ONCE">Once</MenuItem>
              <MenuItem value="TIMES_PER_DAY">Times per day</MenuItem>
              <MenuItem value="DAYS_OF_WEEK">Days of week</MenuItem>
            </TextField>
            {form.frequencyType === 'TIMES_PER_DAY' && (
              <TextField label="Times/Day" type="number" value={form.timesPerDay ?? ''} onChange={e=>setForm(f=>({...f, timesPerDay: e.target.value? Number(e.target.value): undefined}))} />
            )}
            {form.frequencyType === 'DAYS_OF_WEEK' && (
              <TextField label="Days (CSV MON..SUN)" value={form.daysOfWeek ?? ''} onChange={e=>setForm(f=>({...f, daysOfWeek: e.target.value}))} />
            )}
            <TextField label="Notes" value={form.notes ?? ''} onChange={e=>setForm(f=>({...f, notes: e.target.value}))} />
            <Button type="submit" variant="contained">Add</Button>
          </Stack>
        </Box>

        <Typography variant="h6">Active Tasks</Typography>
        <Stack spacing={1} sx={{ mb: 3 }}>
          {tasks.map(t => (
            <Box key={t.id} sx={{ p:2, border:'1px solid #ddd', borderRadius:1, display:'flex', justifyContent:'space-between' }}>
              <Box>
                <Typography variant="subtitle1">{t.name}</Typography>
                <Typography variant="body2" color="text.secondary">{t.frequencyType}{t.timesPerDay?` ${t.timesPerDay}x`:''}{t.daysOfWeek?` ${t.daysOfWeek}`:''}</Typography>
                {t.notes && <Typography variant="body2">{t.notes}</Typography>}
              </Box>
              <Button variant="outlined" onClick={()=>onComplete(t)}>Mark Complete</Button>
            </Box>
          ))}
        </Stack>

        <Typography variant="h6">Recent Completions</Typography>
        <Stack spacing={1}>
          {history.map((h, i) => (
            <Box key={i} sx={{ p:1, border:'1px dashed #ccc', borderRadius:1 }}>
              <Typography variant="body2">{new Date(h.completedAt).toLocaleString()} — {h.taskName}</Typography>
            </Box>
          ))}
        </Stack>
      </Container>
    </>
  )
}

export default Checklist


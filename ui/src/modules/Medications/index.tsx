import React, { useEffect, useState } from 'react'
import { AppBar, Box, Button, Container, MenuItem, Stack, TextField, Toolbar, Typography } from '@mui/material'
import { addMyMedication, archiveMedication, listMyMedications, logMedication, Medication, MedicationScheduleType } from '../../codex-example/api/medications'
import { useAuth } from '../Auth/useAuth'
import { useNavigate } from 'react-router-dom'

const Medications: React.FC = () => {
  const { token, logout } = useAuth()
  const nav = useNavigate()
  const [list, setList] = useState<Medication[]>([])
  const [form, setForm] = useState<{ name: string; strength?: string; scheduleType: MedicationScheduleType; scheduleTimes?: string }>({ name: '', strength: '', scheduleType: 'SCHEDULED', scheduleTimes: '' })

  const load = async () => {
    if (!token) return
    const meds = await listMyMedications()
    setList(meds)
  }
  useEffect(() => { load() }, [token])

  const onAdd = async (e: React.FormEvent) => {
    e.preventDefault()
    const created = await addMyMedication(form)
    setList(prev => [...prev, created])
    setForm({ name: '', strength: '', scheduleType: 'SCHEDULED', scheduleTimes: '' })
  }

  const onArchive = async (m: Medication) => {
    await archiveMedication(m.id)
    setList(prev => prev.filter(x => x.id !== m.id))
  }

  const onLog = async (m: Medication) => {
    const reason = m.scheduleType === 'PRN' ? window.prompt('Reason/Symptom (required for PRN):') ?? undefined : undefined
    await logMedication(m.id, new Date().toISOString(), reason)
    alert('Logged')
  }

  return (
    <>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>Medications</Typography>
          <Button color="inherit" onClick={()=>nav('/')}>Dashboard</Button>
          <Button color="inherit" onClick={()=>nav('/contacts')}>Contacts</Button>
          {token ? (
            <Button color="inherit" onClick={logout}>Logout</Button>
          ) : (
            <Button color="inherit" onClick={()=>nav('/login')}>Login</Button>
          )}
        </Toolbar>
      </AppBar>
      <Container sx={{ mt: 4 }}>
        <Typography variant="h5" gutterBottom>Medication List</Typography>
        <Box component="form" onSubmit={onAdd} sx={{ mb: 3 }}>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
            <TextField label="Name" value={form.name} onChange={e=>setForm(f=>({...f, name: e.target.value}))} required />
            <TextField label="Strength" value={form.strength} onChange={e=>setForm(f=>({...f, strength: e.target.value}))} />
            <TextField select label="Type" value={form.scheduleType} onChange={e=>setForm(f=>({...f, scheduleType: e.target.value as MedicationScheduleType}))}>
              <MenuItem value="SCHEDULED">Scheduled</MenuItem>
              <MenuItem value="PRN">PRN</MenuItem>
            </TextField>
            {form.scheduleType === 'SCHEDULED' && (
              <TextField label="Times (CSV HH:mm)" value={form.scheduleTimes} onChange={e=>setForm(f=>({...f, scheduleTimes: e.target.value}))} />
            )}
            <Button type="submit" variant="contained">Add</Button>
          </Stack>
        </Box>

        <Stack spacing={1}>
          {list.map(m => (
            <Box key={m.id} sx={{ p:2, border:'1px solid #ddd', borderRadius:1, display:'flex', alignItems:'center', justifyContent:'space-between' }}>
              <Box>
                <Typography variant="subtitle1">{m.name} {m.strength && `— ${m.strength}`}</Typography>
                <Typography variant="body2" color="text.secondary">{m.scheduleType === 'PRN' ? 'PRN' : (m.scheduleTimes || '')}</Typography>
              </Box>
              <Box>
                <Button sx={{ mr: 1 }} onClick={()=>onLog(m)} variant="outlined">Log</Button>
                <Button color="error" onClick={()=>onArchive(m)} variant="outlined">Archive</Button>
              </Box>
            </Box>
          ))}
        </Stack>
      </Container>
    </>
  )
}

export default Medications


import React, { useEffect, useRef, useState } from 'react'
import { AppBar, Box, Button, Container, Dialog, DialogActions, DialogContent, DialogTitle, Stack, TextField, Toolbar, Typography } from '@mui/material'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../Auth/useAuth'
import { addMySymptom, listMySymptoms, SymptomEntry } from '../../codex-example/api/symptoms'
import { uploadPhoto } from '../../codex-example/api/uploads'

const Symptoms: React.FC = () => {
  const { token, logout } = useAuth()
  const nav = useNavigate()
  const [list, setList] = useState<SymptomEntry[]>([])
  const [form, setForm] = useState<{ at: string; tags: string; painScore?: number; notes?: string; temperatureC?: number; bloodPressure?: string; pulse?: number; respiration?: number; photoUrl?: string }>({
    at: new Date().toISOString(), tags: ''
  })
  const [consentOpen, setConsentOpen] = useState(false)
  const [pendingFile, setPendingFile] = useState<File | null>(null)
  const fileRef = useRef<HTMLInputElement>(null)
  const [filters, setFilters] = useState<{ from?: string; to?: string; tag?: string }>({})

  const load = async () => {
    if (!token) return
    const res = await listMySymptoms({ from: filters.from, to: filters.to, tag: filters.tag })
    setList(res)
  }
  useEffect(() => { load() }, [token])

  const onAdd = async (e: React.FormEvent) => {
    e.preventDefault()
    const created = await addMySymptom({
      at: form.at,
      tags: form.tags ? form.tags.split(',').map(s=>s.trim()).filter(Boolean) : [],
      painScore: form.painScore,
      notes: form.notes,
      temperatureC: form.temperatureC,
      bloodPressure: form.bloodPressure,
      pulse: form.pulse,
      respiration: form.respiration,
      photoUrl: form.photoUrl,
    })
    setList(prev => [created, ...prev])
    setForm({ at: new Date().toISOString(), tags: '' })
  }

  const onFilter = async (e: React.FormEvent) => {
    e.preventDefault()
    await load()
  }

  return (
    <>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>Symptoms</Typography>
          <Button color="inherit" onClick={()=>nav('/')}>Dashboard</Button>
          <Button color="inherit" onClick={()=>nav('/medications')}>Medications</Button>
          <Button color="inherit" onClick={()=>nav('/visits')}>Visits</Button>
          {token ? (
            <Button color="inherit" onClick={logout}>Logout</Button>
          ) : (
            <Button color="inherit" onClick={()=>nav('/login')}>Login</Button>
          )}
        </Toolbar>
      </AppBar>
      <Container sx={{ mt: 4 }}>
        <Typography variant="h5" gutterBottom>Add Symptom Entry</Typography>
        <Box component="form" onSubmit={onAdd} sx={{ mb: 3 }}>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
            <TextField label="At (ISO)" value={form.at} onChange={e=>setForm(f=>({...f, at: e.target.value}))} required fullWidth />
            <TextField label="Tags (CSV)" value={form.tags} onChange={e=>setForm(f=>({...f, tags: e.target.value}))} fullWidth />
            <TextField label="Pain (0-10)" type="number" inputProps={{ min:0, max:10 }} value={form.painScore ?? ''} onChange={e=>setForm(f=>({...f, painScore: e.target.value? Number(e.target.value): undefined}))} />
          </Stack>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mt: 2 }}>
            <TextField label="Notes" value={form.notes ?? ''} onChange={e=>setForm(f=>({...f, notes: e.target.value}))} fullWidth />
          </Stack>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mt: 2 }}>
            <TextField label="Temp (°C)" type="number" value={form.temperatureC ?? ''} onChange={e=>setForm(f=>({...f, temperatureC: e.target.value? Number(e.target.value): undefined}))} />
            <TextField label="BP (e.g., 120/80)" value={form.bloodPressure ?? ''} onChange={e=>setForm(f=>({...f, bloodPressure: e.target.value}))} />
            <TextField label="Pulse" type="number" value={form.pulse ?? ''} onChange={e=>setForm(f=>({...f, pulse: e.target.value? Number(e.target.value): undefined}))} />
            <TextField label="Respiration" type="number" value={form.respiration ?? ''} onChange={e=>setForm(f=>({...f, respiration: e.target.value? Number(e.target.value): undefined}))} />
            <input ref={fileRef} type="file" accept="image/*" capture="environment" style={{ display:'none' }} onChange={async e=>{
              const f = e.target.files?.[0]
              if (!f) return
              if (!localStorage.getItem('photoConsentAccepted')) { setPendingFile(f); setConsentOpen(true); return }
              const res = await uploadPhoto(f)
              setForm(prev=>({ ...prev, photoUrl: res.url }))
            }} />
            <Button variant="outlined" onClick={()=>fileRef.current?.click()}>Attach Photo</Button>
            <Button type="submit" variant="contained">Add</Button>
          </Stack>
        </Box>

        <Typography variant="h6">Filter</Typography>
        <Box component="form" onSubmit={onFilter} sx={{ mb: 2 }}>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
            <TextField label="From (ISO)" value={filters.from ?? ''} onChange={e=>setFilters(f=>({...f, from: e.target.value || undefined}))} />
            <TextField label="To (ISO)" value={filters.to ?? ''} onChange={e=>setFilters(f=>({...f, to: e.target.value || undefined}))} />
            <TextField label="Tag" value={filters.tag ?? ''} onChange={e=>setFilters(f=>({...f, tag: e.target.value || undefined}))} />
            <Button type="submit" variant="outlined">Apply</Button>
          </Stack>
        </Box>

        <Stack spacing={1}>
          {list.map((e) => (
            <Box key={e.id} sx={{ p:2, border:'1px solid #ddd', borderRadius:1 }}>
              <Typography variant="subtitle1">{new Date(e.at).toLocaleString()} — Pain: {e.painScore ?? 'n/a'}</Typography>
              <Typography variant="body2" color="text.secondary">{e.tags.join(', ')}</Typography>
              {e.notes && <Typography variant="body2">{e.notes}</Typography>}
              {e.photoUrl && <Box sx={{ mt: 1 }}><img src={e.photoUrl} alt="symptom" style={{ maxWidth:'100%', borderRadius:4 }} /></Box>}
            </Box>
          ))}
        </Stack>
      </Container>
      <Dialog open={consentOpen} onClose={()=>setConsentOpen(false)}>
        <DialogTitle>Consent Required</DialogTitle>
        <DialogContent>
          <Typography variant="body2">By uploading photos, you acknowledge they may contain Protected Health Information (PHI). Photos are stored encrypted and only accessible to authorized users. Do you consent to store such photos?</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={()=>{ setConsentOpen(false); setPendingFile(null) }}>Cancel</Button>
          <Button variant="contained" onClick={async ()=>{
            localStorage.setItem('photoConsentAccepted','true')
            setConsentOpen(false)
            if (pendingFile) {
              const res = await uploadPhoto(pendingFile)
              setForm(prev=>({ ...prev, photoUrl: res.url }))
              setPendingFile(null)
            }
          }}>I Consent</Button>
        </DialogActions>
      </Dialog>
    </>
  )
}

export default Symptoms

import React, { useEffect, useState } from 'react'
import { AppBar, Box, Button, Container, MenuItem, Stack, TextField, Toolbar, Typography } from '@mui/material'
import { addMyContact, Contact, ContactRole, listMyContacts } from '../../codex-example/api/contacts'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../Auth/useAuth'

const roleOptions: { value: ContactRole; label: string }[] = [
  { value: 'NURSE', label: 'Nurse' },
  { value: 'HOSPICE_HOTLINE', label: 'Hospice Hotline' },
  { value: 'SOCIAL_WORKER', label: 'Social Worker' },
  { value: 'PHARMACY', label: 'Pharmacy' },
  { value: 'PHYSICIAN', label: 'Physician' },
]

const Contacts: React.FC = () => {
  const nav = useNavigate()
  const { token, logout } = useAuth()
  const [contacts, setContacts] = useState<Contact[]>([])
  const [form, setForm] = useState<{ name: string; role: ContactRole; phone: string }>({ name: '', role: 'NURSE', phone: '' })

  const load = async () => {
    if (!token) return
    const data = await listMyContacts()
    setContacts(data)
  }

  useEffect(() => { load() }, [token])

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    const created = await addMyContact(form)
    setContacts(prev => [...prev, created].sort((a, b) => a.name.localeCompare(b.name)))
    setForm({ name: '', role: 'NURSE', phone: '' })
  }

  return (
    <>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>Contacts</Typography>
          <Button color="inherit" onClick={() => nav('/')}>Dashboard</Button>
          {token ? (
            <Button color="inherit" onClick={logout}>Logout</Button>
          ) : (
            <Button color="inherit" onClick={()=>nav('/login')}>Login</Button>
          )}
        </Toolbar>
      </AppBar>
      <Container sx={{ mt: 4 }}>
        <Typography variant="h5" gutterBottom>Care Team Contacts</Typography>
        <Box component="form" onSubmit={onSubmit} sx={{ mb: 3 }}>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
            <TextField label="Name" value={form.name} onChange={e=>setForm(f=>({...f, name: e.target.value}))} required />
            <TextField select label="Role" value={form.role} onChange={e=>setForm(f=>({...f, role: e.target.value as ContactRole}))}>
              {roleOptions.map(o => <MenuItem key={o.value} value={o.value}>{o.label}</MenuItem>)}
            </TextField>
            <TextField label="Phone" value={form.phone} onChange={e=>setForm(f=>({...f, phone: e.target.value}))} required />
            <Button type="submit" variant="contained">Add</Button>
          </Stack>
        </Box>

        <Stack spacing={1}>
          {contacts.map(c => (
            <Box key={c.id} sx={{ p: 2, border: '1px solid #ddd', borderRadius: 1, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <Box>
                <Typography variant="subtitle1">{c.name}</Typography>
                <Typography variant="body2" color="text.secondary">{c.role.replace('_',' ')}</Typography>
                <Typography variant="body2">{c.phone}</Typography>
              </Box>
              <Button component="a" href={`tel:${c.phone}`} variant="outlined">Call</Button>
            </Box>
          ))}
        </Stack>
      </Container>
    </>
  )
}

export default Contacts


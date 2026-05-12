import React, { useEffect, useState } from 'react'
import { AppBar, Box, Button, Container, Link, List, ListItem, ListItemText, Stack, Toolbar, Typography } from '@mui/material'
import { useAuth } from '../Auth/useAuth'
import { useNavigate } from 'react-router-dom'
import { getMyProfile } from '../../codex-example/api/patients'
import { Contact, listMyContacts } from '../../codex-example/api/contacts'

const Emergency: React.FC = () => {
  const { token, logout } = useAuth()
  const nav = useNavigate()
  const [contacts, setContacts] = useState<Contact[]>([])
  const [patientName, setPatientName] = useState<string>('')
  const [coords, setCoords] = useState<GeolocationCoordinates | null>(null)

  useEffect(() => {
    const load = async () => {
      if (!token) return
      const prof = await getMyProfile()
      setPatientName(prof.fullName)
      setContacts(await listMyContacts())
    }
    load()
  }, [token])

  const hotline = contacts.find(c => c.role === 'HOSPICE_HOTLINE')
  const nurse = contacts.find(c => c.role === 'NURSE')

  const requestLocation = () => {
    if (!navigator.geolocation) return alert('Geolocation not supported')
    navigator.geolocation.getCurrentPosition((pos) => {
      setCoords(pos.coords)
    }, () => alert('Unable to retrieve location'), { enableHighAccuracy: true })
  }

  return (
    <>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>Emergency Plan</Typography>
          <Button color="inherit" onClick={()=>nav('/')}>Dashboard</Button>
          {token ? (
            <Button color="inherit" onClick={logout}>Logout</Button>
          ) : (
            <Button color="inherit" onClick={()=>nav('/login')}>Login</Button>
          )}
        </Toolbar>
      </AppBar>
      <Container sx={{ mt: 3 }}>
        <Typography variant="h5" gutterBottom>What to do now</Typography>
        <Typography variant="subtitle1" gutterBottom>Patient: {patientName}</Typography>

        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mb: 2 }}>
          {hotline && <Button variant="contained" color="error" href={`tel:${hotline.phone}`}>Call Hospice Hotline</Button>}
          {nurse && <Button variant="outlined" href={`tel:${nurse.phone}`}>Call Primary Nurse</Button>}
          <Button variant="text" onClick={requestLocation}>{coords ? 'Location Ready' : 'Share My Location'}</Button>
        </Stack>

        {coords && (
          <Box sx={{ mb: 2 }}>
            <Typography variant="body2">Approximate Location: {coords.latitude.toFixed(5)}, {coords.longitude.toFixed(5)} (not stored)</Typography>
          </Box>
        )}

        <Typography variant="h6">Urgent Scenarios</Typography>
        <List>
          <ListItem>
            <ListItemText primary="Uncontrolled Pain" secondary={<Link href="/education?topic=pain">See guidance</Link>} />
          </ListItem>
          <ListItem>
            <ListItemText primary="Breathing Distress" secondary={<Link href="/education?topic=breathing">See guidance</Link>} />
          </ListItem>
          <ListItem>
            <ListItemText primary="Falls or Injury" secondary={<Link href="/education?topic=falls">See guidance</Link>} />
          </ListItem>
        </List>
      </Container>
    </>
  )
}

export default Emergency


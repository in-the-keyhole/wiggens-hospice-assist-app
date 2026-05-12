import React from 'react'
import { AppBar, Button, Container, Toolbar, Typography } from '@mui/material'
import { useAuth } from '../Auth/useAuth'
import { useNavigate } from 'react-router-dom'

const Dashboard: React.FC = () => {
  const { token, logout } = useAuth()
  const nav = useNavigate()
  return (
    <>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>Wiggens Dashboard</Typography>
          <Button color="inherit" onClick={()=>nav('/contacts')}>Contacts</Button>
          <Button color="inherit" onClick={()=>nav('/medications')}>Medications</Button>
          <Button color="inherit" onClick={()=>nav('/visits')}>Visits</Button>
          <Button color="inherit" onClick={()=>nav('/checklist')}>Checklist</Button>
          <Button color="inherit" onClick={()=>nav('/emergency')}>Emergency</Button>
          {token ? (
            <Button color="inherit" onClick={logout}>Logout</Button>
          ) : (
            <Button color="inherit" onClick={()=>nav('/login')}>Login</Button>
          )}
        </Toolbar>
      </AppBar>
      <Container sx={{ mt: 4 }}>
        <Typography>Welcome {token ? 'back!' : '— please sign in.'}</Typography>
      </Container>
    </>
  )
}

export default Dashboard

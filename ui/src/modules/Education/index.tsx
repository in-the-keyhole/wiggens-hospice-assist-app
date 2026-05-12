import React, { useMemo, useState } from 'react'
import { AppBar, Box, Button, Chip, Container, Stack, TextField, Toolbar, Typography } from '@mui/material'
import { useAuth } from '../Auth/useAuth'
import { useNavigate, useSearchParams } from 'react-router-dom'

type Article = { id: string; title: string; topic: 'meds'|'comfort'|'hygiene'|'equipment'; body: string }

const ARTICLES: Article[] = [
  { id: 'pain-basics', title: 'Managing Pain Basics', topic: 'meds', body: 'Short tips on dosing, contact nurse if pain persists.' },
  { id: 'breathing-ease', title: 'Easing Breathing Distress', topic: 'comfort', body: 'Positioning, fan, call hotline if severe.' },
  { id: 'oral-care', title: 'Oral Care Checklist', topic: 'hygiene', body: 'Brush gently, moisten lips, avoid alcohol rinses.' },
  { id: 'bed-reposition', title: 'Safe Repositioning', topic: 'equipment', body: 'Use draw sheet, move in stages, protect skin.' },
]

const Education: React.FC = () => {
  const { token, logout } = useAuth()
  const nav = useNavigate()
  const [params] = useSearchParams()
  const [q, setQ] = useState(params.get('topic') || '')
  const [completed, setCompleted] = useState<string[]>(() => {
    try { return JSON.parse(localStorage.getItem('eduCompleted') || '[]') } catch { return [] }
  })

  const list = useMemo(() => {
    const needle = q.toLowerCase()
    return ARTICLES.filter(a => a.title.toLowerCase().includes(needle) || a.topic.includes(needle))
  }, [q])

  const toggleComplete = (id: string) => {
    setCompleted(prev => {
      const next = prev.includes(id) ? prev.filter(x => x!==id) : [...prev, id]
      localStorage.setItem('eduCompleted', JSON.stringify(next))
      return next
    })
  }

  return (
    <>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>Care Education</Typography>
          <Button color="inherit" onClick={()=>nav('/')}>Dashboard</Button>
          {token ? (
            <Button color="inherit" onClick={logout}>Logout</Button>
          ) : (
            <Button color="inherit" onClick={()=>nav('/login')}>Login</Button>
          )}
        </Toolbar>
      </AppBar>
      <Container sx={{ mt: 3 }}>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mb: 2 }}>
          <TextField label="Search or Topic (meds/comfort/hygiene/equipment)" value={q} onChange={e=>setQ(e.target.value)} fullWidth />
        </Stack>
        <Stack spacing={2}>
          {list.map(a => (
            <Box key={a.id} sx={{ p:2, border:'1px solid #ddd', borderRadius:1 }}>
              <Stack direction="row" spacing={1} alignItems="center">
                <Typography variant="h6" sx={{ flexGrow: 1 }}>{a.title}</Typography>
                <Chip label={a.topic} size="small" />
                <Button size="small" variant={completed.includes(a.id)?'contained':'outlined'} onClick={()=>toggleComplete(a.id)}>
                  {completed.includes(a.id)?'Completed':'Mark Complete'}
                </Button>
              </Stack>
              <Typography variant="body2" sx={{ mt: 1 }}>{a.body}</Typography>
            </Box>
          ))}
        </Stack>
      </Container>
    </>
  )
}

export default Education


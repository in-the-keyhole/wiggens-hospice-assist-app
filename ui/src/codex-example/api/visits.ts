import api from './axios'

export type Visit = {
  id: number
  at: string
  providerRole: string
  notes?: string
  status: 'UPCOMING' | 'COMPLETED'
  visitNotes?: string
  vitals?: string
  careChanges?: string
}

export async function listUpcomingVisits(): Promise<Visit[]> {
  const res = await api.get('/visits/me')
  return res.data
}

export async function listPastVisits(): Promise<Visit[]> {
  const res = await api.get('/visits/me/past')
  return res.data
}

export async function addVisit(input: { at: string; providerRole: string; notes?: string }): Promise<Visit> {
  const res = await api.post('/visits/me', input)
  return res.data
}

export async function completeVisit(id: number, input: { visitNotes?: string; vitals?: string; careChanges?: string }): Promise<Visit> {
  const res = await api.put(`/visits/${id}/complete`, input)
  return res.data
}


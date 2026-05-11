import api from './axios'

export type SymptomEntry = {
  id: number
  at: string
  tags: string[]
  notes?: string
  painScore?: number
  temperatureC?: number
  bloodPressure?: string
  pulse?: number
  respiration?: number
  photoUrl?: string
  createdAt: string
  updatedAt: string
}

export type SymptomEntryInput = Partial<Omit<SymptomEntry, 'id'|'createdAt'|'updatedAt'>> & { at: string }

export async function addMySymptom(input: SymptomEntryInput): Promise<SymptomEntry> {
  const res = await api.post('/symptoms/me', input)
  return res.data
}

export async function listMySymptoms(params?: { from?: string; to?: string; tag?: string | string[] }): Promise<SymptomEntry[]> {
  const res = await api.get('/symptoms/me', { params })
  return res.data
}


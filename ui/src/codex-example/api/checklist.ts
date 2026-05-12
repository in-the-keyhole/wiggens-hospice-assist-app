import api from './axios'

export type FrequencyType = 'ONCE' | 'TIMES_PER_DAY' | 'DAYS_OF_WEEK'

export type CareTask = {
  id: number
  name: string
  frequencyType: FrequencyType
  timesPerDay?: number
  daysOfWeek?: string
  notes?: string
  active: boolean
  createdAt: string
  updatedAt: string
}

export async function listMyTasks(): Promise<CareTask[]> {
  const res = await api.get('/checklist/me')
  return res.data
}

export async function addMyTask(input: { name: string; frequencyType: FrequencyType; timesPerDay?: number; daysOfWeek?: string; notes?: string }): Promise<CareTask> {
  const res = await api.post('/checklist/me', input)
  return res.data
}

export async function completeTask(id: number, at: string): Promise<void> {
  await api.post(`/checklist/${id}/complete`, { at })
}

export type CompletionEntry = { id: number; taskId: number; taskName: string; completedAt: string }
export async function listHistory(params?: { from?: string; to?: string }): Promise<CompletionEntry[]> {
  const res = await api.get('/checklist/history', { params })
  return res.data
}


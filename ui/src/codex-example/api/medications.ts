import api from './axios'

export type MedicationScheduleType = 'SCHEDULED' | 'PRN'

export type Medication = {
  id: number
  name: string
  strength?: string
  route?: string
  dosageInstructions?: string
  scheduleType: MedicationScheduleType
  scheduleTimes?: string
  prescribingInfo?: string
  specialInstructions?: string
  inventoryCount?: number
  active: boolean
  createdAt: string
  updatedAt: string
}

export async function listMyMedications(): Promise<Medication[]> {
  const res = await api.get('/medications/me')
  return res.data
}

export async function addMyMedication(input: Partial<Medication> & { name: string; scheduleType: MedicationScheduleType }): Promise<Medication> {
  const res = await api.post('/medications/me', input)
  return res.data
}

export async function archiveMedication(id: number): Promise<void> {
  await api.put(`/medications/${id}/archive`)
}

export async function logMedication(id: number, at: string, reason?: string, photoUrl?: string): Promise<void> {
  await api.post(`/medications/${id}/logs`, { at, reason, photoUrl })
}

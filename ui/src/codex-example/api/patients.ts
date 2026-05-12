import api from './axios'

export type PatientProfile = {
  id: number
  fullName: string
  contactEmail?: string
  contactPhone?: string
  hospiceOrganization?: string
}

export async function getMyProfile(): Promise<PatientProfile> {
  const res = await api.get('/patients/me')
  return res.data
}


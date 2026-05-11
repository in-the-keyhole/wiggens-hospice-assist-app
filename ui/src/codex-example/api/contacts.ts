import api from './axios'

export type ContactRole = 'NURSE' | 'HOSPICE_HOTLINE' | 'SOCIAL_WORKER' | 'PHARMACY' | 'PHYSICIAN'

export type Contact = {
  id: number
  name: string
  role: ContactRole
  phone: string
  createdAt: string
  updatedAt: string
}

export async function listMyContacts(): Promise<Contact[]> {
  const res = await api.get('/contacts/me')
  return res.data
}

export async function addMyContact(input: { name: string; role: ContactRole; phone: string }): Promise<Contact> {
  const res = await api.post('/contacts/me', input)
  return res.data
}


import api from './axios'

export type UploadResponse = { id: number; url: string }

export async function uploadPhoto(file: File): Promise<UploadResponse> {
  const form = new FormData()
  form.append('file', file)
  const res = await api.post('/uploads', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return res.data
}


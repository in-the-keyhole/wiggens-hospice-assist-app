import { afterEach, beforeEach, expect, it, vi } from 'vitest'
import { enqueueLog, flush, read } from './offlineQueue'

beforeEach(() => {
  localStorage.clear()
})

afterEach(() => {
  localStorage.clear()
})

it('enqueues and flushes successfully', async () => {
  enqueueLog({ id: 1, at: '2024-01-01T00:00:00Z' })
  expect(read().length).toBe(1)
  const send = vi.fn().mockResolvedValue(undefined)
  await flush(send)
  expect(send).toHaveBeenCalledTimes(1)
  expect(read().length).toBe(0)
})

it('keeps failed items', async () => {
  enqueueLog({ id: 1, at: '2024-01-01T00:00:00Z' })
  const send = vi.fn().mockRejectedValue(new Error('offline'))
  await flush(send)
  expect(send).toHaveBeenCalledTimes(1)
  expect(read().length).toBe(1)
})


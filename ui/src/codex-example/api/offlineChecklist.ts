type PendingCompletion = { id: number; at: string }

const KEY = 'offlineChecklistCompletions'

export function enqueueCompletion(entry: PendingCompletion) {
  const arr = read()
  arr.push(entry)
  localStorage.setItem(KEY, JSON.stringify(arr))
}

export function read(): PendingCompletion[] {
  try {
    return JSON.parse(localStorage.getItem(KEY) || '[]')
  } catch {
    return []
  }
}

export async function flush(send: (e: PendingCompletion)=>Promise<void>) {
  const arr = read()
  const remaining: PendingCompletion[] = []
  for (const e of arr) {
    try {
      await send(e)
    } catch {
      remaining.push(e)
    }
  }
  localStorage.setItem(KEY, JSON.stringify(remaining))
}


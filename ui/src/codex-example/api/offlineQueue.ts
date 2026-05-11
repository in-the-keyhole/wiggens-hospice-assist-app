type PendingLog = { id: number; at: string; reason?: string }

const KEY = 'offlineMedLogs'

export function enqueueLog(log: PendingLog) {
  const arr = read()
  arr.push(log)
  localStorage.setItem(KEY, JSON.stringify(arr))
}

export function read(): PendingLog[] {
  try {
    return JSON.parse(localStorage.getItem(KEY) || '[]')
  } catch {
    return []
  }
}

export async function flush(send: (l: PendingLog)=>Promise<void>) {
  const arr = read()
  const remaining: PendingLog[] = []
  for (const l of arr) {
    try {
      await send(l)
    } catch {
      remaining.push(l)
    }
  }
  localStorage.setItem(KEY, JSON.stringify(remaining))
}


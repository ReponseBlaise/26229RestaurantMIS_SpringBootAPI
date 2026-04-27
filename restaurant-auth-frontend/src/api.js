const BASE = ''

export function parseError(payload) {
  if (payload?.errors) {
    return Object.values(payload.errors).join(', ')
  }
  return payload?.message ?? 'Request failed'
}

export async function api(path, options = {}) {
  const user = JSON.parse(sessionStorage.getItem('authenticatedUser') || 'null')
  const headers = { 'Content-Type': 'application/json', ...options.headers }
  if (user?.id) headers['X-User-Id'] = user.id

  const res = await fetch(`${BASE}${path}`, { ...options, headers })
  const payload = await res.json()
  if (!res.ok) throw new Error(parseError(payload))
  return payload?.data ?? payload
}

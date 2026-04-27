import { useEffect, useState } from 'react'
import { toast } from 'react-toastify'
import { api } from '../api'

const TYPES = ['PROVINCE', 'DISTRICT', 'SECTOR', 'CELL', 'VILLAGE']
const empty = { name: '', code: '', type: 'PROVINCE', parentId: '' }

export default function Locations() {
  const [locations, setLocations] = useState([])
  const [form, setForm] = useState(empty)
  const [loading, setLoading] = useState(false)

  const load = async () => {
    try { const d = await api('/api/locations'); setLocations(d) } catch (e) { toast.error(e.message) }
  }

  useEffect(() => { load() }, [])

  const handleSubmit = async (e) => {
    e.preventDefault(); setLoading(true)
    try {
      const body = { ...form, parentId: form.parentId ? parseInt(form.parentId) : null }
      await api('/api/locations', { method: 'POST', body: JSON.stringify(body) })
      toast.success('Location created'); setForm(empty); load()
    } catch (e) { toast.error(e.message) } finally { setLoading(false) }
  }

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold text-emerald-950">Locations</h2>

      <form onSubmit={handleSubmit} className="grid gap-3 rounded-2xl border border-emerald-200 bg-white p-5 sm:grid-cols-2">
        <input placeholder="Name *" value={form.name} onChange={e => setForm(p => ({ ...p, name: e.target.value }))} className="input" required />
        <input placeholder="Code * (e.g. KIG-SEC-017)" value={form.code} onChange={e => setForm(p => ({ ...p, code: e.target.value }))} className="input" required />
        <select value={form.type} onChange={e => setForm(p => ({ ...p, type: e.target.value }))} className="input">
          {TYPES.map(t => <option key={t} value={t}>{t}</option>)}
        </select>
        <select value={form.parentId} onChange={e => setForm(p => ({ ...p, parentId: e.target.value }))} className="input">
          <option value="">No Parent</option>
          {locations.map(l => <option key={l.id} value={l.id}>{l.name} ({l.type})</option>)}
        </select>
        <button type="submit" disabled={loading} className="btn-primary sm:col-span-2">Add Location</button>
      </form>

      <div className="overflow-x-auto rounded-2xl border border-emerald-200 bg-white">
        <table className="w-full text-sm">
          <thead className="bg-emerald-50 text-emerald-900">
            <tr>{['Name', 'Code', 'Type', 'Parent'].map(h => <th key={h} className="px-4 py-3 text-left font-semibold">{h}</th>)}</tr>
          </thead>
          <tbody>
            {locations.map(l => (
              <tr key={l.id} className="border-t border-emerald-100">
                <td className="px-4 py-3">{l.name}</td>
                <td className="px-4 py-3">{l.code}</td>
                <td className="px-4 py-3">{l.type}</td>
                <td className="px-4 py-3">{l.parentName ?? '—'}</td>
              </tr>
            ))}
            {locations.length === 0 && <tr><td colSpan={4} className="px-4 py-6 text-center text-emerald-700/60">No locations yet.</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  )
}

import { useEffect, useState } from 'react'
import { toast } from 'react-toastify'
import { api } from '../api'

const empty = { name: '', phone: '', email: '', streetAddress: '' }

export default function Customers() {
  const [customers, setCustomers] = useState([])
  const [form, setForm] = useState(empty)
  const [editId, setEditId] = useState(null)
  const [loading, setLoading] = useState(false)

  const load = async () => {
    try {
      const data = await api('/api/customers')
      setCustomers(data.content ?? data)
    } catch (e) { toast.error(e.message) }
  }

  useEffect(() => { load() }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      if (editId) {
        await api(`/api/customers/${editId}`, { method: 'PUT', body: JSON.stringify(form) })
        toast.success('Customer updated')
      } else {
        await api('/api/customers', { method: 'POST', body: JSON.stringify(form) })
        toast.success('Customer created')
      }
      setForm(empty); setEditId(null); load()
    } catch (e) { toast.error(e.message) } finally { setLoading(false) }
  }

  const handleDelete = async (id) => {
    if (!confirm('Delete this customer?')) return
    try {
      await api(`/api/customers/${id}`, { method: 'DELETE' })
      toast.success('Deleted'); load()
    } catch (e) { toast.error(e.message) }
  }

  const startEdit = (c) => {
    setEditId(c.id)
    setForm({ name: c.name, phone: c.phone, email: c.email ?? '', streetAddress: c.streetAddress ?? '' })
  }

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold text-emerald-950">Customers</h2>

      <form onSubmit={handleSubmit} className="grid gap-3 rounded-2xl border border-emerald-200 bg-white p-5 sm:grid-cols-2">
        <input placeholder="Full Name *" value={form.name} onChange={e => setForm(p => ({ ...p, name: e.target.value }))} className="input" required />
        <input placeholder="Phone (+250...)" value={form.phone} onChange={e => setForm(p => ({ ...p, phone: e.target.value }))} className="input" required />
        <input placeholder="Email (optional)" type="email" value={form.email} onChange={e => setForm(p => ({ ...p, email: e.target.value }))} className="input" />
        <input placeholder="Street Address (optional)" value={form.streetAddress} onChange={e => setForm(p => ({ ...p, streetAddress: e.target.value }))} className="input" />
        <div className="flex gap-2 sm:col-span-2">
          <button type="submit" disabled={loading} className="btn-primary">{editId ? 'Update' : 'Add Customer'}</button>
          {editId && <button type="button" onClick={() => { setEditId(null); setForm(empty) }} className="btn-secondary">Cancel</button>}
        </div>
      </form>

      <div className="overflow-x-auto rounded-2xl border border-emerald-200 bg-white">
        <table className="w-full text-sm">
          <thead className="bg-emerald-50 text-emerald-900">
            <tr>{['Name', 'Phone', 'Email', 'Address', 'Actions'].map(h => <th key={h} className="px-4 py-3 text-left font-semibold">{h}</th>)}</tr>
          </thead>
          <tbody>
            {customers.map(c => (
              <tr key={c.id} className="border-t border-emerald-100">
                <td className="px-4 py-3">{c.name}</td>
                <td className="px-4 py-3">{c.phone}</td>
                <td className="px-4 py-3">{c.email ?? '—'}</td>
                <td className="px-4 py-3">{c.streetAddress ?? '—'}</td>
                <td className="px-4 py-3">
                  <div className="flex gap-2">
                    <button onClick={() => startEdit(c)} className="btn-xs-secondary">Edit</button>
                    <button onClick={() => handleDelete(c.id)} className="btn-xs-danger">Delete</button>
                  </div>
                </td>
              </tr>
            ))}
            {customers.length === 0 && (
              <tr><td colSpan={5} className="px-4 py-6 text-center text-emerald-700/60">No customers yet.</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}

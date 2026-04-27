import { useEffect, useState } from 'react'
import { toast } from 'react-toastify'
import { api } from '../api'

const STATUSES = ['PENDING', 'CONFIRMED', 'PREPARING', 'READY', 'DELIVERED', 'CANCELLED']

export default function Orders() {
  const [orders, setOrders] = useState([])
  const [customers, setCustomers] = useState([])
  const [menuItems, setMenuItems] = useState([])
  const [form, setForm] = useState({ customerId: '', tableNumber: '', items: [{ menuItemId: '', quantity: 1, specialInstructions: '' }] })
  const [loading, setLoading] = useState(false)

  const load = async () => {
    try {
      const [o, c, m] = await Promise.all([
        api('/api/orders'),
        api('/api/customers'),
        api('/api/menu-items'),
      ])
      setOrders(o.content ?? o)
      setCustomers(c.content ?? c)
      setMenuItems(m.content ?? m)
    } catch (e) { toast.error(e.message) }
  }

  useEffect(() => { load() }, [])

  const addItem = () => setForm(p => ({ ...p, items: [...p.items, { menuItemId: '', quantity: 1, specialInstructions: '' }] }))
  const removeItem = (i) => setForm(p => ({ ...p, items: p.items.filter((_, idx) => idx !== i) }))
  const updateItem = (i, field, val) => setForm(p => {
    const items = [...p.items]; items[i] = { ...items[i], [field]: val }; return { ...p, items }
  })

  const handleSubmit = async (e) => {
    e.preventDefault(); setLoading(true)
    try {
      const body = {
        customerId: parseInt(form.customerId),
        tableNumber: parseInt(form.tableNumber),
        items: form.items.map(it => ({ menuItemId: parseInt(it.menuItemId), quantity: parseInt(it.quantity), specialInstructions: it.specialInstructions || null })),
      }
      await api('/api/orders', { method: 'POST', body: JSON.stringify(body) })
      toast.success('Order created')
      setForm({ customerId: '', tableNumber: '', items: [{ menuItemId: '', quantity: 1, specialInstructions: '' }] })
      load()
    } catch (e) { toast.error(e.message) } finally { setLoading(false) }
  }

  const updateStatus = async (id, status) => {
    try {
      await api(`/api/orders/${id}/status?status=${status}`, { method: 'PUT' })
      toast.success('Status updated'); load()
    } catch (e) { toast.error(e.message) }
  }

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold text-emerald-950">Orders</h2>

      <form onSubmit={handleSubmit} className="rounded-2xl border border-emerald-200 bg-white p-5 space-y-3">
        <div className="grid gap-3 sm:grid-cols-2">
          <select value={form.customerId} onChange={e => setForm(p => ({ ...p, customerId: e.target.value }))} className="input" required>
            <option value="">Select Customer *</option>
            {customers.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
          <input placeholder="Table Number *" type="number" min="1" value={form.tableNumber} onChange={e => setForm(p => ({ ...p, tableNumber: e.target.value }))} className="input" required />
        </div>

        <p className="text-sm font-semibold text-emerald-950">Order Items</p>
        {form.items.map((item, i) => (
          <div key={i} className="grid gap-2 sm:grid-cols-3 items-end">
            <select value={item.menuItemId} onChange={e => updateItem(i, 'menuItemId', e.target.value)} className="input" required>
              <option value="">Select Menu Item *</option>
              {menuItems.map(m => <option key={m.id} value={m.id}>{m.name} — {m.price}</option>)}
            </select>
            <input placeholder="Qty" type="number" min="1" value={item.quantity} onChange={e => updateItem(i, 'quantity', e.target.value)} className="input" required />
            <div className="flex gap-2">
              <input placeholder="Special instructions" value={item.specialInstructions} onChange={e => updateItem(i, 'specialInstructions', e.target.value)} className="input flex-1" />
              {form.items.length > 1 && <button type="button" onClick={() => removeItem(i)} className="btn-xs-danger">✕</button>}
            </div>
          </div>
        ))}
        <div className="flex gap-2">
          <button type="button" onClick={addItem} className="btn-secondary text-sm">+ Add Item</button>
          <button type="submit" disabled={loading} className="btn-primary">Place Order</button>
        </div>
      </form>

      <div className="overflow-x-auto rounded-2xl border border-emerald-200 bg-white">
        <table className="w-full text-sm">
          <thead className="bg-emerald-50 text-emerald-900">
            <tr>{['Order #', 'Customer', 'Table', 'Total', 'Status', 'Date', 'Update Status'].map(h => <th key={h} className="px-4 py-3 text-left font-semibold">{h}</th>)}</tr>
          </thead>
          <tbody>
            {orders.map(o => (
              <tr key={o.id} className="border-t border-emerald-100">
                <td className="px-4 py-3">{o.orderNumber}</td>
                <td className="px-4 py-3">{o.customerName}</td>
                <td className="px-4 py-3">{o.tableNumber}</td>
                <td className="px-4 py-3">{o.totalAmount}</td>
                <td className="px-4 py-3"><span className="rounded-full bg-emerald-100 px-2 py-0.5 text-xs font-semibold text-emerald-800">{o.status}</span></td>
                <td className="px-4 py-3">{new Date(o.orderDate).toLocaleDateString()}</td>
                <td className="px-4 py-3">
                  <select defaultValue={o.status} onChange={e => updateStatus(o.id, e.target.value)} className="input py-1 text-xs">
                    {STATUSES.map(s => <option key={s} value={s}>{s}</option>)}
                  </select>
                </td>
              </tr>
            ))}
            {orders.length === 0 && <tr><td colSpan={7} className="px-4 py-6 text-center text-emerald-700/60">No orders yet.</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  )
}

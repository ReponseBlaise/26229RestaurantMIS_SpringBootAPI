import { useEffect, useState } from 'react'
import { toast } from 'react-toastify'
import { api } from '../api'

const emptyItem = { name: '', description: '', price: '', category: '', isAvailable: true, preparationTime: 15 }
const emptyDeal = { name: '', description: '', dealPrice: '', menuItemIds: '', isActive: true }

export default function Menu() {
  const [items, setItems] = useState([])
  const [deals, setDeals] = useState([])
  const [tab, setTab] = useState('items')
  const [itemForm, setItemForm] = useState(emptyItem)
  const [dealForm, setDealForm] = useState(emptyDeal)
  const [editId, setEditId] = useState(null)
  const [loading, setLoading] = useState(false)

  const loadItems = async () => {
    try { const d = await api('/api/menu-items'); setItems(d.content ?? d) } catch (e) { toast.error(e.message) }
  }
  const loadDeals = async () => {
    try { const d = await api('/api/menu-items/meal-deals'); setDeals(d.content ?? d) } catch (e) { toast.error(e.message) }
  }

  useEffect(() => { loadItems(); loadDeals() }, [])

  const handleItemSubmit = async (e) => {
    e.preventDefault(); setLoading(true)
    try {
      const body = { ...itemForm, price: parseFloat(itemForm.price), preparationTime: parseInt(itemForm.preparationTime) }
      if (editId) {
        await api(`/api/menu-items/${editId}`, { method: 'PUT', body: JSON.stringify(body) })
        toast.success('Menu item updated')
      } else {
        await api('/api/menu-items', { method: 'POST', body: JSON.stringify(body) })
        toast.success('Menu item created')
      }
      setItemForm(emptyItem); setEditId(null); loadItems()
    } catch (e) { toast.error(e.message) } finally { setLoading(false) }
  }

  const handleDealSubmit = async (e) => {
    e.preventDefault(); setLoading(true)
    try {
      const ids = dealForm.menuItemIds.split(',').map(s => parseInt(s.trim())).filter(Boolean)
      const body = { ...dealForm, dealPrice: parseFloat(dealForm.dealPrice), menuItemIds: ids }
      await api('/api/menu-items/meal-deals', { method: 'POST', body: JSON.stringify(body) })
      toast.success('Meal deal created')
      setDealForm(emptyDeal); loadDeals()
    } catch (e) { toast.error(e.message) } finally { setLoading(false) }
  }

  const handleDeleteItem = async (id) => {
    if (!confirm('Delete this item?')) return
    try { await api(`/api/menu-items/${id}`, { method: 'DELETE' }); toast.success('Deleted'); loadItems() }
    catch (e) { toast.error(e.message) }
  }

  const startEdit = (item) => {
    setEditId(item.id)
    setItemForm({ name: item.name, description: item.description ?? '', price: item.price, category: item.category, isAvailable: item.isAvailable, preparationTime: item.preparationTime })
    setTab('items')
  }

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold text-emerald-950">Menu</h2>

      <div className="grid grid-cols-2 rounded-xl border border-emerald-200 bg-white p-1 w-fit">
        {['items', 'deals'].map(t => (
          <button key={t} onClick={() => { setTab(t); setEditId(null); setItemForm(emptyItem) }}
            className={`rounded-lg px-5 py-2 text-sm font-semibold transition ${tab === t ? 'bg-emerald-800 text-emerald-50' : 'text-emerald-900 hover:bg-emerald-50'}`}>
            {t === 'items' ? 'Menu Items' : 'Meal Deals'}
          </button>
        ))}
      </div>

      {tab === 'items' && (
        <>
          <form onSubmit={handleItemSubmit} className="grid gap-3 rounded-2xl border border-emerald-200 bg-white p-5 sm:grid-cols-2">
            <input placeholder="Name *" value={itemForm.name} onChange={e => setItemForm(p => ({ ...p, name: e.target.value }))} className="input" required />
            <input placeholder="Category *" value={itemForm.category} onChange={e => setItemForm(p => ({ ...p, category: e.target.value }))} className="input" required />
            <input placeholder="Price *" type="number" step="0.01" value={itemForm.price} onChange={e => setItemForm(p => ({ ...p, price: e.target.value }))} className="input" required />
            <input placeholder="Prep Time (min)" type="number" value={itemForm.preparationTime} onChange={e => setItemForm(p => ({ ...p, preparationTime: e.target.value }))} className="input" />
            <input placeholder="Description" value={itemForm.description} onChange={e => setItemForm(p => ({ ...p, description: e.target.value }))} className="input sm:col-span-2" />
            <label className="flex items-center gap-2 text-sm text-emerald-900">
              <input type="checkbox" checked={itemForm.isAvailable} onChange={e => setItemForm(p => ({ ...p, isAvailable: e.target.checked }))} />
              Available
            </label>
            <div className="flex gap-2 sm:col-span-2">
              <button type="submit" disabled={loading} className="btn-primary">{editId ? 'Update Item' : 'Add Item'}</button>
              {editId && <button type="button" onClick={() => { setEditId(null); setItemForm(emptyItem) }} className="btn-secondary">Cancel</button>}
            </div>
          </form>

          <div className="overflow-x-auto rounded-2xl border border-emerald-200 bg-white">
            <table className="w-full text-sm">
              <thead className="bg-emerald-50 text-emerald-900">
                <tr>{['Name', 'Category', 'Price', 'Prep (min)', 'Available', 'Actions'].map(h => <th key={h} className="px-4 py-3 text-left font-semibold">{h}</th>)}</tr>
              </thead>
              <tbody>
                {items.map(item => (
                  <tr key={item.id} className="border-t border-emerald-100">
                    <td className="px-4 py-3">{item.name}</td>
                    <td className="px-4 py-3">{item.category}</td>
                    <td className="px-4 py-3">{item.price}</td>
                    <td className="px-4 py-3">{item.preparationTime}</td>
                    <td className="px-4 py-3">{item.isAvailable ? '✅' : '❌'}</td>
                    <td className="px-4 py-3">
                      <div className="flex gap-2">
                        <button onClick={() => startEdit(item)} className="btn-xs-secondary">Edit</button>
                        <button onClick={() => handleDeleteItem(item.id)} className="btn-xs-danger">Delete</button>
                      </div>
                    </td>
                  </tr>
                ))}
                {items.length === 0 && <tr><td colSpan={6} className="px-4 py-6 text-center text-emerald-700/60">No menu items yet.</td></tr>}
              </tbody>
            </table>
          </div>
        </>
      )}

      {tab === 'deals' && (
        <>
          <form onSubmit={handleDealSubmit} className="grid gap-3 rounded-2xl border border-emerald-200 bg-white p-5 sm:grid-cols-2">
            <input placeholder="Deal Name *" value={dealForm.name} onChange={e => setDealForm(p => ({ ...p, name: e.target.value }))} className="input" required />
            <input placeholder="Deal Price *" type="number" step="0.01" value={dealForm.dealPrice} onChange={e => setDealForm(p => ({ ...p, dealPrice: e.target.value }))} className="input" required />
            <input placeholder="Description" value={dealForm.description} onChange={e => setDealForm(p => ({ ...p, description: e.target.value }))} className="input sm:col-span-2" />
            <input placeholder="Menu Item IDs (comma-separated, e.g. 1,2,3) *" value={dealForm.menuItemIds} onChange={e => setDealForm(p => ({ ...p, menuItemIds: e.target.value }))} className="input sm:col-span-2" required />
            <button type="submit" disabled={loading} className="btn-primary sm:col-span-2">Create Meal Deal</button>
          </form>

          <div className="overflow-x-auto rounded-2xl border border-emerald-200 bg-white">
            <table className="w-full text-sm">
              <thead className="bg-emerald-50 text-emerald-900">
                <tr>{['Name', 'Deal Price', 'Active', 'Description'].map(h => <th key={h} className="px-4 py-3 text-left font-semibold">{h}</th>)}</tr>
              </thead>
              <tbody>
                {deals.map(d => (
                  <tr key={d.id} className="border-t border-emerald-100">
                    <td className="px-4 py-3">{d.name}</td>
                    <td className="px-4 py-3">{d.dealPrice}</td>
                    <td className="px-4 py-3">{d.isActive ? '✅' : '❌'}</td>
                    <td className="px-4 py-3">{d.description ?? '—'}</td>
                  </tr>
                ))}
                {deals.length === 0 && <tr><td colSpan={4} className="px-4 py-6 text-center text-emerald-700/60">No meal deals yet.</td></tr>}
              </tbody>
            </table>
          </div>
        </>
      )}
    </div>
  )
}

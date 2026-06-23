import { useState } from 'react'
import { useMutation } from '@tanstack/react-query'
import { X } from 'lucide-react'
import { createFolder } from '../../api/folders'

export default function CreateFolderModal({ parentId, onClose, onCreated }) {
  const [name, setName] = useState('')
  const [error, setError] = useState('')

  const mut = useMutation({
    mutationFn: () => createFolder(name.trim(), parentId),
    onSuccess: () => { setName(''); onCreated?.() },
    onError: (e) => setError(e.response?.data?.message ?? 'Error al crear la carpeta'),
  })

  const submit = (e) => {
    e.preventDefault()
    if (!name.trim()) return
    setError('')
    mut.mutate()
  }

  return (
    <div className="fixed inset-0 bg-black/30 z-50 flex items-center justify-center" onClick={onClose}>
      <form
        onSubmit={submit}
        className="bg-white rounded-xl shadow-xl p-6 w-80"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex items-center justify-between mb-4">
          <h2 className="font-semibold text-gray-900">Nueva carpeta</h2>
          <button type="button" onClick={onClose} className="text-gray-400 hover:text-gray-600">
            <X size={18} />
          </button>
        </div>

        <input
          autoFocus
          placeholder="Nombre de la carpeta"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm mb-1 focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
        {error && <p className="text-red-500 text-xs mb-2">{error}</p>}

        <div className="flex gap-2 justify-end mt-4">
          <button type="button" onClick={onClose} className="text-sm text-gray-500 px-3 py-1.5">
            Cancelar
          </button>
          <button
            type="submit"
            disabled={mut.isPending || !name.trim()}
            className="text-sm bg-indigo-600 hover:bg-indigo-700 disabled:opacity-60 text-white px-4 py-1.5 rounded-lg"
          >
            {mut.isPending ? 'Creando…' : 'Crear'}
          </button>
        </div>
      </form>
    </div>
  )
}

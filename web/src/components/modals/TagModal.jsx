import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Plus, X } from 'lucide-react'
import { listMyTags, createTag, listFileTags, addTagToFile, removeTagFromFile } from '../../api/tags'

export default function TagModal({ fileId, onClose }) {
  const [newTagName, setNewTagName] = useState('')
  const qc = useQueryClient()

  const { data: allTags = [] } = useQuery({ queryKey: ['tags'], queryFn: listMyTags })
  const { data: fileTags = [] } = useQuery({
    queryKey: ['file-tags', fileId],
    queryFn: () => listFileTags(fileId),
  })

  const fileTagIds = new Set(fileTags.map((t) => t.id))

  const addMut = useMutation({
    mutationFn: (tagId) => addTagToFile(fileId, tagId),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['file-tags', fileId] }),
  })

  const removeMut = useMutation({
    mutationFn: (tagId) => removeTagFromFile(fileId, tagId),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['file-tags', fileId] }),
  })

  const createMut = useMutation({
    mutationFn: () => createTag(newTagName.trim()),
    onSuccess: (tag) => {
      qc.invalidateQueries({ queryKey: ['tags'] })
      addMut.mutate(tag.id)
      setNewTagName('')
    },
  })

  const submitNew = (e) => {
    e.preventDefault()
    if (!newTagName.trim()) return
    createMut.mutate()
  }

  return (
    <div className="fixed inset-0 bg-black/30 z-50 flex items-center justify-center" onClick={onClose}>
      <div className="bg-white rounded-xl shadow-xl p-5 w-80" onClick={(e) => e.stopPropagation()}>
        <div className="flex items-center justify-between mb-4">
          <h2 className="font-semibold text-gray-900">Etiquetas</h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600"><X size={18} /></button>
        </div>

        {/* Current tags */}
        <div className="flex flex-wrap gap-1.5 mb-4 min-h-[28px]">
          {fileTags.map((tag) => (
            <span
              key={tag.id}
              className="flex items-center gap-1 bg-indigo-100 text-indigo-700 text-xs px-2 py-0.5 rounded-full"
            >
              {tag.name}
              <button
                onClick={() => removeMut.mutate(tag.id)}
                className="hover:text-indigo-900"
              >
                <X size={10} />
              </button>
            </span>
          ))}
          {fileTags.length === 0 && <span className="text-xs text-gray-400">Sin etiquetas</span>}
        </div>

        {/* All available tags */}
        <p className="text-xs font-medium text-gray-500 mb-1.5">Mis etiquetas</p>
        <div className="flex flex-wrap gap-1.5 mb-4 max-h-32 overflow-y-auto">
          {allTags.map((tag) => {
            const active = fileTagIds.has(tag.id)
            return (
              <button
                key={tag.id}
                onClick={() => (active ? removeMut.mutate(tag.id) : addMut.mutate(tag.id))}
                className={`text-xs px-2 py-0.5 rounded-full border transition-colors
                  ${active
                    ? 'bg-indigo-100 border-indigo-300 text-indigo-700'
                    : 'bg-gray-50 border-gray-200 text-gray-600 hover:border-indigo-300 hover:text-indigo-600'}`}
              >
                {tag.name}
              </button>
            )
          })}
          {allTags.length === 0 && <span className="text-xs text-gray-400">Ninguna creada aún</span>}
        </div>

        {/* Create new tag */}
        <form onSubmit={submitNew} className="flex gap-2">
          <input
            placeholder="Nueva etiqueta"
            value={newTagName}
            onChange={(e) => setNewTagName(e.target.value)}
            className="flex-1 border border-gray-300 rounded-lg px-2 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
          <button
            type="submit"
            disabled={!newTagName.trim() || createMut.isPending}
            className="bg-indigo-600 hover:bg-indigo-700 disabled:opacity-60 text-white px-3 py-1.5 rounded-lg text-sm"
          >
            <Plus size={14} />
          </button>
        </form>
      </div>
    </div>
  )
}

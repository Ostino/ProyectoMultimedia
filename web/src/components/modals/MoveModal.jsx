import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { ChevronRight, Folder, X } from 'lucide-react'
import { listFolders, moveFolder } from '../../api/folders'

function PickerNode({ folder, depth, selectedId, onSelect }) {
  const [expanded, setExpanded] = useState(false)
  const { data: children = [] } = useQuery({
    queryKey: ['folders', folder.id],
    queryFn: () => listFolders(folder.id),
    enabled: expanded,
  })

  return (
    <div>
      <div
        className={`flex items-center gap-1 px-2 py-1 rounded cursor-pointer text-sm
          ${selectedId === folder.id ? 'bg-indigo-50 text-indigo-700' : 'hover:bg-gray-50 text-gray-700'}`}
        style={{ paddingLeft: `${(depth + 1) * 14}px` }}
        onClick={() => onSelect(folder.id)}
      >
        <button
          className="p-0.5 text-gray-400"
          onClick={(e) => { e.stopPropagation(); setExpanded((v) => !v) }}
        >
          <ChevronRight size={12} className={`transition-transform ${expanded ? 'rotate-90' : ''}`} />
        </button>
        <Folder size={13} />
        <span className="truncate">{folder.name}</span>
      </div>
      {expanded && children.map((c) => (
        <PickerNode key={c.id} folder={c} depth={depth + 1} selectedId={selectedId} onSelect={onSelect} />
      ))}
    </div>
  )
}

export default function MoveModal({ folderId, currentParentId, onClose }) {
  const [destId, setDestId] = useState(currentParentId ?? null)
  const qc = useQueryClient()

  const { data: roots = [] } = useQuery({
    queryKey: ['folders', null],
    queryFn: () => listFolders(undefined),
  })

  const mut = useMutation({
    mutationFn: () => moveFolder(folderId, destId),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['folders'] })
      onClose()
    },
  })

  return (
    <div className="fixed inset-0 bg-black/30 z-50 flex items-center justify-center" onClick={onClose}>
      <div className="bg-white rounded-xl shadow-xl p-5 w-80" onClick={(e) => e.stopPropagation()}>
        <div className="flex items-center justify-between mb-4">
          <h2 className="font-semibold text-gray-900">Mover a…</h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600"><X size={18} /></button>
        </div>

        <div className="border border-gray-200 rounded-lg max-h-60 overflow-y-auto mb-4">
          {/* Root level option */}
          <div
            className={`flex items-center gap-2 px-3 py-2 cursor-pointer text-sm
              ${destId === null ? 'bg-indigo-50 text-indigo-700' : 'hover:bg-gray-50 text-gray-600'}`}
            onClick={() => setDestId(null)}
          >
            <Folder size={13} /> Raíz
          </div>
          {roots.map((f) => (
            <PickerNode key={f.id} folder={f} depth={0} selectedId={destId} onSelect={setDestId} />
          ))}
        </div>

        <div className="flex gap-2 justify-end">
          <button onClick={onClose} className="text-sm text-gray-500 px-3 py-1.5">Cancelar</button>
          <button
            disabled={mut.isPending}
            onClick={() => mut.mutate()}
            className="text-sm bg-indigo-600 hover:bg-indigo-700 disabled:opacity-60 text-white px-4 py-1.5 rounded-lg"
          >
            {mut.isPending ? 'Moviendo…' : 'Mover aquí'}
          </button>
        </div>
      </div>
    </div>
  )
}

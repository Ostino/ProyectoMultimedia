import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { ChevronRight, Folder, FolderOpen, MoreHorizontal, Plus } from 'lucide-react'
import { listFolders, createFolder, renameFolder, moveFolder, deleteFolder } from '../../api/folders'
import CreateFolderModal from '../modals/CreateFolderModal'
import MoveModal from '../modals/MoveModal'

// ── Inline rename mini-modal ──────────────────────────────────────────────────
function RenameFolderDialog({ folder, onDone }) {
  const [name, setName] = useState(folder.name)
  const qc = useQueryClient()
  const mut = useMutation({
    mutationFn: (n) => renameFolder(folder.id, n),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['folders'] })
      onDone()
    },
  })

  const save = (e) => {
    e.preventDefault()
    const trimmed = name.trim()
    if (!trimmed || trimmed === folder.name) { onDone(); return }
    mut.mutate(trimmed)
  }

  return (
    <div className="fixed inset-0 bg-black/30 z-50 flex items-center justify-center" onClick={onDone}>
      <form
        onSubmit={save}
        className="bg-white p-5 rounded-xl shadow-xl w-72"
        onClick={(e) => e.stopPropagation()}
      >
        <p className="font-medium text-sm mb-3">Renombrar carpeta</p>
        <input
          autoFocus
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm mb-3 focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
        <div className="flex gap-2 justify-end">
          <button type="button" onClick={onDone} className="text-sm text-gray-500 px-3 py-1.5">Cancelar</button>
          <button type="submit" disabled={mut.isPending} className="text-sm bg-indigo-600 text-white px-3 py-1.5 rounded-lg">
            {mut.isPending ? '…' : 'Guardar'}
          </button>
        </div>
      </form>
    </div>
  )
}

// ── Single folder row ─────────────────────────────────────────────────────────
function FolderNode({ folder, depth, selectedId, onSelect, onRefreshParent }) {
  const [expanded, setExpanded] = useState(false)
  const [menu, setMenu] = useState(false)
  const [showRename, setShowRename] = useState(false)
  const [showCreate, setShowCreate] = useState(false)
  const [showMove, setShowMove] = useState(false)
  const qc = useQueryClient()

  const { data: children = [] } = useQuery({
    queryKey: ['folders', folder.id],
    queryFn: () => listFolders(folder.id),
    enabled: expanded,
  })

  const del = useMutation({
    mutationFn: () => deleteFolder(folder.id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['folders', folder.parentId ?? null] })
      onRefreshParent?.()
    },
  })

  const isSelected = selectedId === folder.id

  const toggleExpand = (e) => { e.stopPropagation(); setExpanded((v) => !v) }

  return (
    <div>
      <div
        className={`relative flex items-center gap-1 px-2 py-1 rounded-lg cursor-pointer group text-sm select-none
          ${isSelected ? 'bg-indigo-50 text-indigo-700' : 'hover:bg-gray-100 text-gray-700'}`}
        style={{ paddingLeft: `${(depth + 1) * 14}px` }}
        onClick={() => onSelect(folder)}
      >
        <button className="p-0.5 text-gray-400 hover:text-gray-600 flex-shrink-0" onClick={toggleExpand}>
          <ChevronRight size={13} className={`transition-transform ${expanded ? 'rotate-90' : ''}`} />
        </button>

        {expanded ? <FolderOpen size={14} className="flex-shrink-0" /> : <Folder size={14} className="flex-shrink-0" />}
        <span className="flex-1 truncate text-sm">{folder.name}</span>

        {/* Context menu button */}
        <div className="relative opacity-0 group-hover:opacity-100 flex-shrink-0">
          <button
            className="p-0.5 rounded hover:bg-gray-200"
            onClick={(e) => { e.stopPropagation(); setMenu((v) => !v) }}
          >
            <MoreHorizontal size={13} />
          </button>
          {menu && (
            <div
              className="absolute right-0 top-6 bg-white border border-gray-200 rounded-lg shadow-lg z-50 py-1 min-w-[140px] text-sm"
              onMouseLeave={() => setMenu(false)}
            >
              {[
                { label: 'Nueva subcarpeta', action: () => setShowCreate(true) },
                { label: 'Renombrar', action: () => setShowRename(true) },
                { label: 'Mover a…', action: () => setShowMove(true) },
                {
                  label: 'Eliminar', danger: true,
                  action: () => { if (confirm(`¿Eliminar "${folder.name}"?`)) del.mutate() },
                },
              ].map(({ label, action, danger }) => (
                <button
                  key={label}
                  className={`w-full text-left px-3 py-1.5 hover:bg-gray-50 ${danger ? 'text-red-600' : ''}`}
                  onClick={() => { setMenu(false); action() }}
                >
                  {label}
                </button>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Children */}
      {expanded && (
        <div>
          {children.map((child) => (
            <FolderNode
              key={child.id}
              folder={child}
              depth={depth + 1}
              selectedId={selectedId}
              onSelect={onSelect}
              onRefreshParent={() => qc.invalidateQueries({ queryKey: ['folders', folder.id] })}
            />
          ))}
          <button
            className="flex items-center gap-1 text-xs text-gray-400 hover:text-indigo-500 py-1"
            style={{ paddingLeft: `${(depth + 2) * 14 + 4}px` }}
            onClick={(e) => { e.stopPropagation(); setShowCreate(true) }}
          >
            <Plus size={11} /> Nueva carpeta
          </button>
        </div>
      )}

      {showRename && <RenameFolderDialog folder={folder} onDone={() => setShowRename(false)} />}
      {showCreate && (
        <CreateFolderModal
          parentId={folder.id}
          onClose={() => setShowCreate(false)}
          onCreated={() => {
            setShowCreate(false)
            setExpanded(true)
            qc.invalidateQueries({ queryKey: ['folders', folder.id] })
          }}
        />
      )}
      {showMove && (
        <MoveModal folderId={folder.id} currentParentId={folder.parentId} onClose={() => setShowMove(false)} />
      )}
    </div>
  )
}

// ── Root tree component ───────────────────────────────────────────────────────
export default function FolderTree({ selectedId, onSelect }) {
  const [showCreate, setShowCreate] = useState(false)
  const qc = useQueryClient()

  const { data: roots = [] } = useQuery({
    queryKey: ['folders', null],
    queryFn: () => listFolders(undefined),
  })

  return (
    <nav className="py-2">
      {/* "All files" root item */}
      <div
        className={`flex items-center gap-2 px-3 py-1.5 rounded-lg cursor-pointer text-sm mb-1 select-none
          ${!selectedId ? 'bg-indigo-50 text-indigo-700 font-medium' : 'text-gray-600 hover:bg-gray-100'}`}
        onClick={() => onSelect(null)}
      >
        <Folder size={14} />
        <span>Todos los archivos</span>
      </div>

      {roots.map((f) => (
        <FolderNode
          key={f.id}
          folder={f}
          depth={0}
          selectedId={selectedId}
          onSelect={onSelect}
          onRefreshParent={() => qc.invalidateQueries({ queryKey: ['folders', null] })}
        />
      ))}

      <button
        onClick={() => setShowCreate(true)}
        className="flex items-center gap-1 text-xs text-gray-400 hover:text-indigo-500 px-3 py-2 mt-1"
      >
        <Plus size={12} /> Nueva carpeta raíz
      </button>

      {showCreate && (
        <CreateFolderModal
          parentId={null}
          onClose={() => setShowCreate(false)}
          onCreated={() => {
            setShowCreate(false)
            qc.invalidateQueries({ queryKey: ['folders', null] })
          }}
        />
      )}
    </nav>
  )
}

import FolderTree from '../folders/FolderTree'

export default function Sidebar({ selectedFolderId, onSelectFolder }) {
  return (
    <aside className="w-56 flex-shrink-0 bg-white border-r border-gray-200 flex flex-col overflow-hidden">
      <div className="px-3 py-3 border-b border-gray-100">
        <p className="text-[10px] font-semibold text-gray-400 uppercase tracking-wider">Carpetas</p>
      </div>
      <div className="flex-1 overflow-y-auto">
        <FolderTree selectedId={selectedFolderId} onSelect={onSelectFolder} />
      </div>
    </aside>
  )
}

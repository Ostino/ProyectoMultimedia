import { useState } from 'react'
import Topbar from '../components/layout/Topbar'
import Sidebar from '../components/layout/Sidebar'
import FileGrid from '../components/explorer/FileGrid'
import UploadZone from '../components/explorer/UploadZone'
import FileDetailModal from '../components/preview/FileDetailModal'
import TagModal from '../components/modals/TagModal'
import SearchDrawer from '../components/modals/SearchDrawer'
import { useQueryClient } from '@tanstack/react-query'

export default function ExplorerPage() {
  const [selectedFolder, setSelectedFolder] = useState(null) // { id, name } | null
  const [showUpload, setShowUpload] = useState(false)
  const [showSearch, setShowSearch] = useState(false)
  const [searchCriteria, setSearchCriteria] = useState(null) // null = browse mode
  const [openFileId, setOpenFileId] = useState(null)
  const [tagFileId, setTagFileId] = useState(null)
  const qc = useQueryClient()

  const handleSelectFolder = (folder) => {
    setSelectedFolder(folder)
    setSearchCriteria(null) // exit search mode on folder navigation
  }

  const handleSearch = (criteria) => {
    setSearchCriteria(criteria)
  }

  const handleClearSearch = () => {
    setSearchCriteria(null)
  }

  return (
    <div className="h-screen flex flex-col bg-gray-50 overflow-hidden">
      <Topbar
        onSearch={() => setShowSearch(true)}
        onUpload={() => setShowUpload(true)}
      />

      <div className="flex flex-1 overflow-hidden">
        <Sidebar
          selectedFolderId={selectedFolder?.id ?? null}
          onSelectFolder={handleSelectFolder}
        />

        <main className="flex-1 overflow-y-auto flex flex-col">
          {/* Breadcrumb / status bar */}
          <div className="flex items-center gap-2 px-6 py-3 border-b border-gray-200 bg-white text-sm">
            {searchCriteria ? (
              <>
                <span className="text-gray-500">Resultados de búsqueda</span>
                <button
                  onClick={handleClearSearch}
                  className="ml-auto text-xs text-indigo-600 hover:underline"
                >
                  ✕ Limpiar búsqueda
                </button>
              </>
            ) : (
              <>
                <button
                  onClick={() => handleSelectFolder(null)}
                  className="text-gray-400 hover:text-indigo-600"
                >
                  Inicio
                </button>
                {selectedFolder && (
                  <>
                    <span className="text-gray-300">/</span>
                    <span className="text-gray-700 font-medium">{selectedFolder.name}</span>
                  </>
                )}
              </>
            )}
          </div>

          <FileGrid
            folderId={selectedFolder?.id ?? null}
            searchCriteria={searchCriteria}
            onOpenFile={(file) => setOpenFileId(file.id)}
            onTagFile={(file) => setTagFileId(file.id)}
          />
        </main>
      </div>

      {showUpload && (
        <UploadZone
          folderId={selectedFolder?.id ?? null}
          onClose={() => setShowUpload(false)}
          onUploaded={() => qc.invalidateQueries({ queryKey: ['search'] })}
        />
      )}

      {showSearch && (
        <SearchDrawer
          onClose={() => setShowSearch(false)}
          onSearch={handleSearch}
        />
      )}

      {openFileId && (
        <FileDetailModal
          fileId={openFileId}
          onClose={() => setOpenFileId(null)}
        />
      )}

      {tagFileId && (
        <TagModal
          fileId={tagFileId}
          onClose={() => { setTagFileId(null); qc.invalidateQueries({ queryKey: ['file-tags', tagFileId] }) }}
        />
      )}
    </div>
  )
}

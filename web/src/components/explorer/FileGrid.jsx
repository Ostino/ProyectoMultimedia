import { useQuery } from '@tanstack/react-query'
import { FolderOpen } from 'lucide-react'
import { searchFiles } from '../../api/search'
import FileCard from './FileCard'

export default function FileGrid({ folderId, searchCriteria, onOpenFile, onTagFile }) {
  const isSearch = searchCriteria !== null

  const { data, isLoading, isFetching } = useQuery({
    queryKey: ['search', folderId, searchCriteria],
    queryFn: () =>
      searchFiles(
        { folderId: isSearch ? searchCriteria?.folderId : folderId, ...searchCriteria },
        { size: 60, sortBy: 'createdAt', sortDir: 'desc' }
      ),
    keepPreviousData: true,
  })

  const files = data?.content ?? []

  if (isLoading) {
    return (
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4 p-6">
        {Array.from({ length: 12 }).map((_, i) => (
          <div key={i} className="rounded-xl bg-gray-100 animate-pulse h-44" />
        ))}
      </div>
    )
  }

  if (files.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center flex-1 py-24 text-gray-400">
        <FolderOpen size={48} className="mb-3 opacity-30" />
        <p className="text-sm font-medium">
          {isSearch ? 'Sin resultados para esa búsqueda' : 'Esta carpeta está vacía'}
        </p>
        {!isSearch && <p className="text-xs mt-1">Arrastrá archivos o usá el botón Subir</p>}
      </div>
    )
  }

  return (
    <div className="p-6">
      {isSearch && (
        <p className="text-xs text-gray-400 mb-4">
          {data?.totalElements ?? files.length} resultado{data?.totalElements !== 1 ? 's' : ''}
          {isFetching && ' · actualizando…'}
        </p>
      )}
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4">
        {files.map((file) => (
          <FileCard
            key={file.id}
            file={file}
            onOpen={onOpenFile}
            onTag={onTagFile}
          />
        ))}
      </div>
    </div>
  )
}

import { useCallback, useState } from 'react'
import { useDropzone } from 'react-dropzone'
import { useQueryClient } from '@tanstack/react-query'
import { Upload, X, CheckCircle, AlertCircle, Loader } from 'lucide-react'
import { uploadFile } from '../../api/files'

function UploadItem({ item }) {
  const color = item.status === 'done'
    ? 'text-green-600' : item.status === 'error'
    ? 'text-red-500' : 'text-indigo-600'

  return (
    <div className="flex items-center gap-3 py-2 border-b border-gray-100 last:border-0">
      <div className="flex-shrink-0">
        {item.status === 'done' && <CheckCircle size={16} className="text-green-500" />}
        {item.status === 'error' && <AlertCircle size={16} className="text-red-400" />}
        {item.status === 'uploading' && <Loader size={16} className="text-indigo-500 animate-spin" />}
      </div>
      <div className="flex-1 min-w-0">
        <p className="text-sm text-gray-800 truncate">{item.name}</p>
        {item.status === 'uploading' && (
          <div className="mt-1 h-1 bg-gray-200 rounded-full overflow-hidden">
            <div
              className="h-full bg-indigo-500 transition-all duration-300"
              style={{ width: `${item.progress}%` }}
            />
          </div>
        )}
        {item.status === 'error' && <p className="text-xs text-red-400 truncate">{item.error}</p>}
      </div>
    </div>
  )
}

export default function UploadZone({ folderId, onClose, onUploaded }) {
  const [items, setItems] = useState([])
  const qc = useQueryClient()

  const updateItem = (name, patch) =>
    setItems((prev) => prev.map((i) => (i.name === name ? { ...i, ...patch } : i)))

  const onDrop = useCallback(
    async (accepted) => {
      const newItems = accepted.map((f) => ({ name: f.name, status: 'uploading', progress: 0, error: '' }))
      setItems((prev) => [...prev, ...newItems])

      for (const file of accepted) {
        try {
          await uploadFile(file, folderId, (pct) => updateItem(file.name, { progress: pct }))
          updateItem(file.name, { status: 'done', progress: 100 })
          qc.invalidateQueries({ queryKey: ['search'] })
          onUploaded?.()
        } catch (e) {
          const msg = e.response?.data?.message ?? e.message ?? 'Error'
          updateItem(file.name, { status: 'error', error: msg })
        }
      }
    },
    [folderId, qc, onUploaded]
  )

  const { getRootProps, getInputProps, isDragActive } = useDropzone({ onDrop, multiple: true })

  return (
    <div className="fixed inset-0 bg-black/30 z-50 flex items-center justify-center" onClick={onClose}>
      <div
        className="bg-white rounded-xl shadow-xl w-full max-w-md mx-4"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
          <h2 className="font-semibold text-gray-900">Subir archivos</h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600"><X size={18} /></button>
        </div>

        {/* Drop area */}
        <div
          {...getRootProps()}
          className={`m-4 border-2 border-dashed rounded-xl p-8 text-center cursor-pointer transition-colors
            ${isDragActive ? 'border-indigo-400 bg-indigo-50' : 'border-gray-300 hover:border-indigo-300 hover:bg-gray-50'}`}
        >
          <input {...getInputProps()} />
          <Upload size={32} className="mx-auto mb-3 text-gray-300" />
          {isDragActive
            ? <p className="text-indigo-600 font-medium text-sm">Soltá aquí</p>
            : (
              <>
                <p className="text-gray-600 text-sm font-medium mb-1">Arrastrá archivos o hacé clic</p>
                <p className="text-gray-400 text-xs">Imágenes, videos, audios y documentos</p>
              </>
            )}
        </div>

        {/* Upload list */}
        {items.length > 0 && (
          <div className="mx-4 mb-4 max-h-48 overflow-y-auto border border-gray-200 rounded-lg px-3">
            {items.map((item) => <UploadItem key={item.name} item={item} />)}
          </div>
        )}

        <div className="px-5 pb-4 flex justify-end">
          <button onClick={onClose} className="text-sm text-gray-500 hover:text-gray-700">Cerrar</button>
        </div>
      </div>
    </div>
  )
}

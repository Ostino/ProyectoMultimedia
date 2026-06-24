import { useEffect, useRef, useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { FileImage, FileVideo, FileAudio, FileText, Tag } from 'lucide-react'
import { getFileDetail, fetchDerivativeBlob } from '../../api/files'

const STATUS_COLOR = {
  READY: 'bg-green-100 text-green-700',
  PENDING: 'bg-yellow-100 text-yellow-700',
  PROCESSING: 'bg-blue-100 text-blue-700',
  FAILED: 'bg-red-100 text-red-700',
}

const STATUS_LABEL = {
  READY: 'Listo', PENDING: 'Pendiente', PROCESSING: 'Procesando', FAILED: 'Error',
}

const TYPE_ICON = {
  IMAGE: FileImage, VIDEO: FileVideo, AUDIO: FileAudio, DOCUMENT: FileText,
}
const TYPE_COLOR = {
  IMAGE: 'text-emerald-500', VIDEO: 'text-purple-500', AUDIO: 'text-blue-500', DOCUMENT: 'text-orange-500',
}

// Loads an image that requires Authorization header — lazy on scroll into view
function AuthThumb({ fileId, label }) {
  const ref = useRef(null)
  const [visible, setVisible] = useState(false)
  const { data: src } = useQuery({
    queryKey: ['thumb', fileId, label],
    queryFn: () => fetchDerivativeBlob(fileId, label),
    enabled: visible,
    staleTime: Infinity,
    gcTime: 5 * 60 * 1000,
  })

  useEffect(() => {
    const obs = new IntersectionObserver(([e]) => { if (e.isIntersecting) { setVisible(true); obs.disconnect() } })
    if (ref.current) obs.observe(ref.current)
    return () => obs.disconnect()
  }, [])

  return (
    <div ref={ref} className="w-full h-full">
      {src
        ? <img src={src} className="w-full h-full object-cover" loading="lazy" />
        : <div className="w-full h-full animate-pulse bg-gray-100" />}
    </div>
  )
}

export default function FileCard({ file: initialFile, onOpen, onTag }) {
  const needsPoll = ['PENDING', 'PROCESSING'].includes(initialFile.status)

  // Poll status for non-final files
  const { data: polled } = useQuery({
    queryKey: ['file-status', initialFile.id],
    queryFn: () => getFileDetail(initialFile.id),
    enabled: needsPoll,
    refetchInterval: (query) => {
      const status = query.state.data?.status
      return !status || ['PENDING', 'PROCESSING'].includes(status) ? 3000 : false
    },
  })

  const file = polled ?? initialFile
  const isReady = file.status === 'READY'
  const showThumb = isReady && (file.fileType === 'IMAGE' || file.fileType === 'VIDEO')

  const Icon = TYPE_ICON[file.fileType] ?? FileText
  const iconColor = TYPE_COLOR[file.fileType] ?? 'text-gray-400'

  return (
    <div
      className="bg-white rounded-xl border border-gray-200 overflow-hidden cursor-pointer hover:shadow-md hover:border-indigo-300 transition-all group"
      onClick={() => onOpen(file)}
    >
      {/* Thumbnail area */}
      <div className="h-36 bg-gray-50 relative flex items-center justify-center overflow-hidden">
        {showThumb
          ? <AuthThumb fileId={file.id} label={file.fileType === 'VIDEO' ? 'cover' : 'thumb_512'} />
          : <Icon size={40} className={`${iconColor} opacity-40`} />}

        {/* Status badge */}
        <span className={`absolute top-2 left-2 text-[10px] font-medium px-1.5 py-0.5 rounded-full ${STATUS_COLOR[file.status] ?? 'bg-gray-100 text-gray-500'}`}>
          {STATUS_LABEL[file.status] ?? file.status}
        </span>

        {/* Tag button */}
        <button
          className="absolute top-2 right-2 opacity-0 group-hover:opacity-100 bg-white/80 hover:bg-white rounded-full p-1 transition-opacity"
          title="Etiquetar"
          onClick={(e) => { e.stopPropagation(); onTag(file) }}
        >
          <Tag size={12} className="text-gray-600" />
        </button>
      </div>

      {/* Info */}
      <div className="px-3 py-2">
        <p className="text-xs text-gray-800 font-medium truncate" title={file.originalName}>
          {file.originalName}
        </p>
        <p className="text-[10px] text-gray-400 mt-0.5">
          {file.sizeBytes
            ? file.sizeBytes >= 1024 * 1024
              ? `${(file.sizeBytes / 1024 / 1024).toFixed(1)} MB`
              : `${(file.sizeBytes / 1024).toFixed(1)} KB`
            : ''}
        </p>
      </div>
    </div>
  )
}

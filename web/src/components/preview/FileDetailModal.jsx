import { useEffect, useState } from 'react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { X, Download, Tag, ExternalLink } from 'lucide-react'
import { getFileDetail, fetchDerivativeBlob } from '../../api/files'
import { listFileTags } from '../../api/tags'
import AudioPlayer from './AudioPlayer'
import VideoPlayer from './VideoPlayer'
import PdfViewer from './PdfViewer'
import TagModal from '../modals/TagModal'

const STATUS_COLOR = {
  READY: 'text-green-600 bg-green-50',
  PENDING: 'text-yellow-600 bg-yellow-50',
  PROCESSING: 'text-blue-600 bg-blue-50',
  FAILED: 'text-red-600 bg-red-50',
}

function MetaRow({ label, value }) {
  if (!value) return null
  return (
    <div className="flex gap-2 text-sm">
      <span className="text-gray-400 w-28 flex-shrink-0">{label}</span>
      <span className="text-gray-800 break-all">{value}</span>
    </div>
  )
}

function ImagePreview({ fileId }) {
  const [src, setSrc] = useState(null)
  useEffect(() => {
    let url
    fetchDerivativeBlob(fileId, 'thumb_lg')
      .catch(() => fetchDerivativeBlob(fileId, 'thumb_sm'))
      .then((blobUrl) => { url = blobUrl; setSrc(blobUrl) })
      .catch(() => {})
    return () => { if (url) URL.revokeObjectURL(url) }
  }, [fileId])
  return src
    ? <img src={src} alt="" className="w-full rounded-lg object-contain max-h-72" />
    : <div className="h-48 animate-pulse bg-gray-100 rounded-lg" />
}

export default function FileDetailModal({ fileId, onClose }) {
  const [showTagModal, setShowTagModal] = useState(false)
  const qc = useQueryClient()

  const { data, isLoading } = useQuery({
    queryKey: ['file-detail', fileId],
    queryFn: () => getFileDetail(fileId),
    refetchInterval: (query) => {
      const status = query.state.data?.file?.status
      return !status || ['PENDING', 'PROCESSING'].includes(status) ? 3000 : false
    },
  })

  const { data: tags = [] } = useQuery({
    queryKey: ['file-tags', fileId],
    queryFn: () => listFileTags(fileId),
  })

  const file = data?.file
  const meta = data?.metadata

  const downloadOriginal = () => {
    const a = document.createElement('a')
    a.href = `/api/files/${fileId}/original`
    a.download = file?.originalName ?? 'archivo'
    // Let the browser handle auth via the service worker / fetch API — direct download via axios
    // Since the download requires auth, we open a fetch and create a blob link
    import('../../api/files').then(({ fetchOriginalBlob }) => {
      fetchOriginalBlob(fileId).then((url) => {
        a.href = url
        a.click()
        setTimeout(() => URL.revokeObjectURL(url), 30_000)
      })
    })
  }

  return (
    <div className="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4" onClick={onClose}>
      <div
        className="bg-white rounded-2xl shadow-2xl w-full max-w-3xl max-h-[90vh] flex flex-col overflow-hidden"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-200">
          <div className="flex-1 min-w-0 mr-3">
            <h2 className="font-semibold text-gray-900 truncate text-sm">
              {file?.originalName ?? '…'}
            </h2>
            {file && (
              <span className={`inline-flex text-[10px] px-1.5 py-0.5 rounded-full font-medium mt-0.5 ${STATUS_COLOR[file.status] ?? ''}`}>
                {file.status}
              </span>
            )}
          </div>
          <div className="flex items-center gap-2 flex-shrink-0">
            <button
              onClick={() => setShowTagModal(true)}
              title="Etiquetar"
              className="text-gray-400 hover:text-indigo-600 transition-colors"
            >
              <Tag size={18} />
            </button>
            <button onClick={downloadOriginal} title="Descargar original" className="text-gray-400 hover:text-gray-700">
              <Download size={18} />
            </button>
            <button onClick={onClose} className="text-gray-400 hover:text-gray-700">
              <X size={20} />
            </button>
          </div>
        </div>

        {/* Body */}
        <div className="flex-1 overflow-y-auto">
          {isLoading ? (
            <div className="p-6 space-y-3">
              {[...Array(4)].map((_, i) => <div key={i} className="h-4 bg-gray-100 animate-pulse rounded" />)}
            </div>
          ) : (
            <div className="grid md:grid-cols-2 gap-0 h-full">
              {/* Left — Preview */}
              <div className="p-6 border-b md:border-b-0 md:border-r border-gray-100">
                {file?.status !== 'READY' ? (
                  <div className="flex items-center justify-center h-48 text-gray-300 text-sm">
                    Procesando…
                  </div>
                ) : file?.fileType === 'IMAGE' ? (
                  <ImagePreview fileId={fileId} />
                ) : file?.fileType === 'VIDEO' ? (
                  <VideoPlayer fileId={fileId} />
                ) : file?.fileType === 'AUDIO' ? (
                  <AudioPlayer fileId={fileId} />
                ) : file?.fileType === 'DOCUMENT' ? (
                  <PdfViewer fileId={fileId} />
                ) : null}
              </div>

              {/* Right — Info */}
              <div className="p-6 space-y-4">
                {/* File info */}
                <section className="space-y-1.5">
                  <p className="text-xs font-semibold text-gray-400 uppercase tracking-wide mb-2">Archivo</p>
                  <MetaRow label="Tipo" value={file?.fileType} />
                  <MetaRow label="MIME" value={file?.mimeType} />
                  <MetaRow label="Tamaño" value={file?.sizeBytes ? `${(file.sizeBytes / 1024 / 1024).toFixed(2)} MB` : null} />
                  <MetaRow label="Subido" value={file?.createdAt ? new Date(file.createdAt).toLocaleString('es-AR') : null} />
                </section>

                {/* Metadata */}
                {meta && (
                  <section className="border-t border-gray-100 pt-4 space-y-1.5">
                    <p className="text-xs font-semibold text-gray-400 uppercase tracking-wide mb-2">Metadatos</p>
                    <MetaRow label="Dimensiones" value={meta.width && meta.height ? `${meta.width} × ${meta.height} px` : null} />
                    <MetaRow label="Duración" value={meta.durationSeconds ? `${meta.durationSeconds.toFixed(1)} s` : null} />
                    <MetaRow label="Cámara" value={[meta.cameraMake, meta.cameraModel].filter(Boolean).join(' ') || null} />
                    <MetaRow label="Capturada" value={meta.takenAt ? new Date(meta.takenAt).toLocaleDateString('es-AR') : null} />
                    <MetaRow label="Códec video" value={meta.videoCodec} />
                    <MetaRow label="Códec audio" value={meta.audioCodec} />
                    <MetaRow label="Artista" value={meta.artist} />
                    <MetaRow label="Álbum" value={meta.album} />
                    <MetaRow label="Páginas" value={meta.pageCount != null ? String(meta.pageCount) : null} />
                    <MetaRow label="Autor PDF" value={meta.author} />
                  </section>
                )}

                {/* Tags */}
                <section className="border-t border-gray-100 pt-4">
                  <div className="flex items-center justify-between mb-2">
                    <p className="text-xs font-semibold text-gray-400 uppercase tracking-wide">Etiquetas</p>
                    <button
                      onClick={() => setShowTagModal(true)}
                      className="text-xs text-indigo-600 hover:underline"
                    >
                      Editar
                    </button>
                  </div>
                  <div className="flex flex-wrap gap-1.5">
                    {tags.length === 0 && <span className="text-xs text-gray-400">Sin etiquetas</span>}
                    {tags.map((t) => (
                      <span key={t.id} className="bg-indigo-50 text-indigo-700 text-xs px-2 py-0.5 rounded-full">
                        {t.name}
                      </span>
                    ))}
                  </div>
                </section>
              </div>
            </div>
          )}
        </div>
      </div>

      {showTagModal && (
        <TagModal
          fileId={fileId}
          onClose={() => { setShowTagModal(false); qc.invalidateQueries({ queryKey: ['file-tags', fileId] }) }}
        />
      )}
    </div>
  )
}

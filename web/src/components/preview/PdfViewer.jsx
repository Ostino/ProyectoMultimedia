import { useEffect, useState } from 'react'
import { FileText, Loader } from 'lucide-react'
import { fetchDerivativeBlob } from '../../api/files'

export default function PdfViewer({ fileId }) {
  const [src, setSrc] = useState(null)
  const [loading, setLoading] = useState(true)
  const [noPreview, setNoPreview] = useState(false)

  useEffect(() => {
    let url
    fetchDerivativeBlob(fileId, 'preview')
      .then((blobUrl) => { url = blobUrl; setSrc(blobUrl) })
      .catch(() => setNoPreview(true))
      .finally(() => setLoading(false))
    return () => { if (url) URL.revokeObjectURL(url) }
  }, [fileId])

  if (loading) {
    return (
      <div className="flex items-center justify-center h-48 text-gray-400">
        <Loader size={20} className="animate-spin mr-2" /> Cargando vista previa…
      </div>
    )
  }

  if (noPreview || !src) {
    return (
      <div className="flex flex-col items-center justify-center h-48 text-gray-300">
        <FileText size={48} className="mb-2" />
        <p className="text-sm">Vista previa no disponible</p>
      </div>
    )
  }

  return (
    <img
      src={src}
      alt="Primera página del PDF"
      className="w-full rounded-lg border border-gray-200 shadow-sm"
    />
  )
}

import { useEffect, useState } from 'react'
import { Loader } from 'lucide-react'
import { fetchOriginalBlob, fetchDerivativeBlob } from '../../api/files'

export default function VideoPlayer({ fileId }) {
  const [videoSrc, setVideoSrc] = useState(null)
  const [posterSrc, setPosterSrc] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(false)

  useEffect(() => {
    let videoUrl, posterUrl

    const init = async () => {
      try {
        // Load cover thumbnail as poster (fast)
        try {
          posterUrl = await fetchDerivativeBlob(fileId, 'cover')
          setPosterSrc(posterUrl)
        } catch { /* no cover, that's fine */ }

        // Load video as blob
        videoUrl = await fetchOriginalBlob(fileId)
        setVideoSrc(videoUrl)
      } catch {
        setError(true)
      } finally {
        setLoading(false)
      }
    }

    init()
    return () => {
      if (videoUrl) URL.revokeObjectURL(videoUrl)
      if (posterUrl) URL.revokeObjectURL(posterUrl)
    }
  }, [fileId])

  if (loading) {
    return (
      <div className="flex items-center justify-center h-48 text-gray-400">
        <Loader size={20} className="animate-spin mr-2" /> Cargando video…
      </div>
    )
  }

  if (error) return <p className="text-red-500 text-sm">No se pudo cargar el video</p>

  return (
    <video
      src={videoSrc}
      poster={posterSrc}
      controls
      className="w-full rounded-lg max-h-72 bg-black"
    />
  )
}

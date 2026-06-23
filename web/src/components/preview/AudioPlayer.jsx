import { useEffect, useRef, useState } from 'react'
import WaveSurfer from 'wavesurfer.js'
import { Play, Pause, Loader } from 'lucide-react'
import { fetchOriginalBlob, fetchWaveformJson } from '../../api/files'

export default function AudioPlayer({ fileId }) {
  const containerRef = useRef(null)
  const wsRef = useRef(null)
  const [isPlaying, setIsPlaying] = useState(false)
  const [status, setStatus] = useState('loading') // loading | ready | error
  const [currentTime, setCurrentTime] = useState(0)
  const [duration, setDuration] = useState(0)

  useEffect(() => {
    let ws = null
    let audioUrl = null

    const init = async () => {
      try {
        // Load audio as blob (needs Authorization header)
        const blob = await fetchOriginalBlob(fileId)
        audioUrl = blob // already a blob URL from fetchBlob helper

        // Try to get pre-computed peaks from audiowaveform JSON
        let peaks
        try {
          const waveJson = await fetchWaveformJson(fileId)
          if (waveJson?.data?.length) {
            const channels = waveJson.channels ?? 1
            const stride = channels * 2
            const mono = []
            for (let i = 0; i < waveJson.data.length; i += stride) {
              mono.push(waveJson.data[i + 1] / 128) // channel-0 max, normalized -1..1
            }
            peaks = [mono]
          }
        } catch {
          // no waveform derivative — wavesurfer decodes in-browser
        }

        ws = WaveSurfer.create({
          container: containerRef.current,
          waveColor: '#6366f1',
          progressColor: '#a5b4fc',
          height: 72,
          normalize: true,
          barWidth: 2,
          barGap: 1,
          barRadius: 2,
        })

        ws.on('ready', () => {
          setDuration(ws.getDuration())
          setStatus('ready')
        })
        ws.on('audioprocess', () => setCurrentTime(ws.getCurrentTime()))
        ws.on('play', () => setIsPlaying(true))
        ws.on('pause', () => setIsPlaying(false))
        ws.on('finish', () => { setIsPlaying(false); setCurrentTime(0) })
        ws.on('error', () => setStatus('error'))

        ws.load(audioUrl, peaks)
        wsRef.current = ws
      } catch {
        setStatus('error')
      }
    }

    init()
    return () => {
      ws?.destroy()
      if (audioUrl) URL.revokeObjectURL(audioUrl)
    }
  }, [fileId])

  const fmtTime = (s) => `${Math.floor(s / 60)}:${String(Math.floor(s % 60)).padStart(2, '0')}`

  return (
    <div className="space-y-3">
      <div ref={containerRef} className="w-full rounded-lg overflow-hidden" />

      {status === 'loading' && (
        <div className="flex items-center gap-2 text-gray-400 text-sm">
          <Loader size={14} className="animate-spin" /> Cargando audio…
        </div>
      )}
      {status === 'error' && <p className="text-red-500 text-sm">No se pudo cargar el audio</p>}

      {status === 'ready' && (
        <div className="flex items-center gap-3">
          <button
            onClick={() => wsRef.current?.playPause()}
            className="w-9 h-9 rounded-full bg-indigo-600 hover:bg-indigo-700 text-white flex items-center justify-center flex-shrink-0 transition-colors"
          >
            {isPlaying ? <Pause size={16} /> : <Play size={16} />}
          </button>
          <span className="text-xs text-gray-500 tabular-nums">
            {fmtTime(currentTime)} / {fmtTime(duration)}
          </span>
        </div>
      )}
    </div>
  )
}

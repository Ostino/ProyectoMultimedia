import { useState } from 'react'
import { X, Search } from 'lucide-react'

const FILE_TYPES = ['IMAGE', 'VIDEO', 'AUDIO', 'DOCUMENT']

const INITIAL = {
  name: '', fileType: '', fromDate: '', toDate: '', tags: '',
  mobileCamera: false, minWidth: '', minHeight: '', takenFrom: '', takenTo: '',
  minDuration: '', maxDuration: '', videoCodec: '', minVideoWidth: '',
  artist: '', album: '',
  minPageCount: '', pdfAuthor: '',
}

function Field({ label, children }) {
  return (
    <div>
      <label className="block text-xs font-medium text-gray-500 mb-1">{label}</label>
      {children}
    </div>
  )
}

const inputCls = 'w-full border border-gray-300 rounded-lg px-2.5 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500'

export default function SearchDrawer({ onClose, onSearch }) {
  const [f, setF] = useState(INITIAL)

  const set = (key) => (e) =>
    setF((prev) => ({ ...prev, [key]: e.target.type === 'checkbox' ? e.target.checked : e.target.value }))

  const submit = (e) => {
    e.preventDefault()
    const criteria = {
      ...(f.name && { name: f.name }),
      ...(f.fileType && { fileType: f.fileType }),
      ...(f.fromDate && { fromDate: f.fromDate }),
      ...(f.toDate && { toDate: f.toDate }),
      ...(f.tags && { tags: f.tags.split(',').map((t) => t.trim()).filter(Boolean) }),
      ...(f.mobileCamera && { mobileCamera: true }),
      ...(f.minWidth && { minWidth: Number(f.minWidth) }),
      ...(f.minHeight && { minHeight: Number(f.minHeight) }),
      ...(f.takenFrom && { takenFrom: f.takenFrom }),
      ...(f.takenTo && { takenTo: f.takenTo }),
      ...(f.minDuration && { minDuration: Number(f.minDuration) }),
      ...(f.maxDuration && { maxDuration: Number(f.maxDuration) }),
      ...(f.videoCodec && { videoCodec: f.videoCodec }),
      ...(f.minVideoWidth && { minVideoWidth: Number(f.minVideoWidth) }),
      ...(f.artist && { artist: f.artist }),
      ...(f.album && { album: f.album }),
      ...(f.minPageCount && { minPageCount: Number(f.minPageCount) }),
      ...(f.pdfAuthor && { pdfAuthor: f.pdfAuthor }),
    }
    onSearch(criteria)
    onClose()
  }

  const showImg = !f.fileType || f.fileType === 'IMAGE'
  const showVid = !f.fileType || f.fileType === 'VIDEO'
  const showAud = !f.fileType || f.fileType === 'AUDIO'
  const showDoc = !f.fileType || f.fileType === 'DOCUMENT'

  return (
    <div className="fixed inset-0 z-50 flex justify-end">
      <div className="absolute inset-0 bg-black/30" onClick={onClose} />
      <div className="relative bg-white w-80 h-full shadow-xl flex flex-col">
        <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
          <h2 className="font-semibold text-gray-900 flex items-center gap-2">
            <Search size={16} /> Búsqueda avanzada
          </h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600"><X size={18} /></button>
        </div>

        <form onSubmit={submit} className="flex-1 overflow-y-auto px-5 py-4 space-y-4">
          {/* ── Genéricos ── */}
          <Field label="Nombre contiene">
            <input className={inputCls} value={f.name} onChange={set('name')} placeholder="foto, video…" />
          </Field>

          <Field label="Tipo de archivo">
            <select className={inputCls} value={f.fileType} onChange={set('fileType')}>
              <option value="">Todos</option>
              {FILE_TYPES.map((t) => <option key={t} value={t}>{t}</option>)}
            </select>
          </Field>

          <div className="grid grid-cols-2 gap-2">
            <Field label="Desde">
              <input type="date" className={inputCls} value={f.fromDate} onChange={set('fromDate')} />
            </Field>
            <Field label="Hasta">
              <input type="date" className={inputCls} value={f.toDate} onChange={set('toDate')} />
            </Field>
          </div>

          <Field label="Etiquetas (separadas por coma)">
            <input className={inputCls} value={f.tags} onChange={set('tags')} placeholder="vacaciones, trabajo" />
          </Field>

          {/* ── Imagen ── */}
          {showImg && (
            <section className="border-t border-gray-100 pt-3 space-y-3">
              <p className="text-xs font-semibold text-gray-400 uppercase tracking-wide">Imagen</p>
              <label className="flex items-center gap-2 text-sm text-gray-700 cursor-pointer">
                <input type="checkbox" checked={f.mobileCamera} onChange={set('mobileCamera')} />
                Tomada con cámara de celular
              </label>
              <div className="grid grid-cols-2 gap-2">
                <Field label="Ancho mín. (px)">
                  <input type="number" min="0" className={inputCls} value={f.minWidth} onChange={set('minWidth')} />
                </Field>
                <Field label="Alto mín. (px)">
                  <input type="number" min="0" className={inputCls} value={f.minHeight} onChange={set('minHeight')} />
                </Field>
              </div>
              <div className="grid grid-cols-2 gap-2">
                <Field label="Capturada desde">
                  <input type="date" className={inputCls} value={f.takenFrom} onChange={set('takenFrom')} />
                </Field>
                <Field label="Capturada hasta">
                  <input type="date" className={inputCls} value={f.takenTo} onChange={set('takenTo')} />
                </Field>
              </div>
            </section>
          )}

          {/* ── Video ── */}
          {showVid && (
            <section className="border-t border-gray-100 pt-3 space-y-3">
              <p className="text-xs font-semibold text-gray-400 uppercase tracking-wide">Video</p>
              <div className="grid grid-cols-2 gap-2">
                <Field label="Duración mín. (s)">
                  <input type="number" min="0" className={inputCls} value={f.minDuration} onChange={set('minDuration')} />
                </Field>
                <Field label="Duración máx. (s)">
                  <input type="number" min="0" className={inputCls} value={f.maxDuration} onChange={set('maxDuration')} />
                </Field>
              </div>
              <Field label="Códec de video">
                <input className={inputCls} value={f.videoCodec} onChange={set('videoCodec')} placeholder="h264, vp9…" />
              </Field>
              <Field label="Ancho mín. (px)">
                <input type="number" min="0" className={inputCls} value={f.minVideoWidth} onChange={set('minVideoWidth')} />
              </Field>
            </section>
          )}

          {/* ── Audio ── */}
          {showAud && (
            <section className="border-t border-gray-100 pt-3 space-y-3">
              <p className="text-xs font-semibold text-gray-400 uppercase tracking-wide">Audio</p>
              <Field label="Artista contiene">
                <input className={inputCls} value={f.artist} onChange={set('artist')} />
              </Field>
              <Field label="Álbum contiene">
                <input className={inputCls} value={f.album} onChange={set('album')} />
              </Field>
              {!showVid && (
                <div className="grid grid-cols-2 gap-2">
                  <Field label="Duración mín. (s)">
                    <input type="number" min="0" className={inputCls} value={f.minDuration} onChange={set('minDuration')} />
                  </Field>
                  <Field label="Duración máx. (s)">
                    <input type="number" min="0" className={inputCls} value={f.maxDuration} onChange={set('maxDuration')} />
                  </Field>
                </div>
              )}
            </section>
          )}

          {/* ── Documento ── */}
          {showDoc && (
            <section className="border-t border-gray-100 pt-3 space-y-3">
              <p className="text-xs font-semibold text-gray-400 uppercase tracking-wide">Documento</p>
              <Field label="Páginas mín.">
                <input type="number" min="0" className={inputCls} value={f.minPageCount} onChange={set('minPageCount')} />
              </Field>
              <Field label="Autor PDF contiene">
                <input className={inputCls} value={f.pdfAuthor} onChange={set('pdfAuthor')} />
              </Field>
            </section>
          )}
        </form>

        <div className="px-5 py-4 border-t border-gray-200 flex gap-2">
          <button
            type="button"
            onClick={() => setF(INITIAL)}
            className="flex-1 text-sm text-gray-500 border border-gray-300 rounded-lg py-2 hover:bg-gray-50"
          >
            Limpiar
          </button>
          <button
            onClick={submit}
            className="flex-1 text-sm bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg py-2"
          >
            Buscar
          </button>
        </div>
      </div>
    </div>
  )
}

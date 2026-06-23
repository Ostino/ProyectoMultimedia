import { LogOut, Search, Upload } from 'lucide-react'
import useAuthStore from '../../store/authStore'

export default function Topbar({ onSearch, onUpload }) {
  const clearToken = useAuthStore((s) => s.clearToken)

  return (
    <header className="h-14 bg-white border-b border-gray-200 flex items-center px-4 gap-4 flex-shrink-0 z-10">
      <span className="font-bold text-indigo-600 text-lg tracking-tight mr-2">MediaVault</span>

      <div className="flex-1" />

      <button
        onClick={onSearch}
        className="flex items-center gap-2 text-sm text-gray-600 hover:text-gray-900 bg-gray-100 hover:bg-gray-200 px-3 py-1.5 rounded-lg transition-colors"
      >
        <Search size={15} />
        Buscar
      </button>

      <button
        onClick={onUpload}
        className="flex items-center gap-2 text-sm bg-indigo-600 hover:bg-indigo-700 text-white px-3 py-1.5 rounded-lg transition-colors"
      >
        <Upload size={15} />
        Subir
      </button>

      <button
        onClick={clearToken}
        title="Cerrar sesión"
        className="text-gray-400 hover:text-gray-700 transition-colors"
      >
        <LogOut size={18} />
      </button>
    </header>
  )
}

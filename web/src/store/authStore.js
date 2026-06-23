import { create } from 'zustand'

const useAuthStore = create((set) => ({
  token: localStorage.getItem('mmToken'),
  setToken: (token) => {
    localStorage.setItem('mmToken', token)
    set({ token })
  },
  clearToken: () => {
    localStorage.removeItem('mmToken')
    set({ token: null })
  },
}))

export default useAuthStore

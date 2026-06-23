import client from './client'

const BASE = '/api/files'

export const uploadFile = (file, folderId, onProgress) => {
  const form = new FormData()
  form.append('file', file)
  if (folderId) form.append('folderId', folderId)
  return client
    .post(BASE, form, {
      headers: { 'Content-Type': 'multipart/form-data' },
      onUploadProgress: (e) =>
        onProgress?.(e.total ? Math.round((e.loaded * 100) / e.total) : 0),
    })
    .then((r) => r.data)
}

export const getFileDetail = (id) =>
  client.get(`${BASE}/${id}`).then((r) => r.data)

// Returns a blob URL — caller must URL.revokeObjectURL when done
export const fetchBlob = (url) =>
  client.get(url, { responseType: 'blob' }).then((r) => URL.createObjectURL(r.data))

export const fetchOriginalBlob = (id) => fetchBlob(`${BASE}/${id}/original`)

export const fetchDerivativeBlob = (id, label) =>
  fetchBlob(`${BASE}/${id}/derivative/${label}`)

// Raw waveform JSON from audiowaveform CLI
export const fetchWaveformJson = (id) =>
  client.get(`${BASE}/${id}/derivative/waveform`).then((r) => r.data)

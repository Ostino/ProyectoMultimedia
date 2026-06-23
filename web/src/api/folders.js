import client from './client'

const BASE = '/api/folders'

export const listFolders = (parentId) =>
  client.get(BASE, { params: parentId !== undefined ? { parentId } : {} }).then((r) => r.data)

export const createFolder = (name, parentId) =>
  client.post(BASE, { name, parentId: parentId ?? null }).then((r) => r.data)

export const renameFolder = (id, name) =>
  client.patch(`${BASE}/${id}/name`, { name }).then((r) => r.data)

export const moveFolder = (id, parentId) =>
  client.patch(`${BASE}/${id}/parent`, { parentId: parentId ?? null }).then((r) => r.data)

export const deleteFolder = (id) => client.delete(`${BASE}/${id}`)

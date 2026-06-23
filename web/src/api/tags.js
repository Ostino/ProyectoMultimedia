import client from './client'

export const listMyTags = () =>
  client.get('/api/tags').then((r) => r.data)

export const createTag = (name) =>
  client.post('/api/tags', { name }).then((r) => r.data)

export const listFileTags = (fileId) =>
  client.get(`/api/files/${fileId}/tags`).then((r) => r.data)

export const addTagToFile = (fileId, tagId) =>
  client.post(`/api/files/${fileId}/tags`, { tagId })

export const removeTagFromFile = (fileId, tagId) =>
  client.delete(`/api/files/${fileId}/tags/${tagId}`)

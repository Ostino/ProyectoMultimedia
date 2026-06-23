import client from './client'

/**
 * criteria: {
 *   name, fileType, fromDate, toDate, folderId, tags,
 *   mobileCamera, minWidth, minHeight, takenFrom, takenTo,
 *   minDuration, maxDuration, videoCodec, minVideoWidth,
 *   artist, album, minPageCount, pdfAuthor
 * }
 * pagination: { page, size, sortBy, sortDir }
 */
export const searchFiles = (criteria, pagination = {}) => {
  const params = { ...criteria, ...pagination }
  // Remove empty/undefined values so they don't pollute the query string
  Object.keys(params).forEach((k) => {
    if (params[k] === '' || params[k] === null || params[k] === undefined) delete params[k]
  })
  // tags[] needs special handling for multi-value
  if (Array.isArray(params.tags) && params.tags.length === 0) delete params.tags
  return client.get('/api/files/search', { params }).then((r) => r.data)
}

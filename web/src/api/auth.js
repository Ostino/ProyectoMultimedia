import axios from 'axios'

// Auth endpoints are at /auth/* — plain axios (no JWT needed)
export const register = (email, password) =>
  axios.post('/auth/register', { email, password })

export const login = (email, password) =>
  axios.post('/auth/login', { email, password }).then((r) => r.data.token)

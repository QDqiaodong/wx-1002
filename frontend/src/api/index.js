import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

export const umbrellaAPI = {
  create: (data) => api.post('/umbrella', data),
  getById: (id) => api.get(`/umbrella/${id}`),
  getAll: () => api.get('/umbrella'),
  getByZone: (zoneId) => api.get(`/umbrella/zone/${zoneId}`),
  getWithoutZone: () => api.get('/umbrella/without-zone'),
  update: (id, data) => api.put(`/umbrella/${id}`, data),
  delete: (id) => api.delete(`/umbrella/${id}`),
  assignZone: (id, data) => api.post(`/umbrella/${id}/assign-zone`, data),
  batchAssign: (data) => api.post('/umbrella/batch-assign', data)
}

export const zoneAPI = {
  create: (data) => api.post('/zone', data),
  getById: (id) => api.get(`/zone/${id}`),
  getAll: () => api.get('/zone'),
  getTree: () => api.get('/zone/tree'),
  update: (id, data) => api.put(`/zone/${id}`, data),
  delete: (id) => api.delete(`/zone/${id}`)
}

export const recordAPI = {
  getAll: () => api.get('/zone-change-record'),
  getByUmbrella: (umbrellaId) => api.get(`/zone-change-record/umbrella/${umbrellaId}`),
  getByZone: (zoneId) => api.get(`/zone-change-record/zone/${zoneId}`)
}
import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api

// Auth APIs
export const authAPI = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
  getMe: () => api.get('/auth/me')
}

// Word APIs
export const wordAPI = {
  getWords: (params) => api.get('/words', { params }),
  searchWords: (q) => api.get('/words/search', { params: { q } }),
  recordStudy: (wordId, mastered) => api.post(`/words/${wordId}/study`, { mastered })
}

// Grammar APIs
export const grammarAPI = {
  getLessons: (params) => api.get('/grammar', { params }),
  getLesson: (id) => api.get(`/grammar/${id}`)
}

// Article APIs
export const articleAPI = {
  getArticles: (params) => api.get('/articles', { params }),
  getArticle: (id) => api.get(`/articles/${id}`),
  searchArticles: (q) => api.get('/articles/search', { params: { q } })
}

// Quiz APIs
export const quizAPI = {
  getQuizzes: (params) => api.get('/quizzes', { params }),
  getQuiz: (id) => api.get(`/quizzes/${id}`),
  submitQuiz: (id, data) => api.post(`/quizzes/${id}/submit`, data),
  getRecords: () => api.get('/quizzes/records')
}

// Stats APIs
export const statsAPI = {
  getStats: () => api.get('/stats'),
  checkin: () => api.post('/stats/checkin')
}

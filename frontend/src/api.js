import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL + '/api',
})

// слага токена на всяка заявка
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// изтекъл/невалиден токен → изход
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && localStorage.getItem('token')) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  },
)

// превръща грешката от сървъра в текст за показване
export function errorMessage(error) {
  const data = error.response?.data
  if (!data) return 'Сървърът не отговаря'
  if (typeof data === 'string') return data
  if (data.errors && typeof data.errors === 'object') {
    return Object.values(data.errors).join(', ')
  }
  return data.message || data.error || 'Нещо се обърка'
}

export default api
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token'))
  const user = ref(null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  async function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
    await loadUser()
  }

  async function loadUser() {
    if (!token.value) return
    const { data } = await api.get('/users/me')
    user.value = data
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
  }

  return { token, user, isLoggedIn, isAdmin, setToken, loadUser, logout }
})
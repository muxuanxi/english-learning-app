import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authAPI } from '@/api'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const token = ref(localStorage.getItem('token') || '')

  const isLoggedIn = computed(() => !!token.value)
  const userLevel = computed(() => user.value?.level || 'BEGINNER')

  async function login(username, password) {
    const res = await authAPI.login({ username, password })
    token.value = res.data.data.token
    user.value = {
      id: res.data.data.userId,
      username: res.data.data.username,
      nickname: res.data.data.nickname,
      level: res.data.data.level
    }
    localStorage.setItem('token', token.value)
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  async function register(data) {
    const res = await authAPI.register(data)
    token.value = res.data.data.token
    user.value = {
      id: res.data.data.userId,
      username: res.data.data.username,
      nickname: res.data.data.nickname,
      level: res.data.data.level
    }
    localStorage.setItem('token', token.value)
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { user, token, isLoggedIn, userLevel, login, register, logout }
})

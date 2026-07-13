import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { authApi, type LoginRequest, type RegisterRequest, type User } from '@/api'

const readStoredUser = (): User | null => {
  const value = localStorage.getItem('vocab_user')
  if (!value) return null
  try {
    return JSON.parse(value) as User
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('vocab_access_token'))
  const user = ref<User | null>(readStoredUser())
  const isAuthenticated = computed(() => Boolean(token.value && user.value))

  const saveSession = (accessToken: string, currentUser: User) => {
    token.value = accessToken
    user.value = currentUser
    localStorage.setItem('vocab_access_token', accessToken)
    localStorage.setItem('vocab_user', JSON.stringify(currentUser))
  }

  const login = async (payload: LoginRequest) => {
    const result = await authApi.login(payload)
    saveSession(result.accessToken, result.user)
  }

  const register = async (payload: RegisterRequest) => {
    const result = await authApi.register(payload)
    saveSession(result.accessToken, result.user)
  }

  const logout = async () => {
    try {
      await authApi.logout()
    } finally {
      token.value = null
      user.value = null
      localStorage.removeItem('vocab_access_token')
      localStorage.removeItem('vocab_user')
    }
  }

  return { token, user, isAuthenticated, login, register, logout }
})

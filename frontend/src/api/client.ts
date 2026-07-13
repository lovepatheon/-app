import axios, { type AxiosRequestConfig } from 'axios'
import type { ApiResponse } from './types'

const useMock = import.meta.env.VITE_USE_MOCK !== 'false'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 12_000,
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' },
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('vocab_access_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('vocab_access_token')
      localStorage.removeItem('vocab_user')
    }
    return Promise.reject(error)
  },
)

const delay = (ms = 260) => new Promise((resolve) => setTimeout(resolve, ms))

export async function apiRequest<T>(
  config: AxiosRequestConfig,
  mockFactory: () => T | Promise<T>,
): Promise<T> {
  if (useMock) {
    await delay()
    return mockFactory()
  }

  const response = await http.request<ApiResponse<T>>(config)
  return response.data.data
}

export { useMock }

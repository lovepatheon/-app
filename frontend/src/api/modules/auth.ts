import { apiRequest } from '../client'
import { mockUser } from '../mockData'
import type { AuthTokens, LoginRequest, RegisterRequest } from '../types'

export const authApi = {
  login(payload: LoginRequest) {
    return apiRequest<AuthTokens>({ method: 'POST', url: '/auth/login', data: payload }, () => {
      if (payload.username !== 'demo' || payload.password !== '123456') {
        throw new Error('账号或密码错误。体验账号为 demo / 123456')
      }
      return { accessToken: 'mock-access-token', expiresIn: 7200, user: mockUser }
    })
  },

  register(payload: RegisterRequest) {
    return apiRequest<AuthTokens>({ method: 'POST', url: '/auth/register', data: payload }, () => ({
      accessToken: 'mock-access-token',
      expiresIn: 7200,
      user: { ...mockUser, username: payload.username, nickname: payload.nickname, email: payload.email },
    }))
  },

  logout() {
    return apiRequest<void>({ method: 'POST', url: '/auth/logout' }, () => undefined)
  },
}

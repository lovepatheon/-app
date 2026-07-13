import { apiRequest } from '../client'
import { mockUser } from '../mockData'
import type { User, UserSettings } from '../types'

export const userApi = {
  me() {
    return apiRequest<User>({ method: 'GET', url: '/users/me' }, () => mockUser)
  },

  updateSettings(settings: UserSettings) {
    return apiRequest<UserSettings>({ method: 'PUT', url: '/users/me/settings', data: settings }, () => settings)
  },
}

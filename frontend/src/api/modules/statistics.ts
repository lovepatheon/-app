import { apiRequest } from '../client'
import { mockStatistics } from '../mockData'
import type { StatisticsData } from '../types'

export const statisticsApi = {
  overview(range: '7D' | '30D' = '7D') {
    return apiRequest<StatisticsData>({ method: 'GET', url: '/statistics/overview', params: { range } }, () => ({
      ...mockStatistics,
      range,
    }))
  },
}

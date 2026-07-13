import { apiRequest } from '../client'
import { mockDashboard, mockWords } from '../mockData'
import type { DashboardData, ReviewAnswerRequest, ReviewAnswerResult, StudyQueue } from '../types'

export const studyApi = {
  dashboard() {
    return apiRequest<DashboardData>({ method: 'GET', url: '/study/dashboard' }, () => mockDashboard)
  },

  queue(mode: 'NEW' | 'REVIEW') {
    return apiRequest<StudyQueue>({ method: 'GET', url: '/study/queue', params: { mode } }, () => ({
      sessionId: `mock-${mode.toLowerCase()}-session`,
      mode,
      total: mode === 'NEW' ? 6 : 8,
      completed: 0,
      words: mode === 'NEW' ? mockWords.slice(0, 6) : [...mockWords].reverse(),
    }))
  },

  answer(payload: ReviewAnswerRequest) {
    return apiRequest<ReviewAnswerResult>({ method: 'POST', url: '/study/answers', data: payload }, () => {
      const intervals = { AGAIN: 0, HARD: 1, GOOD: 4, EASY: 12 }
      const days = intervals[payload.rating]
      const date = new Date()
      date.setDate(date.getDate() + days)
      return {
        nextReviewAt: date.toISOString(),
        intervalDays: days,
        mastery: payload.rating === 'EASY' ? 'MASTERED' : payload.rating === 'GOOD' ? 'FAMILIAR' : 'LEARNING',
      }
    })
  },
}

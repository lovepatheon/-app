import { apiRequest } from '../client'
import { mockWords } from '../mockData'
import type { MasteryLevel, PageResponse, WordDetail, WordLevel, WordSummary } from '../types'

export interface WordQuery {
  keyword?: string
  level?: WordLevel | 'ALL'
  mastery?: MasteryLevel | 'ALL'
  page?: number
  size?: number
}

export const wordsApi = {
  list(query: WordQuery = {}) {
    return apiRequest<PageResponse<WordSummary>>({ method: 'GET', url: '/words', params: query }, () => {
      const keyword = query.keyword?.trim().toLowerCase()
      const filtered = mockWords.filter((item) => {
        const matchesKeyword = !keyword || item.word.includes(keyword) || item.briefDefinition.includes(keyword)
        const matchesLevel = !query.level || query.level === 'ALL' || item.level.includes(query.level)
        const matchesMastery = !query.mastery || query.mastery === 'ALL' || item.mastery === query.mastery
        return matchesKeyword && matchesLevel && matchesMastery
      })
      return { content: filtered, page: 0, size: query.size || 20, totalElements: filtered.length, totalPages: 1 }
    })
  },

  detail(id: number) {
    return apiRequest<WordDetail>({ method: 'GET', url: `/words/${id}` }, () => {
      const word = mockWords.find((item) => item.id === id)
      if (!word) throw new Error('未找到该单词')
      return word
    })
  },
}

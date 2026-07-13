export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  requestId?: string
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export interface UserSettings {
  dailyNewWords: number
  dailyReviewLimit: number
  preferredLevel: 'CET4' | 'CET6' | 'BOTH'
  reminderTime: string
  soundEnabled: boolean
}

export interface User {
  id: number
  username: string
  nickname: string
  email: string
  avatarText: string
  continuousDays: number
  settings: UserSettings
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
  nickname: string
}

export interface AuthTokens {
  accessToken: string
  expiresIn: number
  user: User
}

export type WordLevel = 'CET4' | 'CET6'
export type MasteryLevel = 'NEW' | 'LEARNING' | 'FAMILIAR' | 'MASTERED'

export interface WordSense {
  partOfSpeech: string
  definitionCn: string
  definitionEn: string
}

export interface WordExample {
  sentence: string
  translation: string
}

export interface WordSummary {
  id: number
  word: string
  phonetic: string
  level: WordLevel[]
  briefDefinition: string
  mastery: MasteryLevel
  nextReviewAt?: string
}

export interface WordDetail extends WordSummary {
  pronunciationUrl?: string
  senses: WordSense[]
  examples: WordExample[]
  collocations: string[]
  usageNote: string
  memoryTip: string
  source: string
}

export interface DashboardData {
  greeting: string
  learnedToday: number
  newWordTarget: number
  reviewedToday: number
  reviewDue: number
  masteredTotal: number
  continuousDays: number
  weeklyActivity: Array<{ date: string; label: string; count: number; target: number }>
  difficultWords: WordSummary[]
}

export interface StudyQueue {
  sessionId: string
  mode: 'NEW' | 'REVIEW'
  total: number
  completed: number
  words: WordDetail[]
}

export type RecallRating = 'AGAIN' | 'HARD' | 'GOOD' | 'EASY'

export interface ReviewAnswerRequest {
  sessionId: string
  wordId: number
  rating: RecallRating
  responseTimeMs: number
}

export interface ReviewAnswerResult {
  nextReviewAt: string
  intervalDays: number
  mastery: MasteryLevel
}

export interface StatisticsData {
  range: '7D' | '30D'
  totalLearned: number
  totalMastered: number
  reviewAccuracy: number
  totalMinutes: number
  daily: Array<{ date: string; label: string; learned: number; reviewed: number }>
  masteryDistribution: Array<{ label: string; value: number; color: string }>
  recentAchievements: Array<{ title: string; description: string; date: string }>
}

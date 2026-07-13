import { createRouter, createWebHistory } from 'vue-router'

import AppShell from '@/layouts/AppShell.vue'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  scrollBehavior: () => ({ top: 0 }),
  routes: [
    { path: '/login', name: 'login', component: () => import('@/views/LoginView.vue'), meta: { guestOnly: true } },
    { path: '/register', name: 'register', component: () => import('@/views/RegisterView.vue'), meta: { guestOnly: true } },
    {
      path: '/',
      component: AppShell,
      meta: { requiresAuth: true },
      children: [
        { path: '', name: 'dashboard', component: () => import('@/views/DashboardView.vue') },
        { path: 'study', name: 'study', component: () => import('@/views/StudyView.vue') },
        { path: 'review', name: 'review', component: () => import('@/views/ReviewView.vue') },
        { path: 'words', name: 'words', component: () => import('@/views/WordLibraryView.vue') },
        { path: 'words/:id', name: 'word-detail', component: () => import('@/views/WordDetailView.vue') },
        { path: 'statistics', name: 'statistics', component: () => import('@/views/StatisticsView.vue') },
        { path: 'settings', name: 'settings', component: () => import('@/views/SettingsView.vue') },
      ],
    },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isAuthenticated) return { name: 'login', query: { redirect: to.fullPath } }
  if (to.meta.guestOnly && auth.isAuthenticated) return { name: 'dashboard' }
})

export default router

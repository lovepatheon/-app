<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const navItems = [
  { name: 'dashboard', label: '今日学习', short: '今', path: '/' },
  { name: 'study', label: '学习新词', short: '学', path: '/study' },
  { name: 'review', label: '到期复习', short: '复', path: '/review' },
  { name: 'words', label: '我的词库', short: '词', path: '/words' },
  { name: 'statistics', label: '学习统计', short: '统', path: '/statistics' },
]

const activeName = computed(() => {
  if (route.name === 'word-detail') return 'words'
  return String(route.name || '')
})

const handleLogout = async () => {
  await auth.logout()
  await router.push('/login')
}
</script>

<template>
  <div class="app-shell">
    <aside class="sidebar">
      <RouterLink to="/" class="brand" aria-label="词屿首页">
        <span class="brand-mark">W</span>
        <span><strong>WordHarbor</strong><small>四六级差集词库</small></span>
      </RouterLink>

      <nav class="side-nav" aria-label="主导航">
        <RouterLink
          v-for="item in navItems"
          :key="item.name"
          :to="item.path"
          :class="['nav-item', { active: activeName === item.name }]"
        >
          <span class="nav-glyph">{{ item.short }}</span>
          <span>{{ item.label }}</span>
          <span v-if="item.name === 'review'" class="nav-badge">16</span>
        </RouterLink>
      </nav>

      <div class="streak-card">
        <div class="streak-icon">12</div>
        <div><strong>连续学习 12 天</strong><span>今天也别断掉</span></div>
      </div>

      <div class="sidebar-footer">
        <RouterLink to="/settings" :class="['user-card', { active: activeName === 'settings' }]">
          <span class="avatar">{{ auth.user?.avatarText || '词' }}</span>
          <span><strong>{{ auth.user?.nickname || '学习者' }}</strong><small>个人设置</small></span>
        </RouterLink>
        <button class="icon-button" type="button" aria-label="退出登录" @click="handleLogout">↗</button>
      </div>
    </aside>

    <main class="main-content">
      <header class="mobile-header">
        <RouterLink to="/" class="brand compact"><span class="brand-mark">W</span><strong>WordHarbor</strong></RouterLink>
        <RouterLink to="/settings" class="avatar">{{ auth.user?.avatarText || '词' }}</RouterLink>
      </header>
      <RouterView v-slot="{ Component }">
        <Transition name="page" mode="out-in"><component :is="Component" /></Transition>
      </RouterView>
    </main>

    <nav class="mobile-nav" aria-label="移动端主导航">
      <RouterLink v-for="item in navItems.slice(0, 4)" :key="item.name" :to="item.path" :class="{ active: activeName === item.name }">
        <span>{{ item.short }}</span><small>{{ item.label.replace('今日', '').replace('到期', '').replace('我的', '') }}</small>
      </RouterLink>
    </nav>
  </div>
</template>

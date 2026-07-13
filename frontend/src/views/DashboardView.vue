<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { studyApi, type DashboardData } from '@/api'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const loading = ref(true)
const data = ref<DashboardData | null>(null)

const learningPercent = computed(() => {
  if (!data.value) return 0
  return Math.min(100, Math.round((data.value.learnedToday / data.value.newWordTarget) * 100))
})

onMounted(async () => {
  try {
    data.value = await studyApi.dashboard()
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page dashboard-page">
    <header class="page-header dashboard-heading">
      <div><span class="eyebrow">MONDAY · JUL 13</span><h1>下午好，{{ auth.user?.nickname }}</h1><p>{{ data?.greeting || '今天也向前一点。' }}</p></div>
      <RouterLink to="/settings" class="header-action">调整今日计划</RouterLink>
    </header>

    <div v-if="loading" class="dashboard-grid"><div class="hero-task skeleton-card"></div><div class="review-card skeleton-card"></div></div>

    <template v-else-if="data">
      <section class="dashboard-grid">
        <article class="hero-task">
          <div class="task-copy">
            <span class="eyebrow warm">TODAY'S FOCUS</span>
            <h2>还剩 {{ data.newWordTarget - data.learnedToday }} 个新词</h2>
            <p>预计需要 6 分钟。先完成最重要的一件小事。</p>
            <RouterLink to="/study" class="button light-button">继续学习 <span>→</span></RouterLink>
          </div>
          <div class="progress-ring" :style="{ '--value': `${learningPercent * 3.6}deg` }">
            <div><strong>{{ learningPercent }}%</strong><span>{{ data.learnedToday }}/{{ data.newWordTarget }} 词</span></div>
          </div>
        </article>

        <article class="review-card">
          <div class="review-top"><span class="square-icon">复</span><span class="eyebrow">DUE REVIEW</span></div>
          <strong class="review-number">{{ data.reviewDue }}</strong>
          <h3>个单词等待复习</h3>
          <p>其中 5 个是昨日易错词，建议今天完成。</p>
          <RouterLink to="/review" class="text-link">开始复习 <span>→</span></RouterLink>
        </article>
      </section>

      <section class="metric-row">
        <article><span>今日已复习</span><strong>{{ data.reviewedToday }}</strong><small>次回忆练习</small></article>
        <article><span>累计已掌握</span><strong>{{ data.masteredTotal }}</strong><small>目标词库 25%</small></article>
        <article><span>连续学习</span><strong>{{ data.continuousDays }}<i>天</i></strong><small>本月最佳 14 天</small></article>
      </section>

      <section class="lower-grid">
        <article class="panel activity-panel">
          <div class="panel-heading"><div><span class="eyebrow">WEEKLY RHYTHM</span><h2>本周学习节奏</h2></div><RouterLink to="/statistics">查看统计</RouterLink></div>
          <div class="activity-chart">
            <div v-for="day in data.weeklyActivity" :key="day.date" class="activity-day">
              <div class="bar-wrap"><i :class="{ completed: day.count >= day.target }" :style="{ height: `${Math.min(100, (day.count / day.target) * 100)}%` }"></i></div>
              <strong>{{ day.count }}</strong><span>{{ day.label }}</span>
            </div>
          </div>
          <p class="chart-note"><span></span>每天的稳定投入，比偶尔突击更有效。</p>
        </article>

        <article class="panel difficult-panel">
          <div class="panel-heading"><div><span class="eyebrow">NEEDS ATTENTION</span><h2>最近易错词</h2></div></div>
          <RouterLink v-for="word in data.difficultWords" :key="word.id" :to="`/words/${word.id}`" class="difficult-word">
            <span><strong>{{ word.word }}</strong><small>{{ word.phonetic }}</small></span>
            <em>{{ word.briefDefinition }}</em><b>→</b>
          </RouterLink>
        </article>
      </section>
    </template>
  </div>
</template>

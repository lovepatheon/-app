<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { wordsApi, type MasteryLevel, type WordLevel, type WordSummary } from '@/api'

const loading = ref(true)
const words = ref<WordSummary[]>([])
const filters = reactive<{ keyword: string; level: WordLevel | 'ALL'; mastery: MasteryLevel | 'ALL' }>({
  keyword: '',
  level: 'ALL',
  mastery: 'ALL',
})

let searchTimer: ReturnType<typeof setTimeout>
const load = async () => {
  loading.value = true
  try {
    const result = await wordsApi.list(filters)
    words.value = result.content
  } finally {
    loading.value = false
  }
}

watch(filters, () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(load, 180)
}, { deep: true })

onMounted(load)

const masteryLabel: Record<MasteryLevel, string> = {
  NEW: '新词', LEARNING: '学习中', FAMILIAR: '熟悉', MASTERED: '已掌握',
}
</script>

<template>
  <div class="page library-page">
    <header class="page-header">
      <div><span class="eyebrow">PERSONAL WORD BANK</span><h1>我的词库</h1><p>搜索、筛选并回看每个单词的释义与用法。</p></div>
      <div class="library-count"><strong>1,942</strong><span>差集词汇总量<br />等待最终校验</span></div>
    </header>

    <section class="filter-panel">
      <label class="search-box"><span>⌕</span><input v-model="filters.keyword" placeholder="搜索单词或中文释义" /></label>
      <div class="segmented">
        <button v-for="item in [{v:'ALL',l:'全部'}, {v:'CET4',l:'四级'}, {v:'CET6',l:'六级'}]" :key="item.v" type="button" :class="{ active: filters.level === item.v }" @click="filters.level = item.v as typeof filters.level">{{ item.l }}</button>
      </div>
      <select v-model="filters.mastery" aria-label="掌握程度">
        <option value="ALL">全部掌握程度</option><option value="NEW">新词</option><option value="LEARNING">学习中</option><option value="FAMILIAR">熟悉</option><option value="MASTERED">已掌握</option>
      </select>
    </section>

    <div class="result-summary"><span>当前展示 {{ words.length }} 个示例词</span><small>正式词库将在后端数据导入后分页加载</small></div>

    <section v-if="loading" class="word-grid"><div v-for="n in 6" :key="n" class="word-card skeleton-card"></div></section>
    <section v-else-if="words.length" class="word-grid">
      <RouterLink v-for="word in words" :key="word.id" :to="`/words/${word.id}`" class="word-card">
        <div class="word-card-top"><div><strong>{{ word.word }}</strong><span>{{ word.phonetic }}</span></div><i :class="['mastery-dot', word.mastery.toLowerCase()]"></i></div>
        <p>{{ word.briefDefinition }}</p>
        <div class="word-card-bottom"><span v-for="level in word.level" :key="level" class="level-tag">{{ level }}</span><em>{{ masteryLabel[word.mastery] }}</em><b>→</b></div>
      </RouterLink>
    </section>
    <div v-else class="empty-state"><strong>没有匹配的单词</strong><p>尝试清空关键词或调整筛选条件。</p></div>
  </div>
</template>

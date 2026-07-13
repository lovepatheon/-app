<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { studyApi, type RecallRating, type StudyQueue } from '@/api'

const props = defineProps<{ mode: 'NEW' | 'REVIEW' }>()

const loading = ref(true)
const queue = ref<StudyQueue | null>(null)
const index = ref(0)
const revealed = ref(false)
const finished = ref(false)
const submitting = ref(false)
const startedAt = ref(Date.now())

const currentWord = computed(() => queue.value?.words[index.value])
const progress = computed(() => {
  if (!queue.value?.total) return 0
  return Math.round((index.value / queue.value.total) * 100)
})

const load = async () => {
  loading.value = true
  try {
    queue.value = await studyApi.queue(props.mode)
  } finally {
    loading.value = false
  }
}

const reveal = () => {
  revealed.value = true
}

const playPronunciation = () => {
  ElMessage.info('发音资源接口已预留，待后端返回 pronunciationUrl')
}

const handleKeyboard = (event: KeyboardEvent) => {
  if (event.code === 'Space' && !revealed.value && !finished.value) {
    event.preventDefault()
    reveal()
  }
}

const answer = async (rating: RecallRating) => {
  if (!queue.value || !currentWord.value) return
  submitting.value = true
  try {
    await studyApi.answer({
      sessionId: queue.value.sessionId,
      wordId: currentWord.value.id,
      rating,
      responseTimeMs: Date.now() - startedAt.value,
    })
    if (index.value >= queue.value.words.length - 1) {
      finished.value = true
    } else {
      index.value += 1
      revealed.value = false
      startedAt.value = Date.now()
    }
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  load()
  window.addEventListener('keydown', handleKeyboard)
})
onBeforeUnmount(() => window.removeEventListener('keydown', handleKeyboard))
</script>

<template>
  <section class="session-page">
    <div class="session-topbar">
      <div>
        <span class="eyebrow">{{ mode === 'NEW' ? 'NEW WORDS' : 'SPACED REVIEW' }}</span>
        <h1>{{ mode === 'NEW' ? '学习新词' : '到期复习' }}</h1>
      </div>
      <div class="session-progress">
        <span>{{ Math.min(index + 1, queue?.total || 0) }} / {{ queue?.total || 0 }}</span>
        <div class="progress-track"><i :style="{ width: `${progress}%` }"></i></div>
      </div>
    </div>

    <div v-if="loading" class="study-card loading-card"><div class="skeleton wide"></div><div class="skeleton"></div></div>

    <div v-else-if="finished" class="completion-card">
      <span class="completion-mark">✓</span>
      <span class="eyebrow">SESSION COMPLETE</span>
      <h2>这一轮完成了</h2>
      <p>你已完成 {{ queue?.total }} 个单词。复习计划已经按照回忆程度更新。</p>
      <div class="completion-actions">
        <RouterLink to="/" class="button primary">回到首页</RouterLink>
        <button class="button secondary" type="button" @click="finished = false; index = 0; revealed = false">再来一轮</button>
      </div>
    </div>

    <template v-else-if="currentWord">
      <article class="study-card">
        <div class="word-meta">
          <span v-for="level in currentWord.level" :key="level" class="level-tag">{{ level }}</span>
          <span class="word-index">NO. {{ String(currentWord.id).padStart(4, '0') }}</span>
        </div>
        <div class="word-center">
          <button class="sound-button" type="button" aria-label="播放发音" @click="playPronunciation">↗</button>
          <h2>{{ currentWord.word }}</h2>
          <p>{{ currentWord.phonetic }}</p>
        </div>

        <button v-if="!revealed" class="reveal-area" type="button" @click="reveal">
          <span>先在脑中回忆词义</span>
          <strong>点击查看答案</strong>
        </button>

        <div v-else class="answer-panel">
          <div class="answer-primary">
            <span>{{ currentWord.senses[0]?.partOfSpeech }}</span>
            <strong>{{ currentWord.briefDefinition }}</strong>
          </div>
          <p class="usage-note">{{ currentWord.usageNote }}</p>
          <div class="example-block">
            <p>{{ currentWord.examples[0]?.sentence }}</p>
            <span>{{ currentWord.examples[0]?.translation }}</span>
          </div>
          <div class="collocation-row">
            <span v-for="item in currentWord.collocations.slice(0, 3)" :key="item">{{ item }}</span>
          </div>
        </div>
      </article>

      <div v-if="revealed" class="rating-panel">
        <p>你刚才回忆得怎么样？</p>
        <div class="rating-grid">
          <button :disabled="submitting" type="button" class="rating again" @click="answer('AGAIN')"><strong>忘记了</strong><span>本轮再见</span></button>
          <button :disabled="submitting" type="button" class="rating hard" @click="answer('HARD')"><strong>有点难</strong><span>约 1 天后</span></button>
          <button :disabled="submitting" type="button" class="rating good" @click="answer('GOOD')"><strong>记得</strong><span>约 4 天后</span></button>
          <button :disabled="submitting" type="button" class="rating easy" @click="answer('EASY')"><strong>很熟练</strong><span>约 12 天后</span></button>
        </div>
      </div>
      <p v-else class="keyboard-hint">按空格或点击卡片查看答案</p>
    </template>
  </section>
</template>

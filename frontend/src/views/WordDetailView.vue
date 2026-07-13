<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { wordsApi, type WordDetail } from '@/api'

const route = useRoute()
const loading = ref(true)
const word = ref<WordDetail | null>(null)

const load = async () => {
  loading.value = true
  try {
    word.value = await wordsApi.detail(Number(route.params.id))
  } finally {
    loading.value = false
  }
}

const playPronunciation = () => ElMessage.info('发音资源接口已预留，待后端返回 pronunciationUrl')

watch(() => route.params.id, load)
onMounted(load)
</script>

<template>
  <div class="page detail-page">
    <RouterLink to="/words" class="back-link">← 返回词库</RouterLink>
    <div v-if="loading" class="detail-layout"><div class="panel skeleton-card"></div><div class="panel skeleton-card"></div></div>
    <template v-else-if="word">
      <header class="word-hero">
        <div><div class="word-levels"><span v-for="level in word.level" :key="level" class="level-tag">{{ level }}</span><span class="mastery-label">学习中</span></div><h1>{{ word.word }}</h1><p>{{ word.phonetic }} <button type="button" class="inline-sound" @click="playPronunciation">↗ 播放</button></p></div>
        <div class="word-hero-definition"><span>核心释义</span><strong>{{ word.briefDefinition }}</strong></div>
      </header>

      <div class="detail-layout">
        <section class="detail-main">
          <article class="panel detail-section"><span class="section-number">01</span><div><h2>词义详解</h2><div v-for="sense in word.senses" :key="sense.definitionCn" class="sense-row"><b>{{ sense.partOfSpeech }}</b><span><strong>{{ sense.definitionCn }}</strong><small>{{ sense.definitionEn }}</small></span></div></div></article>
          <article class="panel detail-section"><span class="section-number">02</span><div><h2>例句与语境</h2><div v-for="(example, index) in word.examples" :key="example.sentence" class="detail-example"><span>0{{ index + 1 }}</span><p><strong>{{ example.sentence }}</strong><small>{{ example.translation }}</small></p></div></div></article>
          <article class="panel detail-section"><span class="section-number">03</span><div><h2>用法提醒</h2><p class="large-note">{{ word.usageNote }}</p><div class="collocation-row"><span v-for="item in word.collocations" :key="item">{{ item }}</span></div></div></article>
        </section>
        <aside class="detail-side">
          <article class="panel memory-card"><span class="eyebrow">MEMORY TIP</span><h3>记忆提示</h3><p>{{ word.memoryTip }}</p></article>
          <article class="panel source-card"><span class="eyebrow">DATA SOURCE</span><h3>数据说明</h3><p>{{ word.source }}</p><small>词义与例句仅为前端演示内容，正式内容需经过授权和校验。</small></article>
          <RouterLink to="/review" class="button primary full">加入一次复习</RouterLink>
        </aside>
      </div>
    </template>
  </div>
</template>

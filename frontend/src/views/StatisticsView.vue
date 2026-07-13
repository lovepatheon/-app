<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { statisticsApi, type StatisticsData } from '@/api'

const data = ref<StatisticsData | null>(null)
const range = ref<'7D' | '30D'>('7D')
const maxDaily = computed(() => Math.max(...(data.value?.daily.map((item) => item.learned + item.reviewed) || [1])))

const load = async () => { data.value = await statisticsApi.overview(range.value) }
onMounted(load)
</script>

<template>
  <div class="page statistics-page">
    <header class="page-header"><div><span class="eyebrow">LEARNING INSIGHTS</span><h1>学习统计</h1><p>关注节奏，而不只是数字。</p></div><div class="segmented"><button type="button" :class="{active: range === '7D'}" @click="range='7D';load()">近 7 天</button><button type="button" :class="{active: range === '30D'}" @click="range='30D';load()">近 30 天</button></div></header>
    <template v-if="data">
      <section class="stats-grid">
        <article><span>本期学习</span><strong>{{ data.totalLearned }}</strong><small>个新词</small></article>
        <article><span>累计掌握</span><strong>{{ data.totalMastered }}</strong><small>个单词</small></article>
        <article><span>复习正确率</span><strong>{{ data.reviewAccuracy }}%</strong><small>较上周 +3%</small></article>
        <article><span>投入时间</span><strong>{{ data.totalMinutes }}</strong><small>分钟</small></article>
      </section>
      <section class="stats-layout">
        <article class="panel trend-panel"><div class="panel-heading"><div><span class="eyebrow">DAILY ACTIVITY</span><h2>每日学习量</h2></div><div class="chart-legend"><span class="learned"></span>新词 <span class="reviewed"></span>复习</div></div><div class="stack-chart"><div v-for="item in data.daily" :key="item.date" class="stack-day"><div class="stack-bars"><i class="reviewed" :style="{height: `${item.reviewed/maxDaily*100}%`}"></i><i class="learned" :style="{height: `${item.learned/maxDaily*100}%`}"></i></div><strong>{{ item.learned + item.reviewed }}</strong><span>{{ item.label }}</span></div></div></article>
        <article class="panel mastery-panel"><div class="panel-heading"><div><span class="eyebrow">MASTERY</span><h2>掌握分布</h2></div></div><div class="donut" :style="{background: `conic-gradient(${data.masteryDistribution.map((item, i) => `${item.color} ${i*25}% ${(i+1)*25}%`).join(',')})`}"><div><strong>980</strong><span>已学习</span></div></div><div class="mastery-list"><div v-for="item in data.masteryDistribution" :key="item.label"><i :style="{background:item.color}"></i><span>{{ item.label }}</span><strong>{{ item.value }}</strong></div></div></article>
      </section>
      <section class="panel achievements"><div class="panel-heading"><div><span class="eyebrow">MILESTONES</span><h2>最近里程碑</h2></div></div><div class="achievement-list"><article v-for="(item,index) in data.recentAchievements" :key="item.title"><span>0{{index+1}}</span><div><strong>{{item.title}}</strong><p>{{item.description}}</p></div><time>{{item.date}}</time></article></div></section>
    </template>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi, type UserSettings } from '@/api'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const form = reactive<UserSettings>({
  dailyNewWords: auth.user?.settings.dailyNewWords || 20,
  dailyReviewLimit: auth.user?.settings.dailyReviewLimit || 80,
  preferredLevel: auth.user?.settings.preferredLevel || 'BOTH',
  reminderTime: auth.user?.settings.reminderTime || '20:30',
  soundEnabled: auth.user?.settings.soundEnabled ?? true,
})

const save = async () => {
  await userApi.updateSettings(form)
  if (auth.user) {
    auth.user.settings = { ...form }
    localStorage.setItem('vocab_user', JSON.stringify(auth.user))
  }
  ElMessage.success('学习设置已保存')
}
</script>

<template>
  <div class="page settings-page">
    <header class="page-header"><div><span class="eyebrow">PREFERENCES</span><h1>学习设置</h1><p>把计划调到刚好能够长期坚持。</p></div></header>
    <div class="settings-layout">
      <aside class="profile-panel"><span class="profile-avatar">{{auth.user?.avatarText}}</span><h2>{{auth.user?.nickname}}</h2><p>@{{auth.user?.username}}</p><div><span>连续学习</span><strong>{{auth.user?.continuousDays}} 天</strong></div><div><span>邮箱</span><strong>{{auth.user?.email}}</strong></div></aside>
      <form class="panel settings-form" @submit.prevent="save">
        <section><span class="eyebrow">DAILY PLAN</span><h2>每日计划</h2><label><span><strong>每日新词</strong><small>建议初期保持在 15–25 个</small></span><input v-model.number="form.dailyNewWords" type="number" min="5" max="100" /><em>词</em></label><label><span><strong>复习上限</strong><small>超过上限的内容顺延到次日</small></span><input v-model.number="form.dailyReviewLimit" type="number" min="20" max="300" /><em>词</em></label></section>
        <section><span class="eyebrow">WORD BANK</span><h2>词库偏好</h2><div class="choice-grid"><button v-for="item in [{v:'CET4',t:'四级优先',d:'先学习四级差集'}, {v:'CET6',t:'六级优先',d:'先学习六级差集'}, {v:'BOTH',t:'混合学习',d:'按难度统一排序'}]" :key="item.v" type="button" :class="{active: form.preferredLevel === item.v}" @click="form.preferredLevel=item.v as UserSettings['preferredLevel']"><strong>{{item.t}}</strong><span>{{item.d}}</span></button></div></section>
        <section><span class="eyebrow">REMINDER</span><h2>提醒与声音</h2><label><span><strong>每日提醒时间</strong><small>仅保存偏好，通知能力后续接入</small></span><input v-model="form.reminderTime" type="time" /></label><label><span><strong>单词发音</strong><small>展示单词时自动播放发音</small></span><button type="button" :class="['toggle', {on: form.soundEnabled}]" :aria-pressed="form.soundEnabled" @click="form.soundEnabled=!form.soundEnabled"><i></i></button></label></section>
        <div class="settings-actions"><button class="button primary" type="submit">保存设置</button></div>
      </form>
    </div>
  </div>
</template>

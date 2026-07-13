<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const loading = ref(false)
const form = reactive({ username: 'demo', password: '123456', remember: true })

const submit = async () => {
  if (!form.username || !form.password) return ElMessage.warning('请输入账号和密码')
  loading.value = true
  try {
    await auth.login({ username: form.username, password: form.password })
    await router.push(String(route.query.redirect || '/'))
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="auth-page">
    <section class="auth-story">
      <RouterLink to="/login" class="brand light"><span class="brand-mark">W</span><span><strong>WordHarbor</strong><small>四六级差集词库</small></span></RouterLink>
      <div class="story-copy">
        <span class="eyebrow warm">FOCUS ON WHAT MATTERS</span>
        <h1>少背一点重复，<br />多记一点真正陌生。</h1>
        <p>从四级、六级词汇中剔除高考考纲词，配合间隔复习，把时间留给真正需要掌握的部分。</p>
      </div>
      <div class="story-metric"><strong>1,942</strong><span>预计差集词汇<br />等待最终词表校验</span></div>
    </section>

    <section class="auth-form-wrap">
      <form class="auth-form" @submit.prevent="submit">
        <div class="auth-heading"><span class="eyebrow">WELCOME BACK</span><h2>继续你的词汇航程</h2><p>使用账号登录并同步学习进度。</p></div>
        <label class="field-label">账号<input v-model.trim="form.username" autocomplete="username" placeholder="用户名或邮箱" /></label>
        <label class="field-label">密码<input v-model="form.password" type="password" autocomplete="current-password" placeholder="输入密码" /></label>
        <div class="form-row"><label class="check-label"><input v-model="form.remember" type="checkbox" />保持登录</label><button class="text-button" type="button">忘记密码？</button></div>
        <button class="button primary full" type="submit" :disabled="loading">{{ loading ? '正在登录…' : '登录' }}</button>
        <div class="demo-hint"><span>体验账号</span><code>demo</code><span>/</span><code>123456</code></div>
        <p class="auth-switch">还没有账号？<RouterLink to="/register">创建账号</RouterLink></p>
      </form>
    </section>
  </main>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)
const form = reactive({ username: '', nickname: '', email: '', password: '', confirmPassword: '' })

const submit = async () => {
  if (!form.username || !form.nickname || !form.email || !form.password) return ElMessage.warning('请填写完整信息')
  if (form.password.length < 6) return ElMessage.warning('密码至少需要 6 位')
  if (form.password !== form.confirmPassword) return ElMessage.warning('两次输入的密码不一致')
  loading.value = true
  try {
    await auth.register({
      username: form.username,
      nickname: form.nickname,
      email: form.email,
      password: form.password,
    })
    await router.push('/')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="auth-page register-page">
    <section class="auth-story">
      <RouterLink to="/login" class="brand light"><span class="brand-mark">W</span><span><strong>WordHarbor</strong><small>四六级差集词库</small></span></RouterLink>
      <div class="story-copy"><span class="eyebrow warm">START SMALL, STAY STEADY</span><h1>每天二十词，<br />把陌生变成笃定。</h1><p>建立个人学习计划，让新词、复习与掌握程度始终保持清晰。</p></div>
      <ol class="mini-steps"><li><b>01</b><span>设定每日词量</span></li><li><b>02</b><span>学习真正陌生的词</span></li><li><b>03</b><span>按遗忘节奏复习</span></li></ol>
    </section>
    <section class="auth-form-wrap">
      <form class="auth-form register-form" @submit.prevent="submit">
        <div class="auth-heading"><span class="eyebrow">CREATE ACCOUNT</span><h2>建立你的学习档案</h2><p>注册后即可保存进度和复习计划。</p></div>
        <div class="two-fields"><label class="field-label">用户名<input v-model.trim="form.username" autocomplete="username" /></label><label class="field-label">昵称<input v-model.trim="form.nickname" /></label></div>
        <label class="field-label">邮箱<input v-model.trim="form.email" type="email" autocomplete="email" /></label>
        <div class="two-fields"><label class="field-label">密码<input v-model="form.password" type="password" autocomplete="new-password" /></label><label class="field-label">确认密码<input v-model="form.confirmPassword" type="password" autocomplete="new-password" /></label></div>
        <button class="button primary full" type="submit" :disabled="loading">{{ loading ? '正在创建…' : '创建账号' }}</button>
        <p class="auth-switch">已有账号？<RouterLink to="/login">返回登录</RouterLink></p>
      </form>
    </section>
  </main>
</template>

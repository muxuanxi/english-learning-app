<template>
  <div class="auth-page">
    <div class="auth-card card">
      <div class="auth-header">
        <router-link to="/" class="auth-logo">📚 EnglishMaster</router-link>
        <h2>欢迎回来</h2>
        <p>登录你的账号继续学习</p>
      </div>

      <form @submit.prevent="handleLogin" class="auth-form">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <input v-model="form.username" type="text" class="form-input"
                 placeholder="请输入用户名" required />
        </div>
        <div class="form-group">
          <label class="form-label">密码</label>
          <input v-model="form.password" type="password" class="form-input"
                 placeholder="请输入密码" required />
        </div>

        <div v-if="error" class="alert alert-error">{{ error }}</div>

        <button type="submit" class="btn btn-primary btn-lg submit-btn" :disabled="loading">
          <span v-if="loading" class="spinner" style="width:20px;height:20px;border-width:2px;"></span>
          <span v-else>登录</span>
        </button>
      </form>

      <div class="auth-footer">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/store/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

const form = reactive({ username: '', password: '' })
const loading = ref(false)
const error = ref('')

async function handleLogin() {
  error.value = ''
  loading.value = true
  try {
    await auth.login(form.username, form.password)
    const redirect = route.query.redirect || '/dashboard'
    router.push(redirect)
  } catch (e) {
    error.value = e.response?.data?.message || '登录失败，请检查用户名和密码'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: linear-gradient(135deg, #eef2ff 0%, #f0f9ff 50%, #fef3c7 100%);
}
.auth-card {
  width: 100%;
  max-width: 420px;
  padding: 40px;
}
.auth-header {
  text-align: center;
  margin-bottom: 32px;
}
.auth-logo {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--gray-900);
  display: block;
  margin-bottom: 20px;
}
.auth-header h2 { margin-bottom: 4px; }
.auth-header p { color: var(--gray-500); font-size: 0.875rem; }

.auth-form { display: flex; flex-direction: column; gap: 20px; }
.form-group { }
.submit-btn { width: 100%; padding: 14px; font-size: 1.063rem; margin-top: 8px; }

.alert {
  padding: 12px 16px;
  border-radius: var(--radius-sm);
  font-size: 0.875rem;
}
.alert-error { background: var(--error-bg); color: var(--error); }

.auth-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 0.875rem;
  color: var(--gray-500);
}
.auth-footer a { font-weight: 600; }
</style>

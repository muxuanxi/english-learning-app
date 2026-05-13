<template>
  <div class="auth-page">
    <div class="auth-card card">
      <div class="auth-header">
        <router-link to="/" class="auth-logo">📚 EnglishMaster</router-link>
        <h2>创建账号</h2>
        <p>开启你的英语学习之旅</p>
      </div>

      <form @submit.prevent="handleRegister" class="auth-form">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <input v-model="form.username" type="text" class="form-input"
                 placeholder="3-50个字符" required minlength="3" maxlength="50" />
        </div>
        <div class="form-group">
          <label class="form-label">邮箱</label>
          <input v-model="form.email" type="email" class="form-input"
                 placeholder="your@email.com" required />
        </div>
        <div class="form-group">
          <label class="form-label">昵称</label>
          <input v-model="form.nickname" type="text" class="form-input"
                 placeholder="选填，默认为用户名" />
        </div>
        <div class="form-group">
          <label class="form-label">密码</label>
          <input v-model="form.password" type="password" class="form-input"
                 placeholder="至少6位" required minlength="6" />
        </div>

        <div v-if="error" class="alert alert-error">{{ error }}</div>

        <button type="submit" class="btn btn-primary btn-lg submit-btn" :disabled="loading">
          <span v-if="loading" class="spinner" style="width:20px;height:20px;border-width:2px;"></span>
          <span v-else>注册</span>
        </button>
      </form>

      <div class="auth-footer">
        已有账号？<router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'

const auth = useAuthStore()
const router = useRouter()

const form = reactive({
  username: '', email: '', nickname: '', password: ''
})
const loading = ref(false)
const error = ref('')

async function handleRegister() {
  error.value = ''
  loading.value = true
  try {
    await auth.register({
      username: form.username,
      email: form.email,
      nickname: form.nickname || form.username,
      password: form.password
    })
    router.push('/dashboard')
  } catch (e) {
    error.value = e.response?.data?.message || '注册失败，请重试'
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

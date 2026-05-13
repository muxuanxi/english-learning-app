<template>
  <header class="navbar" :class="{ scrolled }">
    <div class="navbar-inner container">
      <router-link to="/" class="logo">
        <span class="logo-icon">📚</span>
        <span class="logo-text">English<span class="logo-highlight">Master</span></span>
      </router-link>

      <nav class="nav-links" :class="{ open: mobileOpen }">
        <router-link to="/vocabulary" @click="closeMobile">
          <span class="nav-icon">📝</span>词汇
        </router-link>
        <router-link to="/grammar" @click="closeMobile">
          <span class="nav-icon">📖</span>语法
        </router-link>
        <router-link to="/reading" @click="closeMobile">
          <span class="nav-icon">📰</span>阅读
        </router-link>
        <router-link to="/listening" @click="closeMobile">
          <span class="nav-icon">🎧</span>听力
        </router-link>
        <router-link to="/quiz" @click="closeMobile">
          <span class="nav-icon">✅</span>测验
        </router-link>
        <router-link to="/search" @click="closeMobile" class="nav-highlight">
          <span class="nav-icon">🔍</span>搜索
        </router-link>
        <router-link to="/translate" @click="closeMobile" class="nav-highlight">
          <span class="nav-icon">🌐</span>翻译
        </router-link>
      </nav>

      <div class="nav-actions">
        <template v-if="auth.isLoggedIn">
          <router-link to="/dashboard" class="btn btn-outline btn-sm">
            📊 学习报告
          </router-link>
          <div class="user-menu">
            <span class="user-avatar">{{ userInitial }}</span>
            <span class="user-name">{{ auth.user?.nickname }}</span>
            <button class="btn btn-sm btn-secondary" @click="handleLogout">退出</button>
          </div>
        </template>
        <template v-else>
          <router-link to="/login" class="btn btn-outline btn-sm">登录</router-link>
          <router-link to="/register" class="btn btn-primary btn-sm">注册</router-link>
        </template>
      </div>

      <button class="mobile-toggle" @click="mobileOpen = !mobileOpen">
        <span></span><span></span><span></span>
      </button>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'

const auth = useAuthStore()
const router = useRouter()
const mobileOpen = ref(false)
const scrolled = ref(false)

const userInitial = computed(() => (auth.user?.nickname || 'U')[0].toUpperCase())

function handleLogout() {
  auth.logout()
  router.push('/')
}

function closeMobile() { mobileOpen.value = false }

function onScroll() { scrolled.value = window.scrollY > 10 }
onMounted(() => window.addEventListener('scroll', onScroll))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid transparent;
  transition: all 0.3s ease;
}
.navbar.scrolled {
  border-bottom-color: var(--gray-200);
  box-shadow: var(--shadow-sm);
}
.navbar-inner {
  display: flex;
  align-items: center;
  height: var(--header-height);
  gap: 32px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.35rem;
  font-weight: 800;
  color: var(--gray-900);
  flex-shrink: 0;
}
.logo-icon { font-size: 1.5rem; }
.logo-highlight { color: var(--primary); }

.nav-links {
  display: flex;
  gap: 4px;
  flex: 1;
  justify-content: center;
}
.nav-links a {
  padding: 8px 16px;
  border-radius: var(--radius-sm);
  color: var(--gray-600);
  font-weight: 500;
  font-size: 0.875rem;
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  gap: 4px;
}
.nav-links a:hover, .nav-links a.router-link-active {
  background: var(--primary-bg);
  color: var(--primary);
}
.nav-icon { font-size: 1rem; }

.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 8px;
}
.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 0.813rem;
}
.user-name {
  font-weight: 600;
  font-size: 0.875rem;
  color: var(--gray-700);
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.mobile-toggle {
  display: none;
  flex-direction: column;
  gap: 5px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
}
.mobile-toggle span {
  width: 24px;
  height: 2px;
  background: var(--gray-700);
  border-radius: 2px;
  transition: all 0.3s ease;
}

@media (max-width: 900px) {
  .nav-links {
    position: fixed;
    top: var(--header-height);
    left: 0;
    right: 0;
    background: white;
    flex-direction: column;
    padding: 16px;
    box-shadow: var(--shadow-lg);
    transform: translateY(-120%);
    opacity: 0;
    transition: all 0.3s ease;
  }
  .nav-links.open {
    transform: translateY(0);
    opacity: 1;
  }
  .mobile-toggle { display: flex; }
  .nav-actions .user-name { display: none; }
}
</style>

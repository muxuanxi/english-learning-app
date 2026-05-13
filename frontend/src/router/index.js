import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/auth'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/vocabulary',
    name: 'Vocabulary',
    component: () => import('@/views/Vocabulary.vue'),
    meta: { title: '词汇学习' }
  },
  {
    path: '/grammar',
    name: 'Grammar',
    component: () => import('@/views/Grammar.vue'),
    meta: { title: '语法课程' }
  },
  {
    path: '/grammar/:id',
    name: 'GrammarDetail',
    component: () => import('@/views/GrammarDetail.vue'),
    meta: { title: '语法详情' }
  },
  {
    path: '/reading',
    name: 'Reading',
    component: () => import('@/views/Reading.vue'),
    meta: { title: '阅读训练' }
  },
  {
    path: '/reading/:id',
    name: 'ReadingDetail',
    component: () => import('@/views/ReadingDetail.vue'),
    meta: { title: '文章阅读' }
  },
  {
    path: '/listening',
    name: 'Listening',
    component: () => import('@/views/Listening.vue'),
    meta: { title: '听力训练' }
  },
  {
    path: '/quiz',
    name: 'Quiz',
    component: () => import('@/views/Quiz.vue'),
    meta: { title: '在线测验' }
  },
  {
    path: '/quiz/:id',
    name: 'QuizPlay',
    component: () => import('@/views/QuizPlay.vue'),
    meta: { title: '答题中' }
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/views/Search.vue'),
    meta: { title: '全局搜索' }
  },
  {
    path: '/translate',
    name: 'Translate',
    component: () => import('@/views/Translate.vue'),
    meta: { title: '翻译工具' }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { title: '学习仪表盘', requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  document.title = (to.meta.title || 'EnglishMaster') + ' - EnglishMaster'

  if (to.meta.requiresAuth) {
    const authStore = useAuthStore()
    if (!authStore.isLoggedIn) {
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }
  }
  next()
})

export default router

<template>
  <div class="page">
    <div class="container">
      <button class="btn btn-secondary btn-sm back-btn" @click="$router.push('/grammar')">
        ← 返回语法列表
      </button>

      <div class="loading-screen" v-if="loading"><div class="spinner"></div></div>

      <article v-if="lesson" class="grammar-article card">
        <header class="article-header">
          <span class="badge" :class="difficultyBadge(lesson.difficultyLevel)">
            {{ difficultyLabel(lesson.difficultyLevel) }}
          </span>
          <h1>{{ lesson.title }}</h1>
          <p class="category" v-if="lesson.category">{{ lesson.category }}</p>
        </header>
        <div class="article-body" v-html="lesson.contentHtml"></div>
      </article>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { grammarAPI } from '@/api'

const route = useRoute()
const lesson = ref(null)
const loading = ref(true)

function difficultyBadge(level) {
  return level <= 1 ? 'badge-primary' : level <= 3 ? 'badge-warning' : 'badge-error'
}
function difficultyLabel(level) {
  return level <= 1 ? '初级' : level <= 3 ? '中级' : '高级'
}

onMounted(async () => {
  try {
    const res = await grammarAPI.getLesson(route.params.id)
    lesson.value = res.data.data
  } finally { loading.value = false }
})
</script>

<style scoped>
.back-btn { margin-bottom: 24px; }
.grammar-article { max-width: 800px; margin: 0 auto; padding: 40px; }
.article-header { text-align: center; margin-bottom: 32px; padding-bottom: 24px; border-bottom: 1px solid var(--gray-200); }
.article-header h1 { margin: 12px 0 8px; }
.category { color: var(--gray-500); }

.article-body :deep(h2) { margin: 32px 0 16px; color: var(--primary); }
.article-body :deep(h3) { margin: 24px 0 12px; }
.article-body :deep(p) { margin-bottom: 12px; line-height: 1.8; }
.article-body :deep(ul) { padding-left: 24px; margin-bottom: 16px; }
.article-body :deep(li) { margin-bottom: 8px; line-height: 1.7; }
.article-body :deep(em) { color: var(--secondary); font-style: normal; font-weight: 500; }
.article-body :deep(strong) { color: var(--gray-900); }

@media (max-width: 768px) {
  .grammar-article { padding: 24px; }
}
</style>

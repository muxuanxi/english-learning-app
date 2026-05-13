<template>
  <div class="page">
    <div class="container">
      <div class="page-header">
        <h1>📰 阅读训练</h1>
        <p>原汁原味英文文章，提升阅读理解能力</p>
      </div>

      <div class="category-tabs">
        <button v-for="cat in categories" :key="cat.value"
                class="btn btn-sm" :class="selectedCat === cat.value ? 'btn-primary' : 'btn-secondary'"
                @click="selectedCat = cat.value; loadArticles()">
          {{ cat.label }}
        </button>
      </div>

      <div class="loading-screen" v-if="loading"><div class="spinner"></div></div>

      <div class="article-grid" v-else>
        <article class="article-card card" v-for="article in articles" :key="article.id"
                 @click="$router.push(`/reading/${article.id}`)">
          <div class="article-meta">
            <span class="badge badge-primary">{{ article.category }}</span>
            <span class="badge" :class="levelBadge(article.difficultyLevel)">
              {{ levelLabel(article.difficultyLevel) }}
            </span>
          </div>
          <h3>{{ article.title }}</h3>
          <p class="article-source" v-if="article.author">
            {{ article.author }} · {{ article.source }}
          </p>
          <p class="article-summary">{{ article.summary }}</p>
          <div class="article-footer">
            <span>{{ article.wordCount || 0 }} 词</span>
            <span class="read-link">阅读全文 →</span>
          </div>
        </article>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { articleAPI } from '@/api'

const categories = [
  { value: '', label: '全部' },
  { value: '科技', label: '🔬 科技' },
  { value: '商业', label: '💼 商业' },
  { value: '环境', label: '🌍 环境' },
  { value: '百科', label: '📚 百科' },
]

const articles = ref([])
const loading = ref(false)
const selectedCat = ref('')

function levelBadge(lvl) {
  return lvl <= 2 ? 'badge-primary' : lvl <= 3 ? 'badge-warning' : 'badge-error'
}
function levelLabel(lvl) {
  return lvl <= 2 ? '初级' : lvl <= 3 ? '中级' : '高级'
}

async function loadArticles() {
  loading.value = true
  try {
    const params = {}
    if (selectedCat.value) params.category = selectedCat.value
    const res = await articleAPI.getArticles(params)
    articles.value = res.data.data || []
  } finally { loading.value = false }
}

onMounted(loadArticles)
</script>

<style scoped>
.category-tabs {
  display: flex; gap: 8px; justify-content: center; flex-wrap: wrap; margin-bottom: 32px;
}
.article-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 20px;
}
.article-card {
  cursor: pointer;
}
.article-card:hover { border-color: var(--secondary); }
.article-meta {
  display: flex; gap: 8px; margin-bottom: 12px;
}
.article-card h3 { margin-bottom: 4px; line-height: 1.4; }
.article-source { font-size: 0.813rem; color: var(--gray-400); margin-bottom: 8px; }
.article-summary {
  font-size: 0.875rem; color: var(--gray-500);
  display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical;
  overflow: hidden; line-height: 1.6;
}
.article-footer {
  display: flex; justify-content: space-between; align-items: center;
  margin-top: 16px; padding-top: 16px; border-top: 1px solid var(--gray-100);
  font-size: 0.813rem; color: var(--gray-400);
}
.read-link { color: var(--primary); font-weight: 600; }

@media (max-width: 768px) {
  .article-grid { grid-template-columns: 1fr; }
}
</style>

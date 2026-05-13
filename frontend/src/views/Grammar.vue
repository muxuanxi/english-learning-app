<template>
  <div class="page">
    <div class="container">
      <div class="page-header">
        <h1>📖 语法课程</h1>
        <p>系统学习英语语法，从基础到高级</p>
      </div>

      <div class="category-tabs">
        <button v-for="cat in categories" :key="cat.value"
                class="btn btn-sm" :class="selectedCat === cat.value ? 'btn-primary' : 'btn-secondary'"
                @click="selectedCat = cat.value; loadLessons()">
          {{ cat.label }}
        </button>
      </div>

      <div class="loading-screen" v-if="loading"><div class="spinner"></div></div>

      <div class="lesson-grid" v-else>
        <div class="lesson-card card" v-for="lesson in lessons" :key="lesson.id"
             @click="$router.push(`/grammar/${lesson.id}`)">
          <div class="lesson-header">
            <span class="lesson-number">{{ String(lesson.sortOrder).padStart(2, '0') }}</span>
            <span class="badge" :class="difficultyBadge(lesson.difficultyLevel)">
              {{ difficultyLabel(lesson.difficultyLevel) }}
            </span>
          </div>
          <h3>{{ lesson.title }}</h3>
          <p class="lesson-category" v-if="lesson.category">{{ lesson.category }}</p>
          <p class="lesson-desc">{{ lesson.description }}</p>
          <span class="lesson-link">开始学习 →</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { grammarAPI } from '@/api'

const categories = [
  { value: '', label: '全部' },
  { value: '时态', label: '时态' },
  { value: '从句', label: '从句' },
  { value: '语气', label: '语气' },
  { value: '语态', label: '语态' },
]

const lessons = ref([])
const loading = ref(false)
const selectedCat = ref('')

function difficultyBadge(level) {
  return level <= 1 ? 'badge-primary' : level <= 3 ? 'badge-warning' : 'badge-error'
}
function difficultyLabel(level) {
  return level <= 1 ? '初级' : level <= 3 ? '中级' : '高级'
}

async function loadLessons() {
  loading.value = true
  try {
    const params = {}
    if (selectedCat.value) params.category = selectedCat.value
    const res = await grammarAPI.getLessons(params)
    lessons.value = res.data.data || []
  } finally { loading.value = false }
}

onMounted(loadLessons)
</script>

<style scoped>
.category-tabs {
  display: flex; gap: 8px; justify-content: center; flex-wrap: wrap; margin-bottom: 32px;
}
.lesson-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}
.lesson-card {
  cursor: pointer;
  position: relative;
}
.lesson-card:hover { border-color: var(--primary); }
.lesson-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;
}
.lesson-number {
  font-size: 2rem; font-weight: 800; color: var(--gray-200);
}
.lesson-card h3 { margin-bottom: 4px; }
.lesson-category { font-size: 0.813rem; color: var(--primary); font-weight: 600; }
.lesson-desc { color: var(--gray-500); font-size: 0.875rem; margin: 12px 0; }
.lesson-link { font-weight: 600; color: var(--primary); font-size: 0.875rem; }

@media (max-width: 768px) {
  .lesson-grid { grid-template-columns: 1fr; }
}
</style>

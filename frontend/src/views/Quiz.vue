<template>
  <div class="page">
    <div class="container">
      <div class="page-header">
        <h1>✅ 在线测验</h1>
        <p>检验学习成果，发现薄弱环节</p>
      </div>

      <div class="filter-row">
        <button v-for="f in filters" :key="f.value"
                class="btn btn-sm" :class="selectedType === f.value ? 'btn-primary' : 'btn-secondary'"
                @click="selectedType = f.value; loadQuizzes()">
          {{ f.label }}
        </button>
      </div>

      <div class="loading-screen" v-if="loading"><div class="spinner"></div></div>

      <div class="quiz-grid" v-else>
        <div class="quiz-card card" v-for="quiz in quizzes" :key="quiz.id">
          <div class="quiz-type-badge" :class="typeClass(quiz.quizType)">
            {{ typeLabel(quiz.quizType) }}
          </div>
          <h3>{{ quiz.title }}</h3>
          <p class="quiz-desc">{{ quiz.description }}</p>

          <div class="quiz-info">
            <div class="info-item">
              <span class="info-label">题目数</span>
              <span class="info-value">{{ quiz.totalQuestions || quiz.questions?.length || 0 }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">时间</span>
              <span class="info-value">{{ quiz.timeLimitMinutes ? quiz.timeLimitMinutes + '分钟' : '不限时' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">及格线</span>
              <span class="info-value">{{ quiz.passScore }}分</span>
            </div>
          </div>

          <div class="quiz-footer">
            <span class="badge" :class="levelBadge(quiz.difficultyLevel)">
              {{ levelLabel(quiz.difficultyLevel) }}
            </span>
            <button class="btn btn-primary btn-sm" @click="$router.push(`/quiz/${quiz.id}`)">
              开始答题 →
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { quizAPI } from '@/api'

const filters = [
  { value: '', label: '全部' },
  { value: 'VOCABULARY', label: '📝 词汇' },
  { value: 'GRAMMAR', label: '📖 语法' },
  { value: 'READING', label: '📰 阅读' },
  { value: 'LISTENING', label: '🎧 听力' },
  { value: 'COMPREHENSIVE', label: '🎯 综合' },
]

const quizzes = ref([])
const loading = ref(false)
const selectedType = ref('')

function typeLabel(t) {
  const map = { VOCABULARY: '词汇', GRAMMAR: '语法', READING: '阅读', LISTENING: '听力', COMPREHENSIVE: '综合' }
  return map[t] || t
}
function typeClass(t) {
  const map = { VOCABULARY: 'type-vocab', GRAMMAR: 'type-grammar', READING: 'type-reading', LISTENING: 'type-listening', COMPREHENSIVE: 'type-comp' }
  return map[t] || 'type-vocab'
}
function levelBadge(lvl) {
  return lvl <= 2 ? 'badge-primary' : lvl <= 4 ? 'badge-warning' : 'badge-error'
}
function levelLabel(lvl) {
  return lvl <= 2 ? '初级' : lvl <= 4 ? '中级' : '高级'
}

async function loadQuizzes() {
  loading.value = true
  try {
    const params = {}
    if (selectedType.value) params.type = selectedType.value
    const res = await quizAPI.getQuizzes(params)
    quizzes.value = res.data.data || []
  } finally { loading.value = false }
}

onMounted(loadQuizzes)
</script>

<style scoped>
.filter-row {
  display: flex; gap: 8px; justify-content: center; flex-wrap: wrap; margin-bottom: 32px;
}
.quiz-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
}
.quiz-card { position: relative; overflow: hidden; }
.quiz-type-badge {
  position: absolute; top: 0; right: 0;
  padding: 6px 16px; font-size: 0.75rem; font-weight: 700; color: white;
  border-radius: 0 var(--radius-lg) 0 var(--radius);
}
.type-vocab { background: var(--primary); }
.type-grammar { background: var(--secondary); }
.type-reading { background: var(--accent); }
.type-listening { background: var(--success); }
.type-comp { background: #8b5cf6; }

.quiz-card h3 { margin-top: 8px; margin-bottom: 8px; }
.quiz-desc { color: var(--gray-500); font-size: 0.875rem; margin-bottom: 20px; }

.quiz-info {
  display: flex; gap: 16px; margin-bottom: 20px;
}
.info-item {
  text-align: center; flex: 1;
  background: var(--gray-50); padding: 10px; border-radius: var(--radius-sm);
}
.info-label { display: block; font-size: 0.75rem; color: var(--gray-400); }
.info-value { font-weight: 700; color: var(--gray-700); }

.quiz-footer {
  display: flex; justify-content: space-between; align-items: center;
}

@media (max-width: 768px) {
  .quiz-grid { grid-template-columns: 1fr; }
}
</style>

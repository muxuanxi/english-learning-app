<template>
  <div class="page">
    <div class="container">
      <div class="page-header">
        <h1>📊 学习仪表盘</h1>
        <p>追踪你的学习进展，见证每一步成长</p>
      </div>

      <!-- Stats Cards -->
      <div class="stats-grid">
        <div class="stat-card card">
          <span class="stat-icon">📚</span>
          <div class="stat-info">
            <span class="stat-num">{{ stats?.totalWordsLearned || 0 }}</span>
            <span class="stat-label">已学词汇</span>
          </div>
        </div>
        <div class="stat-card card">
          <span class="stat-icon">✅</span>
          <div class="stat-info">
            <span class="stat-num">{{ stats?.masteredWords || 0 }}</span>
            <span class="stat-label">已掌握词汇</span>
          </div>
        </div>
        <div class="stat-card card">
          <span class="stat-icon">📝</span>
          <div class="stat-info">
            <span class="stat-num">{{ stats?.totalQuizTaken || 0 }}</span>
            <span class="stat-label">完成测验</span>
          </div>
        </div>
        <div class="stat-card card">
          <span class="stat-icon">🎯</span>
          <div class="stat-info">
            <span class="stat-num">{{ stats?.avgQuizScore || 0 }}%</span>
            <span class="stat-label">平均得分</span>
          </div>
        </div>
      </div>

      <!-- Checkin Section -->
      <div class="checkin-section card">
        <div class="checkin-header">
          <div>
            <h3>🔥 每日打卡</h3>
            <p>已连续打卡 <strong>{{ stats?.consecutiveDays || 0 }}</strong> 天</p>
          </div>
          <button class="btn btn-accent" @click="handleCheckin" :disabled="checkedInToday">
            {{ checkedInToday ? '今日已打卡 ✓' : '📅 今日打卡' }}
          </button>
        </div>
        <div class="checkin-calendar">
          <div class="day-cell" v-for="day in calendarDays" :key="day.date"
               :class="{ checked: day.checked, today: day.isToday, future: day.isFuture }">
            <span class="day-num">{{ day.dayNum }}</span>
            <span class="day-check">{{ day.checked ? '✓' : '' }}</span>
          </div>
        </div>
      </div>

      <!-- Quick Links -->
      <div class="quick-links">
        <h3>快速开始</h3>
        <div class="links-grid">
          <router-link to="/vocabulary" class="quick-link-card card">
            <span class="ql-icon">📝</span>
            <span>学习新词汇</span>
          </router-link>
          <router-link to="/grammar" class="quick-link-card card">
            <span class="ql-icon">📖</span>
            <span>语法课程</span>
          </router-link>
          <router-link to="/quiz" class="quick-link-card card">
            <span class="ql-icon">✅</span>
            <span>做个测验</span>
          </router-link>
          <router-link to="/reading" class="quick-link-card card">
            <span class="ql-icon">📰</span>
            <span>阅读文章</span>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useLearningStore } from '@/store/learning'

const learningStore = useLearningStore()
const stats = computed(() => learningStore.stats)
const checkedInToday = ref(false)

// Generate calendar for current month
const calendarDays = computed(() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = now.getMonth()
  const today = now.getDate()

  const firstDay = new Date(year, month, 1)
  const lastDay = new Date(year, month + 1, 0)
  const daysInMonth = lastDay.getDate()
  const startOffset = firstDay.getDay()

  const days = []
  // Previous month padding
  for (let i = 0; i < startOffset; i++) {
    days.push({ dayNum: '', date: '', checked: false, isToday: false, isFuture: false })
  }
  // Current month
  for (let d = 1; d <= daysInMonth; d++) {
    const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    days.push({
      dayNum: d,
      date: dateStr,
      checked: learningStore.checkinDays.includes(dateStr),
      isToday: d === today,
      isFuture: d > today
    })
  }
  return days
})

async function handleCheckin() {
  try {
    await learningStore.doCheckin()
    checkedInToday.value = true
    await learningStore.fetchStats()
  } catch (e) {
    // fallback
  }
}

onMounted(async () => {
  await learningStore.fetchStats()
  // Check if already checked in today
  const today = new Date().toISOString().split('T')[0]
  checkedInToday.value = learningStore.checkinDays.includes(today)
})
</script>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 32px;
}
.stat-card {
  display: flex; align-items: center; gap: 16px;
}
.stat-icon { font-size: 2.25rem; }
.stat-num { font-size: 1.75rem; font-weight: 800; color: var(--gray-900); display: block; }
.stat-label { font-size: 0.813rem; color: var(--gray-500); }

.checkin-section { margin-bottom: 32px; }
.checkin-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;
}
.checkin-header h3 { margin-bottom: 4px; }
.checkin-header p { color: var(--gray-500); font-size: 0.875rem; }

.checkin-calendar {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}
.day-cell {
  aspect-ratio: 1;
  border-radius: var(--radius);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: var(--gray-50);
  font-size: 0.813rem;
  transition: all var(--transition-fast);
}
.day-cell.checked {
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  color: white;
}
.day-cell.today {
  border: 2px solid var(--primary);
}
.day-cell.future {
  opacity: 0.4;
}
.day-cell:empty { background: transparent; }
.day-num { font-weight: 600; }
.day-check { font-size: 0.75rem; }

.quick-links { }
.quick-links h3 { margin-bottom: 16px; }
.links-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.quick-link-card {
  text-align: center; padding: 32px 16px;
  display: flex; flex-direction: column; align-items: center; gap: 12px;
}
.ql-icon { font-size: 2rem; }
.quick-link-card span:last-child { font-weight: 600; }

@media (max-width: 768px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .links-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>

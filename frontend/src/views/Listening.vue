<template>
  <div class="page">
    <div class="container">
      <div class="page-header">
        <h1>🎧 听力训练</h1>
        <p>多场景听力材料，提升听力理解能力</p>
      </div>

      <div class="loading-screen" v-if="loading"><div class="spinner"></div></div>

      <div class="listening-grid" v-else>
        <div class="listening-card card" v-for="item in materials" :key="item.id">
          <div class="listening-header">
            <span class="category-badge badge badge-primary">{{ item.category || '通用' }}</span>
            <span class="duration" v-if="item.durationSeconds">
              {{ formatDuration(item.durationSeconds) }}
            </span>
          </div>
          <h3>{{ item.title }}</h3>
          <p class="listening-desc">{{ item.description }}</p>

          <!-- Audio Player -->
          <div class="audio-player">
            <audio controls :src="item.audioUrl" class="audio-element">
              您的浏览器不支持音频播放
            </audio>
          </div>

          <!-- Transcript (collapsible) -->
          <div v-if="item.transcript" class="transcript">
            <button class="btn btn-sm btn-secondary" @click="toggleTranscript(item.id)">
              📝 {{ expandedId === item.id ? '收起原文' : '显示原文' }}
            </button>
            <div v-if="expandedId === item.id" class="transcript-content">
              <p>{{ item.transcript }}</p>
            </div>
          </div>

          <div class="listening-footer">
            <span class="badge" :class="levelBadge(item.difficultyLevel)">
              {{ levelLabel(item.difficultyLevel) }}
            </span>
          </div>
        </div>
      </div>

      <div v-if="!loading && materials.length === 0" class="empty-state">
        <h3>暂无听力材料</h3>
        <p>请稍后再来查看</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api'

const materials = ref([])
const loading = ref(true)
const expandedId = ref(null)

function levelBadge(lvl) {
  return lvl <= 2 ? 'badge-primary' : lvl <= 3 ? 'badge-warning' : 'badge-error'
}
function levelLabel(lvl) {
  return lvl <= 2 ? '初级' : lvl <= 3 ? '中级' : '高级'
}
function formatDuration(seconds) {
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m}:${String(s).padStart(2, '0')}`
}
function toggleTranscript(id) {
  expandedId.value = expandedId.value === id ? null : id
}

onMounted(async () => {
  try {
    const res = await api.get('/listening')
    materials.value = res.data.data || []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.listening-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 20px;
}
.listening-card { }
.listening-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;
}
.duration { font-size: 0.813rem; color: var(--gray-400); font-weight: 600; }
.listening-card h3 { margin-bottom: 8px; }
.listening-desc { color: var(--gray-500); font-size: 0.875rem; margin-bottom: 16px; }

.audio-player { margin-bottom: 12px; }
.audio-element { width: 100%; height: 40px; border-radius: var(--radius-sm); }

.transcript { margin-bottom: 12px; }
.transcript-content {
  background: var(--gray-50); padding: 16px; border-radius: var(--radius);
  margin-top: 8px; font-size: 0.875rem; color: var(--gray-600); line-height: 1.7;
  white-space: pre-line;
}
.listening-footer { display: flex; justify-content: flex-end; }

@media (max-width: 768px) {
  .listening-grid { grid-template-columns: 1fr; }
}
</style>

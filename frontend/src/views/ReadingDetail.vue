<template>
  <div class="page">
    <div class="container">
      <button class="btn btn-secondary btn-sm back-btn" @click="$router.push('/reading')">← 返回文章列表</button>
      <div class="loading-screen" v-if="loading"><div class="spinner"></div></div>

      <!-- Reader Control Bar -->
      <div v-if="article" class="reader-bar card">
        <div class="reader-info">
          <span>🔊 朗读文章</span>
          <div class="accent-selector">
            <button class="btn btn-sm" :class="accent === 'us' ? 'btn-primary' : 'btn-secondary'"
                    @click="setAccent('us')">
              🇺🇸 美式口音
            </button>
            <button class="btn btn-sm" :class="accent === 'uk' ? 'btn-primary' : 'btn-secondary'"
                    @click="setAccent('uk')">
              🇬🇧 英式口音
            </button>
          </div>
        </div>
        <div class="reader-controls">
          <button class="btn btn-primary btn-sm" @click="startReading" :disabled="isReading && !isPaused">
            ▶ {{ isPaused ? '继续' : '开始朗读' }}
          </button>
          <button class="btn btn-secondary btn-sm" @click="pauseReading" :disabled="!isReading || isPaused">
            ⏸ 暂停
          </button>
          <button class="btn btn-secondary btn-sm" @click="stopReading" :disabled="!isReading">
            ⏹ 停止
          </button>
          <span v-if="isReading" class="reading-progress">
            {{ currentSentence + 1 }} / {{ totalSentences }}
          </span>
        </div>
        <div class="reader-speed">
          <label>语速：</label>
          <input type="range" min="0.5" max="1.5" step="0.1" v-model="speed" class="speed-slider" />
          <span class="speed-value">{{ speed }}x</span>
        </div>
      </div>

      <article v-if="article" class="reading-article card">
        <header class="article-header">
          <div class="article-meta-top">
            <span class="badge badge-primary">{{ article.category }}</span>
            <span class="badge" :class="levelBadge(article.difficultyLevel)">
              {{ levelLabel(article.difficultyLevel) }}
            </span>
          </div>
          <h1>{{ article.title }}</h1>
          <p class="byline" v-if="article.author">
            {{ article.author }} · {{ article.source }} · {{ article.wordCount }} 词
          </p>
          <div class="summary-box" v-if="article.summary">
            <strong>📋 摘要：</strong>{{ article.summary }}
          </div>
        </header>
        <div class="article-body" ref="articleBody" v-html="article.contentHtml"></div>
      </article>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { articleAPI } from '@/api'
import { speak, ArticleReader } from '@/utils/speech'

const route = useRoute()
const article = ref(null)
const loading = ref(true)
const articleBody = ref(null)

// Reader state
const accent = ref('us')
const speed = ref(0.9)
const isReading = ref(false)
const isPaused = ref(false)
const currentSentence = ref(0)
const totalSentences = ref(0)
let reader = null

function levelBadge(lvl) {
  return lvl <= 2 ? 'badge-primary' : lvl <= 3 ? 'badge-warning' : 'badge-error'
}
function levelLabel(lvl) {
  return lvl <= 2 ? '初级' : lvl <= 3 ? '中级' : '高级'
}

function setAccent(a) {
  accent.value = a
  if (reader) reader.setAccent(a)
}

function startReading() {
  if (!article.value) return

  if (isPaused.value && reader) {
    reader.resume()
    isPaused.value = false
    isReading.value = true
    return
  }

  window.speechSynthesis?.cancel()

  const text = article.value.contentHtml || ''
  reader = new ArticleReader(text)
  reader.setAccent(accent.value)
  totalSentences.value = reader.sentences.length

  reader.onProgress((idx, total) => {
    currentSentence.value = idx
    totalSentences.value = total
  })

  reader.onEnd(() => {
    isReading.value = false
    isPaused.value = false
    currentSentence.value = 0
  })

  isReading.value = true
  isPaused.value = false
  reader.speak()
}

function pauseReading() {
  if (reader) {
    reader.pause()
    isPaused.value = true
    isReading.value = false
  }
}

function stopReading() {
  if (reader) {
    reader.stop()
  }
  isReading.value = false
  isPaused.value = false
  currentSentence.value = 0
  window.speechSynthesis?.cancel()
}

onMounted(async () => {
  try {
    const res = await articleAPI.getArticle(route.params.id)
    article.value = res.data.data
  } finally { loading.value = false }
})

onUnmounted(() => {
  stopReading()
})
</script>

<style scoped>
.back-btn { margin-bottom: 24px; }

/* Reader Bar */
.reader-bar {
  max-width: 800px;
  margin: 0 auto 20px;
  padding: 20px 24px;
  background: linear-gradient(135deg, #f8faff, #f0f9ff);
  border: 1.5px solid var(--primary-bg);
}
.reader-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-weight: 600;
  color: var(--gray-700);
}
.accent-selector {
  display: flex;
  gap: 6px;
}
.reader-controls {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}
.reading-progress {
  margin-left: auto;
  font-size: 0.875rem;
  color: var(--primary);
  font-weight: 600;
}
.reader-speed {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.813rem;
  color: var(--gray-500);
}
.speed-slider {
  width: 100px;
  accent-color: var(--primary);
}
.speed-value {
  font-weight: 600;
  color: var(--gray-700);
  min-width: 32px;
}

.reading-article { max-width: 800px; margin: 0 auto; padding: 40px; }
.article-header { margin-bottom: 32px; padding-bottom: 24px; border-bottom: 1px solid var(--gray-200); }
.article-meta-top { display: flex; gap: 8px; margin-bottom: 16px; }
.article-header h1 { margin-bottom: 8px; line-height: 1.3; }
.byline { color: var(--gray-400); font-size: 0.875rem; }
.summary-box {
  background: var(--primary-bg); padding: 16px 20px; border-radius: var(--radius);
  margin-top: 20px; font-size: 0.938rem; color: var(--gray-600); line-height: 1.6;
}

.article-body :deep(p) { margin-bottom: 16px; line-height: 1.9; font-size: 1.063rem; color: var(--gray-700); }
.article-body :deep(em) { color: var(--primary); }

@media (max-width: 768px) {
  .reading-article { padding: 24px; }
  .reader-info { flex-direction: column; gap: 10px; align-items: flex-start; }
  .reader-controls { flex-wrap: wrap; }
}
</style>

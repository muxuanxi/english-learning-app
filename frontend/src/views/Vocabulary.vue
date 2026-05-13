<template>
  <div class="page">
    <div class="container">
      <div class="page-header">
        <h1>📝 词汇学习</h1>
        <p>科学记忆，艾宾浩斯复习曲线助你高效记单词</p>
      </div>

      <!-- Filters -->
      <div class="filters">
        <div class="filter-group">
          <button v-for="lvl in levels" :key="lvl.value" class="btn btn-sm"
                  :class="selectedLevel === lvl.value ? 'btn-primary' : 'btn-secondary'"
                  @click="selectedLevel = lvl.value; loadWords()">
            {{ lvl.label }}
          </button>
        </div>
        <div class="search-box">
          <input v-model="searchQuery" type="text" class="form-input"
                 placeholder="搜索单词..." @keyup.enter="searchWords" />
          <button class="btn btn-primary btn-sm" @click="searchWords">搜索</button>
        </div>
      </div>

      <!-- Flashcard Mode -->
      <div v-if="flashcardMode && currentWord" class="flashcard-overlay" @click.self="flashcardMode = false">
        <div class="flashcard-container">
          <div class="flashcard" :class="{ flipped }" @click="flipped = !flipped">
            <div class="flashcard-front">
              <h2>{{ currentWord.word }}</h2>
              <p class="phonetic-us">🇺🇸 {{ currentWord.phoneticUs }}</p>
              <p class="phonetic-uk" v-if="currentWord.phoneticUk">🇬🇧 {{ currentWord.phoneticUk }}</p>
              <span class="badge badge-primary">{{ currentWord.partOfSpeech }}</span>
              <div class="flashcard-pronounce">
                <button class="pronounce-btn us"
                        :class="{ playing: speakingAccent === 'us' }"
                        @click.stop="speakWord(currentWord, 'us')"
                        :disabled="speakingAccent === 'us'"
                        title="美式发音">
                  🇺🇸 美式发音
                  <span v-if="speakingAccent === 'us'" class="speaking-dot"></span>
                </button>
                <button class="pronounce-btn uk"
                        :class="{ playing: speakingAccent === 'uk' }"
                        @click.stop="speakWord(currentWord, 'uk')"
                        :disabled="speakingAccent === 'uk'"
                        title="英式发音">
                  🇬🇧 英式发音
                  <span v-if="speakingAccent === 'uk'" class="speaking-dot"></span>
                </button>
              </div>
              <p class="hint">点击翻转查看释义</p>
            </div>
            <div class="flashcard-back">
              <p class="definition-cn">{{ currentWord.definitionCn }}</p>
              <p class="definition-en" v-if="currentWord.definitionEn">{{ currentWord.definitionEn }}</p>
              <p class="example" v-if="currentWord.exampleSentence">
                <em>"{{ currentWord.exampleSentence }}"</em>
              </p>
              <p class="example-cn" v-if="currentWord.exampleTranslation">{{ currentWord.exampleTranslation }}</p>
            </div>
          </div>
          <div class="flashcard-actions">
            <button class="btn btn-error btn-sm" @click="markWord(false); nextWord()">还不会</button>
            <button class="btn btn-secondary btn-sm" @click="nextWord()">跳过</button>
            <button class="btn btn-success btn-sm" @click="markWord(true); nextWord()">已掌握 ✓</button>
          </div>
          <div class="flashcard-progress">
            第 {{ currentIndex + 1 }} / {{ words.length }} 个
          </div>
        </div>
      </div>

      <!-- Word List -->
      <div v-if="!flashcardMode" class="word-grid">
        <div class="word-card card" v-for="word in words" :key="word.id">
          <div class="word-card-inner" @click="openFlashcard(word)">
            <div class="word-main">
              <h4>{{ word.word }}</h4>
              <span class="phonetic-us">🇺🇸 {{ word.phoneticUs }}</span>
              <span class="phonetic-uk" v-if="word.phoneticUk">🇬🇧 {{ word.phoneticUk }}</span>
            </div>
            <span class="badge badge-primary">{{ word.partOfSpeech || 'n.' }}</span>
            <p class="def">{{ word.definitionCn }}</p>
            <div class="word-footer">
              <span class="level-label">难度: {'★'.repeat(word.difficultyLevel)}</span>
              <span class="category" v-if="word.category">{{ word.category }}</span>
            </div>
          </div>
          <div class="word-pronounce" @click.stop>
            <button class="pronounce-btn us"
                    :class="{ playing: speakingWordId === word.id && speakingAccent === 'us' }"
                    @click="speakWord(word, 'us')"
                    :disabled="speakingWordId === word.id && speakingAccent === 'us'"
                    title="美式发音">
              🇺🇸 <span class="pronounce-label">US</span>
              <span v-if="speakingWordId === word.id && speakingAccent === 'us'" class="speaking-dot"></span>
            </button>
            <button class="pronounce-btn uk"
                    :class="{ playing: speakingWordId === word.id && speakingAccent === 'uk' }"
                    @click="speakWord(word, 'uk')"
                    :disabled="speakingWordId === word.id && speakingAccent === 'uk'"
                    title="英式发音">
              🇬🇧 <span class="pronounce-label">UK</span>
              <span v-if="speakingWordId === word.id && speakingAccent === 'uk'" class="speaking-dot"></span>
            </button>
          </div>
        </div>
      </div>

      <div v-if="!flashcardMode && words.length > 0" class="load-more">
        <button class="btn btn-outline" @click="loadWords">加载更多</button>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-screen">
        <div class="spinner"></div>
      </div>

      <!-- Flashcard Prompt -->
      <div v-if="!flashcardMode && words.length > 0" class="flashcard-prompt">
        <button class="btn btn-accent btn-lg" @click="startFlashcards">
          🎴 开始闪卡模式
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { wordAPI } from '@/api'
import { speak } from '@/utils/speech'

const levels = [
  { value: null, label: '全部' },
  { value: 1, label: '★ 初级' },
  { value: 2, label: '★★ 初中级' },
  { value: 3, label: '★★★ 中级' },
  { value: 4, label: '★★★★ 中高级' },
  { value: 5, label: '★★★★★ 高级' },
]

const words = ref([])
const loading = ref(false)
const selectedLevel = ref(null)
const searchQuery = ref('')

// Pronunciation state
const speakingWordId = ref(null)
const speakingAccent = ref(null)

// Flashcard mode
const flashcardMode = ref(false)
const currentIndex = ref(0)
const flipped = ref(false)

const currentWord = computed(() => words.value[currentIndex.value] || null)

async function speakWord(word, accent) {
  const lang = accent === 'uk' ? 'en-GB' : 'en-US'
  const wordId = word.id
  speakingWordId.value = wordId
  speakingAccent.value = accent
  try {
    await speak(word.word, lang, {})
  } catch (e) {
    // silently fail
  } finally {
    if (speakingWordId.value === wordId) {
      speakingWordId.value = null
      speakingAccent.value = null
    }
  }
}

async function loadWords() {
  loading.value = true
  try {
    const params = { limit: 200 }
    if (selectedLevel.value) params.level = selectedLevel.value
    const res = await wordAPI.getWords(params)
    words.value = res.data.data || []
    currentIndex.value = 0
  } finally {
    loading.value = false
  }
}

async function searchWords() {
  if (!searchQuery.value.trim()) return loadWords()
  loading.value = true
  try {
    const res = await wordAPI.searchWords(searchQuery.value.trim())
    words.value = res.data.data || []
  } finally {
    loading.value = false
  }
}

function startFlashcards() {
  currentIndex.value = 0
  flipped.value = false
  flashcardMode.value = true
}

function openFlashcard(word) {
  currentIndex.value = words.value.indexOf(word)
  flipped.value = false
  flashcardMode.value = true
}

function nextWord() {
  flipped.value = false
  speakingAccent.value = null
  if (currentIndex.value < words.value.length - 1) {
    currentIndex.value++
  } else {
    flashcardMode.value = false
  }
}

function markWord(mastered) {
  if (currentWord.value) {
    wordAPI.recordStudy(currentWord.value.id, mastered).catch(() => {})
  }
}

onMounted(loadWords)
</script>

<style scoped>
.filters {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 32px;
}
.filter-group { display: flex; gap: 8px; flex-wrap: wrap; }
.search-box { display: flex; gap: 8px; }
.search-box .form-input { width: 240px; }

.word-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}
.word-card {
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.word-card-inner {
  cursor: pointer;
  flex: 1;
}
.word-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: var(--primary);
  border-radius: 4px 0 0 4px;
}
.word-main {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 8px;
}
.word-main h4 { font-size: 1.25rem; color: var(--primary-dark); }
.phonetic-us, .phonetic-uk { font-size: 0.813rem; color: var(--gray-500); display: block; }
.phonetic-uk { margin-top: 2px; opacity: 0.85; }
.def { color: var(--gray-600); font-size: 0.938rem; margin: 8px 0; }
.word-footer {
  display: flex;
  justify-content: space-between;
  font-size: 0.75rem;
  color: var(--gray-400);
  margin-top: 12px;
}

/* Pronunciation Buttons */
.word-pronounce {
  display: flex;
  gap: 6px;
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid var(--gray-100);
}
.pronounce-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border: 1.5px solid var(--gray-200);
  border-radius: 20px;
  background: white;
  cursor: pointer;
  font-size: 0.75rem;
  font-weight: 600;
  transition: all 0.2s ease;
  font-family: var(--font-mixed);
}
.pronounce-btn:hover {
  border-color: var(--primary);
  background: var(--primary-bg);
}
.pronounce-btn.playing {
  border-color: var(--primary);
  background: var(--primary);
  color: white;
}
.pronounce-btn:disabled {
  cursor: default;
  opacity: 0.8;
}
.pronounce-label {
  font-size: 0.688rem;
  opacity: 0.7;
}
.speaking-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
  animation: pulse 0.8s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.4; transform: scale(1.5); }
}

/* Flashcard */
.flashcard-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.6);
  backdrop-filter: blur(8px);
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
}
.flashcard-container {
  text-align: center;
}
.flashcard {
  width: 420px;
  max-width: 90vw;
  height: 340px;
  perspective: 1000px;
  cursor: pointer;
  position: relative;
}
.flashcard-front, .flashcard-back {
  position: absolute;
  inset: 0;
  background: white;
  border-radius: var(--radius-xl);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px;
  backface-visibility: hidden;
  transition: transform 0.6s ease;
  box-shadow: var(--shadow-xl);
}
.flashcard-back {
  transform: rotateY(180deg);
}
.flashcard.flipped .flashcard-front {
  transform: rotateY(180deg);
}
.flashcard.flipped .flashcard-back {
  transform: rotateY(0deg);
}
.flashcard-front h2 { font-size: 2rem; color: var(--primary); }
.flashcard-front .hint { color: var(--gray-400); font-size: 0.813rem; margin-top: 16px; }
.flashcard-pronounce {
  display: flex;
  gap: 10px;
  margin: 14px 0 10px;
}
.flashcard-pronounce .pronounce-btn {
  padding: 8px 18px;
  font-size: 0.875rem;
  border-radius: 22px;
}
.flashcard-back .definition-cn { font-size: 1.5rem; font-weight: 700; color: var(--gray-900); }
.flashcard-back .definition-en { color: var(--gray-500); margin: 8px 0; }
.flashcard-back .example { color: var(--gray-600); font-size: 0.938rem; margin-top: 12px; }
.flashcard-actions {
  display: flex; gap: 12px; justify-content: center; margin-top: 24px;
}
.flashcard-progress { color: white; margin-top: 16px; font-size: 0.875rem; }
.flashcard-prompt { text-align: center; margin-top: 40px; }
.load-more { text-align: center; margin-top: 32px; }

@media (max-width: 768px) {
  .filters { flex-direction: column; align-items: stretch; }
  .search-box { width: 100%; }
  .search-box .form-input { flex: 1; }
  .word-grid { grid-template-columns: 1fr; }
  .flashcard { height: 280px; }
}
</style>

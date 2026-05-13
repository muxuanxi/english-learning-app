<template>
  <div class="page">
    <div class="container">
      <div class="page-header">
        <h1>🌐 翻译工具</h1>
        <p>英语 ⇄ 中文实时翻译</p>
      </div>

      <div class="translate-container">
        <!-- Source -->
        <div class="translate-panel">
          <div class="panel-header">
            <span class="lang-badge">English</span>
            <button class="btn btn-sm btn-secondary" @click="clearSource">清除</button>
          </div>
          <textarea v-model="sourceText" class="translate-textarea" rows="8"
                    placeholder="输入要翻译的英文文本..."></textarea>
          <div class="panel-footer">
            <span class="char-count">{{ sourceText.length }} 字符</span>
            <button class="btn btn-primary" @click="doTranslate" :disabled="!sourceText.trim() || translating">
              <span v-if="translating">翻译中...</span>
              <span v-else>翻译 →</span>
            </button>
          </div>
        </div>

        <!-- Swap button -->
        <div class="swap-row">
          <button class="swap-btn" @click="swapLanguages" title="切换语言方向">
            ⇄
          </button>
        </div>

        <!-- Target -->
        <div class="translate-panel">
          <div class="panel-header">
            <span class="lang-badge">中文</span>
            <button class="btn btn-sm btn-secondary" @click="copyResult" :disabled="!translatedText">
              📋 复制
            </button>
          </div>
          <textarea v-model="translatedText" class="translate-textarea result" rows="8"
                    placeholder="翻译结果..." readonly></textarea>
          <div class="panel-footer">
            <span class="char-count">{{ translatedText.length }} 字符</span>
            <button class="btn btn-sm btn-outline" @click="speakResult" :disabled="!translatedText || speaking">
              🔊 朗读原文
            </button>
          </div>
        </div>
      </div>

      <!-- Quick Translate Cards -->
      <div class="quick-section">
        <h3>📌 快速翻译常用短语</h3>
        <div class="quick-phrases">
          <div v-for="p in quickPhrases" :key="p.en" class="phrase-card card"
               @click="sourceText = p.en; doTranslate()">
            <p class="phrase-en">{{ p.en }}</p>
            <p class="phrase-zh">{{ p.zh }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { translateText } from '@/utils/translate'
import { speak } from '@/utils/speech'

const sourceText = ref('')
const translatedText = ref('')
const translating = ref(false)
const speaking = ref(false)
const direction = ref('en|zh') // en|zh or zh|en

const quickPhrases = [
  { en: 'How are you doing today?', zh: '你今天过得怎么样？' },
  { en: 'I would like to improve my English speaking skills.', zh: '我想提高我的英语口语能力。' },
  { en: 'Could you please recommend a good restaurant nearby?', zh: '你能推荐附近一家好的餐厅吗？' },
  { en: 'The meeting has been postponed to next Monday.', zh: '会议已推迟到下周一。' },
  { en: 'I am looking forward to hearing from you soon.', zh: '我期待尽快收到你的回复。' },
]

function clearSource() {
  sourceText.value = ''
  translatedText.value = ''
}

function swapLanguages() {
  direction.value = direction.value === 'en|zh' ? 'zh|en' : 'en|zh'
  const temp = sourceText.value
  sourceText.value = translatedText.value
  translatedText.value = temp
  const badges = document.querySelectorAll('.lang-badge')
  if (badges.length >= 2) {
    const tmp = badges[0].textContent
    badges[0].textContent = badges[1].textContent
    badges[1].textContent = tmp
  }
}

async function doTranslate() {
  if (!sourceText.value.trim()) return
  translating.value = true
  try {
    const [from, to] = direction.value.split('|')
    const result = await translateText(sourceText.value.trim(), from, to)
    translatedText.value = result
  } finally {
    translating.value = false
  }
}

function copyResult() {
  if (translatedText.value) {
    navigator.clipboard?.writeText(translatedText.value)
  }
}

async function speakResult() {
  speaking.value = true
  try {
    await speak(sourceText.value, 'en-US', {})
  } finally {
    speaking.value = false
  }
}
</script>

<style scoped>
.translate-container {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 16px;
  align-items: start;
  max-width: 900px;
  margin: 0 auto 60px;
}
.translate-panel {
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow);
  overflow: hidden;
}
.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: var(--gray-50);
  border-bottom: 1px solid var(--gray-100);
}
.lang-badge {
  font-weight: 700;
  font-size: 0.938rem;
  color: var(--gray-700);
}
.translate-textarea {
  width: 100%;
  border: none;
  outline: none;
  padding: 16px;
  font-size: 1rem;
  font-family: var(--font-mixed);
  resize: vertical;
  line-height: 1.7;
  color: var(--gray-800);
}
.translate-textarea.result {
  background: var(--gray-50);
  color: var(--gray-700);
}
.translate-textarea::placeholder {
  color: var(--gray-400);
}
.panel-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
  border-top: 1px solid var(--gray-100);
}
.char-count {
  font-size: 0.75rem;
  color: var(--gray-400);
}

.swap-row {
  display: flex;
  align-items: center;
  padding-top: 40px;
}
.swap-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid var(--gray-200);
  background: white;
  font-size: 1.25rem;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}
.swap-btn:hover {
  border-color: var(--primary);
  background: var(--primary-bg);
}

.quick-section {
  max-width: 900px;
  margin: 0 auto;
}
.quick-section h3 {
  margin-bottom: 16px;
}
.quick-phrases {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 12px;
}
.phrase-card {
  cursor: pointer;
  padding: 20px;
  transition: all var(--transition);
}
.phrase-card:hover {
  border-color: var(--primary);
  transform: translateY(-2px);
}
.phrase-en {
  font-size: 0.938rem;
  color: var(--gray-700);
  margin-bottom: 8px;
  line-height: 1.5;
}
.phrase-zh {
  font-size: 0.813rem;
  color: var(--primary);
  font-weight: 500;
}

@media (max-width: 768px) {
  .translate-container {
    grid-template-columns: 1fr;
  }
  .swap-row {
    justify-content: center;
    padding-top: 0;
  }
  .swap-btn {
    transform: rotate(90deg);
  }
}
</style>

<template>
  <div class="page">
    <div class="container">
      <div class="page-header">
        <h1>🔍 全局搜索</h1>
        <p>搜索词汇、文章、语法课程</p>
      </div>

      <!-- Search Box -->
      <div class="search-hero">
        <div class="search-input-group">
          <input v-model="query" type="text" class="form-input search-input-lg"
                 placeholder="输入关键词搜索..." @keyup.enter="doSearch" autofocus />
          <button class="btn btn-primary btn-lg" @click="doSearch" :disabled="loading">
            <span v-if="loading">搜索中...</span>
            <span v-else>🔍 搜索</span>
          </button>
        </div>
        <!-- Suggestions -->
        <div v-if="suggestions.length > 0 && !searched" class="suggestions">
          <span v-for="s in suggestions" :key="s" class="suggestion-chip"
                @click="query = s; doSearch()">{{ s }}</span>
        </div>
      </div>

      <div class="loading-screen" v-if="loading"><div class="spinner"></div></div>

      <!-- Results -->
      <div v-if="searched && !loading" class="search-results">
        <div class="result-summary" v-if="results">
          找到 <strong>{{ results?.total || 0 }}</strong> 个结果
        </div>

        <!-- Words -->
        <section v-if="results?.words?.length" class="result-section">
          <h2 class="section-label">📝 词汇 ({{ results.words.length }})</h2>
          <div class="word-results">
            <div class="search-word-card card" v-for="word in results.words" :key="'w'+word.id"
                 @click="$router.push('/vocabulary')">
              <div class="word-main">
                <h4>{{ word.word }}</h4>
                <span class="phonetic-us">🇺🇸 {{ word.phoneticUs }}</span>
                <span class="phonetic-uk" v-if="word.phoneticUk">🇬🇧 {{ word.phoneticUk }}</span>
              </div>
              <span class="badge badge-primary">{{ word.partOfSpeech }}</span>
              <p class="def">{{ word.definitionCn }}</p>
              <div class="pronounce-row" @click.stop>
                <button class="mini-btn" @click="speakWord(word, 'us')" title="美式发音">🇺🇸 读</button>
                <button class="mini-btn" @click="speakWord(word, 'uk')" title="英式发音">🇬🇧 读</button>
              </div>
            </div>
          </div>
        </section>

        <!-- Articles -->
        <section v-if="results?.articles?.length" class="result-section">
          <h2 class="section-label">📰 文章 ({{ results.articles.length }})</h2>
          <div class="article-results">
            <div class="search-article-card card" v-for="a in results.articles" :key="'a'+a.id"
                 @click="$router.push(`/reading/${a.id}`)">
              <h3>{{ a.title }}</h3>
              <p class="meta">{{ a.author }} · {{ a.source }} · {{ a.wordCount }}词</p>
              <p class="summary">{{ a.summary }}</p>
            </div>
          </div>
        </section>

        <!-- Grammar -->
        <section v-if="results?.grammar?.length" class="result-section">
          <h2 class="section-label">📖 语法课程 ({{ results.grammar.length }})</h2>
          <div class="grammar-results">
            <div class="search-grammar-card card" v-for="g in results.grammar" :key="'g'+g.id"
                 @click="$router.push(`/grammar/${g.id}`)">
              <h3>{{ g.title }}</h3>
              <span class="badge badge-primary">{{ g.category }}</span>
              <p>{{ g.description }}</p>
            </div>
          </div>
        </section>

        <!-- No results -->
        <div v-if="results && results.total === 0" class="empty-state">
          <h3>😕 没有找到相关结果</h3>
          <p>试试其他关键词，或浏览其他学习内容</p>
          <div class="empty-actions">
            <router-link to="/vocabulary" class="btn btn-primary">浏览词汇</router-link>
            <router-link to="/articles" class="btn btn-outline">浏览文章</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { speak } from '@/utils/speech'
import api from '@/api'

const query = ref('')
const results = ref(null)
const suggestions = ref([])
const loading = ref(false)
const searched = ref(false)

async function doSearch() {
  const q = query.value.trim()
  if (!q) return

  loading.value = true
  searched.value = true
  try {
    const res = await api.get('/search', { params: { q } })
    results.value = res.data.data
  } finally {
    loading.value = false
  }
}

async function loadSuggestions() {
  try {
    const res = await api.get('/search/suggest', { params: { q: '' } })
    suggestions.value = res.data.data || []
  } catch (e) { /* ignore */ }
}

async function speakWord(word, accent) {
  const lang = accent === 'uk' ? 'en-GB' : 'en-US'
  await speak(word.word, lang, {})
}

onMounted(loadSuggestions)
</script>

<style scoped>
.search-hero {
  max-width: 640px;
  margin: 0 auto 40px;
}
.search-input-group {
  display: flex;
  gap: 12px;
}
.search-input-lg {
  flex: 1;
  font-size: 1.125rem;
  padding: 14px 20px;
}
.suggestions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 12px;
}
.suggestion-chip {
  padding: 6px 14px;
  background: var(--gray-100);
  border-radius: 100px;
  font-size: 0.813rem;
  color: var(--gray-600);
  cursor: pointer;
  transition: all 0.2s;
}
.suggestion-chip:hover {
  background: var(--primary-bg);
  color: var(--primary);
}

.search-results {
  max-width: 800px;
  margin: 0 auto;
}
.result-summary {
  text-align: center;
  margin-bottom: 32px;
  color: var(--gray-500);
  font-size: 0.938rem;
}
.result-section {
  margin-bottom: 40px;
}
.section-label {
  font-size: 1.125rem;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid var(--gray-100);
}

.word-results {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 12px;
}
.search-word-card {
  cursor: pointer;
  padding: 20px;
}
.search-word-card h4 {
  font-size: 1.2rem;
  color: var(--primary-dark);
  margin-bottom: 2px;
}
.phonetic-us, .phonetic-uk {
  font-size: 0.75rem;
  color: var(--gray-400);
  display: block;
}
.def {
  font-size: 0.875rem;
  color: var(--gray-600);
  margin: 8px 0;
}
.pronounce-row {
  display: flex;
  gap: 6px;
  margin-top: 10px;
}
.mini-btn {
  padding: 3px 10px;
  border: 1px solid var(--gray-200);
  border-radius: 100px;
  background: white;
  cursor: pointer;
  font-size: 0.7rem;
  transition: all 0.2s;
}
.mini-btn:hover {
  border-color: var(--primary);
  background: var(--primary-bg);
}
.search-article-card, .search-grammar-card {
  cursor: pointer;
  margin-bottom: 12px;
}
.search-article-card h3, .search-grammar-card h3 {
  margin-bottom: 4px;
}
.meta {
  font-size: 0.813rem;
  color: var(--gray-400);
  margin-bottom: 8px;
}
.summary {
  font-size: 0.875rem;
  color: var(--gray-500);
}

.empty-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 20px;
}
</style>

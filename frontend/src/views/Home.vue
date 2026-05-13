<template>
  <div class="home">
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-bg"></div>
      <div class="container hero-content">
        <h1 class="animate-in">
          掌握英语，<br>
          <span class="gradient-text">开启无限可能</span>
        </h1>
        <p class="hero-subtitle animate-in" style="animation-delay: 0.15s">
          科学的学习路径 · 丰富的学习资源 · 智能的进度追踪<br>
          为成年人量身定制的英语学习平台
        </p>
        <div class="hero-actions animate-in" style="animation-delay: 0.3s">
          <router-link to="/register" class="btn btn-primary btn-lg">🎯 免费开始学习</router-link>
          <router-link to="/quiz" class="btn btn-outline btn-lg">📋 测试英语水平</router-link>
        </div>
        <div class="hero-stats animate-in" style="animation-delay: 0.45s">
          <div class="stat-item">
            <span class="stat-number">5000+</span>
            <span class="stat-label">核心词汇</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">200+</span>
            <span class="stat-label">语法课程</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">1000+</span>
            <span class="stat-label">阅读文章</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">∞</span>
            <span class="stat-label">学习动力</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Features -->
    <section class="features container">
      <h2 class="section-title">全方位提升你的英语能力</h2>
      <p class="section-subtitle">从词汇积累到流利表达，每一步都扎实</p>
      <div class="grid grid-4">
        <div class="feature-card" v-for="(f, i) in features" :key="i"
             :style="{ animationDelay: (i * 0.1) + 's' }">
          <span class="feature-icon">{{ f.icon }}</span>
          <h3>{{ f.title }}</h3>
          <p>{{ f.desc }}</p>
          <router-link :to="f.link" class="feature-link">开始学习 →</router-link>
        </div>
      </div>
    </section>

    <!-- Learning Path -->
    <section class="path-section">
      <div class="container">
        <h2 class="section-title">你的学习路径</h2>
        <p class="section-subtitle">从初级到高级，循序渐进</p>
        <div class="path-timeline">
          <div class="path-step" v-for="(step, i) in levels" :key="i">
            <div class="step-marker">{{ i + 1 }}</div>
            <div class="step-content">
              <h3>{{ step.name }}</h3>
              <p>{{ step.desc }}</p>
              <span class="badge" :class="step.badgeClass">{{ step.label }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Today's Words -->
    <section class="daily-words container">
      <div class="section-header">
        <div>
          <h2 class="section-title">今日推荐词汇</h2>
          <p class="section-subtitle">每天学习新词汇，积少成多</p>
        </div>
        <router-link to="/vocabulary" class="btn btn-outline">查看全部 →</router-link>
      </div>
      <div class="word-cards">
        <div class="word-card" v-for="word in dailyWords" :key="word.word">
          <div class="word-card-top">
            <div>
              <h4>{{ word.word }}</h4>
              <span class="word-phonetic">🇺🇸 {{ word.phoneticUs }}</span>
              <span class="word-phonetic-uk" v-if="word.phoneticUk">🇬🇧 {{ word.phoneticUk }}</span>
            </div>
            <div class="word-pronounce-mini">
              <button class="mini-pronounce-btn"
                      :class="{ playing: speakingId === word.id && speakingAcc === 'us' }"
                      @click="speakWord(word, 'us')" title="美式发音">
                🇺🇸
              </button>
              <button class="mini-pronounce-btn"
                      :class="{ playing: speakingId === word.id && speakingAcc === 'uk' }"
                      @click="speakWord(word, 'uk')" title="英式发音">
                🇬🇧
              </button>
            </div>
          </div>
          <span class="word-pos badge badge-primary">{{ word.partOfSpeech }}</span>
          <p class="word-def">{{ word.definitionCn }}</p>
          <p class="word-example"><em>"{{ word.exampleSentence }}"</em></p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { wordAPI } from '@/api'
import { speak } from '@/utils/speech'

const features = [
  { icon: '📝', title: '词汇积累', desc: '科学记忆曲线帮助高效记单词', link: '/vocabulary' },
  { icon: '📖', title: '语法精讲', desc: '系统学习英语语法规则', link: '/grammar' },
  { icon: '📰', title: '阅读训练', desc: '原汁原味英文文章提升阅读力', link: '/reading' },
  { icon: '🎧', title: '听力练习', desc: '多种场景听力材料强化听感', link: '/listening' },
  { icon: '✅', title: '在线测验', desc: '检验学习成果查漏补缺', link: '/quiz' },
  { icon: '📊', title: '进度追踪', desc: '可视化学习数据见证成长', link: '/dashboard' },
  { icon: '🔥', title: '每日打卡', desc: '坚持打卡养成学习好习惯', link: '/dashboard' },
  { icon: '🎯', title: '个性推荐', desc: '根据水平智能推荐内容', link: '/vocabulary' },
]

const levels = [
  { name: '初级入门', desc: '掌握基础词汇和简单语法，建立英语思维框架', label: 'Beginner', badgeClass: 'badge-primary' },
  { name: '初级进阶', desc: '扩展常用词汇，学习基本时态和句型结构', label: 'Elementary', badgeClass: 'badge-primary' },
  { name: '中级突破', desc: '深入复杂语法，开始阅读原版文章', label: 'Intermediate', badgeClass: 'badge-warning' },
  { name: '中高级提升', desc: '精进听说读写，能流利进行商务沟通', label: 'Upper-Intermediate', badgeClass: 'badge-warning' },
  { name: '高级精通', desc: '接近母语水平，能处理专业领域英语', label: 'Advanced', badgeClass: 'badge-success' },
]

const dailyWords = ref([])
const speakingId = ref(null)
const speakingAcc = ref(null)

async function speakWord(word, accent) {
  const lang = accent === 'uk' ? 'en-GB' : 'en-US'
  speakingId.value = word.id
  speakingAcc.value = accent
  try {
    await speak(word.word, lang, {})
  } catch (e) { /* ignore */ }
  finally {
    if (speakingId.value === word.id) {
      speakingId.value = null
      speakingAcc.value = null
    }
  }
}

onMounted(async () => {
  try {
    const res = await wordAPI.getWords({ random: true, limit: 4 })
    dailyWords.value = res.data.data || []
  } catch (e) {
    dailyWords.value = []
  }
})
</script>

<style scoped>
/* Hero */
.hero {
  position: relative;
  padding: 120px 0 80px;
  overflow: hidden;
  background: linear-gradient(135deg, #eef2ff 0%, #f0f9ff 50%, #fef3c7 100%);
}
.hero-bg {
  position: absolute;
  top: -50%;
  right: -20%;
  width: 800px;
  height: 800px;
  background: radial-gradient(circle, rgba(99,102,241,0.08) 0%, transparent 70%);
  border-radius: 50%;
}
.hero-content {
  position: relative;
  text-align: center;
  z-index: 1;
}
.hero h1 {
  font-size: 3.25rem;
  line-height: 1.2;
  margin-bottom: 20px;
}
.gradient-text {
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.hero-subtitle {
  font-size: 1.125rem;
  color: var(--gray-500);
  max-width: 600px;
  margin: 0 auto 32px;
  line-height: 1.8;
}
.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
  margin-bottom: 48px;
}
.hero-stats {
  display: flex;
  justify-content: center;
  gap: 48px;
  flex-wrap: wrap;
}
.stat-item {
  text-align: center;
}
.stat-number {
  display: block;
  font-size: 2.5rem;
  font-weight: 800;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.stat-label {
  font-size: 0.875rem;
  color: var(--gray-500);
  font-weight: 500;
}

/* Sections */
section { padding: 80px 0; }
.section-title {
  text-align: center;
  margin-bottom: 8px;
}
.section-subtitle {
  text-align: center;
  color: var(--gray-500);
  margin-bottom: 40px;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 24px;
}
.section-header .section-title,
.section-header .section-subtitle {
  text-align: left;
  margin-bottom: 4px;
}

/* Features */
.features {
  background: white;
  margin-top: -1px;
}
.feature-card {
  text-align: center;
  padding: 32px 24px;
  background: var(--gray-50);
  border-radius: var(--radius-lg);
  transition: all var(--transition);
  animation: fadeInUp 0.5s ease forwards;
  opacity: 0;
}
.feature-card:hover {
  background: white;
  box-shadow: var(--shadow-lg);
  transform: translateY(-4px);
}
.feature-icon { font-size: 2.5rem; display: block; margin-bottom: 16px; }
.feature-card h3 { font-size: 1.125rem; margin-bottom: 8px; }
.feature-card p { color: var(--gray-500); font-size: 0.875rem; margin-bottom: 12px; }
.feature-link { font-weight: 600; font-size: 0.875rem; }

/* Path */
.path-section {
  background: var(--gray-900);
}
.path-section .section-title { color: white; }
.path-section .section-subtitle { color: var(--gray-400); }
.path-timeline {
  max-width: 700px;
  margin: 0 auto;
  position: relative;
}
.path-timeline::before {
  content: '';
  position: absolute;
  left: 24px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: var(--gray-700);
}
.path-step {
  display: flex;
  gap: 24px;
  margin-bottom: 32px;
  position: relative;
}
.step-marker {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  flex-shrink: 0;
  z-index: 1;
}
.step-content {
  background: var(--gray-800);
  padding: 20px 24px;
  border-radius: var(--radius);
  flex: 1;
}
.step-content h3 { color: white; margin-bottom: 4px; }
.step-content p { color: var(--gray-400); font-size: 0.875rem; margin-bottom: 8px; }

/* Daily Words */
.daily-words {
  background: white;
  margin-top: -1px;
}
.word-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}
.word-card {
  background: var(--gray-50);
  padding: 24px;
  border-radius: var(--radius);
  border-left: 4px solid var(--primary);
  transition: all var(--transition);
}
.word-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}
.word-card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 4px;
}
.word-card h4 { font-size: 1.35rem; color: var(--primary-dark); margin-bottom: 4px; }
.word-pronounce-mini {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
}
.mini-pronounce-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 1.5px solid var(--gray-200);
  background: white;
  cursor: pointer;
  font-size: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}
.mini-pronounce-btn:hover {
  border-color: var(--primary);
  background: var(--primary-bg);
}
.mini-pronounce-btn.playing {
  border-color: var(--primary);
  background: var(--primary);
  animation: pulse 0.8s infinite;
}
.word-phonetic { font-size: 0.875rem; color: var(--gray-400); display: block; margin-bottom: 2px; }
.word-phonetic-uk { font-size: 0.813rem; color: var(--gray-400); opacity: 0.85; display: block; margin-bottom: 8px; }
.word-pos { margin-bottom: 8px; }
.word-def { font-size: 0.938rem; color: var(--gray-700); margin-bottom: 12px; }
.word-example { font-size: 0.813rem; color: var(--gray-500); line-height: 1.5; }

@media (max-width: 768px) {
  .hero h1 { font-size: 2.25rem; }
  .hero-stats { gap: 24px; }
  .stat-number { font-size: 1.75rem; }
  section { padding: 48px 0; }
}
</style>

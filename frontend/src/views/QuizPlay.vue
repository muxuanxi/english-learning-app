<template>
  <div class="page">
    <div class="container">
      <button class="btn btn-secondary btn-sm back-btn" @click="$router.push('/quiz')">← 返回测验列表</button>

      <div class="loading-screen" v-if="loading"><div class="spinner"></div></div>

      <div v-if="quiz && !submitted" class="quiz-play">
        <!-- Header -->
        <div class="quiz-header card">
          <h2>{{ quiz.title }}</h2>
          <div class="quiz-progress-info">
            <span>第 {{ currentQ + 1 }} / {{ questions.length }} 题</span>
            <span class="timer" v-if="timeLeft !== null">
              ⏱ {{ formatTime(timeLeft) }}
            </span>
          </div>
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
          </div>
        </div>

        <!-- Question Card -->
        <div class="question-card card" v-if="currentQuestion">
          <div class="question-type-badge">
            {{ typeLabel(currentQuestion.questionType) }}
          </div>
          <h3 class="question-text">{{ currentQuestion.questionText }}</h3>

          <!-- Multiple Choice -->
          <div v-if="currentQuestion.questionType === 'MULTIPLE_CHOICE'" class="options-list">
            <button v-for="(opt, i) in parsedOptions" :key="i"
                    class="option-btn" :class="{ selected: answers[currentQuestion.id] === opt }"
                    @click="answers[currentQuestion.id] = opt">
              <span class="option-letter">{{ letters[i] }}</span>
              <span class="option-text">{{ opt }}</span>
              <span class="option-check" v-if="answers[currentQuestion.id] === opt">✓</span>
            </button>
          </div>

          <!-- True/False -->
          <div v-if="currentQuestion.questionType === 'TRUE_FALSE'" class="options-list">
            <button v-for="opt in ['True', 'False']" :key="opt"
                    class="option-btn" :class="{ selected: answers[currentQuestion.id] === opt }"
                    @click="answers[currentQuestion.id] = opt">
              <span class="option-letter">{{ opt === 'True' ? 'T' : 'F' }}</span>
              <span class="option-text">{{ opt === 'True' ? '正确 (True)' : '错误 (False)' }}</span>
              <span class="option-check" v-if="answers[currentQuestion.id] === opt">✓</span>
            </button>
          </div>

          <!-- Fill Blank -->
          <div v-if="currentQuestion.questionType === 'FILL_BLANK'" class="fill-blank">
            <input v-model="answers[currentQuestion.id]" type="text" class="form-input"
                   placeholder="请输入你的答案..." />
          </div>
        </div>

        <!-- Navigation -->
        <div class="quiz-nav">
          <button class="btn btn-secondary" :disabled="currentQ === 0" @click="currentQ--">← 上一题</button>
          <div class="question-dots">
            <span v-for="(q, i) in questions" :key="q.id"
                  class="dot" :class="{
                    active: i === currentQ,
                    answered: answers[q.id],
                    correct: submitted && results[q.id]
                  }"
                  @click="currentQ = i">
              {{ i + 1 }}
            </span>
          </div>
          <button v-if="currentQ < questions.length - 1" class="btn btn-primary"
                  @click="currentQ++">
            下一题 →
          </button>
          <button v-else class="btn btn-accent" @click="submitQuiz">
            📩 提交答卷
          </button>
        </div>
      </div>

      <!-- Results -->
      <div v-if="submitted" class="result-card card">
        <div class="result-header" :class="passed ? 'passed' : 'failed'">
          <span class="result-icon">{{ passed ? '🎉' : '💪' }}</span>
          <h2>{{ passed ? '恭喜通过！' : '继续加油！' }}</h2>
          <div class="score-circle">
            <span class="score-value">{{ resultScore }}</span>
            <span class="score-unit">分</span>
          </div>
          <p class="score-detail">
            得分 {{ result?.score || 0 }} / {{ result?.totalScore || 0 }}
            · 正确率 {{ scorePercent }}%
          </p>
        </div>

        <div class="result-details">
          <h3>📋 答题详情</h3>
          <div class="review-item" v-for="(q, i) in questions" :key="q.id"
               :class="{ correct: results[q.id], wrong: !results[q.id] }">
            <span class="review-status">{{ results[q.id] ? '✓' : '✗' }}</span>
            <div>
              <p class="review-question">{{ i + 1 }}. {{ q.questionText }}</p>
              <p class="review-answer">
                你的答案：<strong>{{ answers[q.id] || '未作答' }}</strong>
              </p>
              <p v-if="!results[q.id]" class="review-correct">
                正确答案：<strong>{{ q.correctAnswer }}</strong>
              </p>
              <p v-if="q.explanation" class="review-explanation">💡 {{ q.explanation }}</p>
            </div>
          </div>
        </div>

        <div class="result-actions">
          <button class="btn btn-outline" @click="$router.push('/quiz')">返回测验列表</button>
          <button class="btn btn-primary" @click="retryQuiz">重新作答</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { quizAPI } from '@/api'

const route = useRoute()
const quiz = ref(null)
const loading = ref(true)
const currentQ = ref(0)
const answers = ref({})
const submitted = ref(false)
const result = ref(null)
const results = ref({})
const timeLeft = ref(null)
let timer = null

const questions = computed(() => quiz.value?.questions || [])
const currentQuestion = computed(() => questions.value[currentQ.value] || null)
const progressPercent = computed(() =>
  questions.value.length ? ((currentQ.value + 1) / questions.value.length) * 100 : 0
)
const parsedOptions = computed(() => {
  if (!currentQuestion.value?.optionsJson) return []
  try {
    let result = JSON.parse(currentQuestion.value.optionsJson)
    // Handle double-wrapped JSON (outer quotes make it a string instead of array)
    if (typeof result === 'string') {
      result = JSON.parse(result)
    }
    return Array.isArray(result) ? result : []
  } catch { return [] }
})
const letters = ['A', 'B', 'C', 'D', 'E', 'F']

const passed = computed(() => (result.value?.score || 0) >= (quiz.value?.passScore || 60))
const resultScore = computed(() => result.value?.score || 0)
const scorePercent = computed(() => {
  if (!result.value?.totalScore) return 0
  return Math.round((result.value.score / result.value.totalScore) * 100)
})

function typeLabel(t) {
  const map = { MULTIPLE_CHOICE: '单选题', FILL_BLANK: '填空题', TRUE_FALSE: '判断题', MATCHING: '连线题', WRITING: '写作题' }
  return map[t] || t
}
function formatTime(s) {
  const m = Math.floor(s / 60), sec = s % 60
  return `${m}:${String(sec).padStart(2, '0')}`
}

async function loadQuiz() {
  try {
    const res = await quizAPI.getQuiz(route.params.id)
    quiz.value = res.data.data
    if (quiz.value.timeLimitMinutes > 0) {
      timeLeft.value = quiz.value.timeLimitMinutes * 60
      startTimer()
    }
  } finally { loading.value = false }
}

function startTimer() {
  timer = setInterval(() => {
    if (timeLeft.value > 0) {
      timeLeft.value--
    } else {
      submitQuiz()
    }
  }, 1000)
}

async function submitQuiz() {
  clearInterval(timer)
  try {
    const res = await quizAPI.submitQuiz(quiz.value.id, {
      answers: answers.value,
      timeSpent: quiz.value.timeLimitMinutes * 60 - (timeLeft.value || 0)
    })
    result.value = res.data.data
    submitted.value = true

    // Calculate results per question
    questions.value.forEach(q => {
      const userAns = (answers.value[q.id] || '').trim().toLowerCase()
      const correctAns = q.correctAnswer.trim().toLowerCase()
      results.value[q.id] = userAns === correctAns
    })
  } catch (e) {
    alert('提交失败，请重试')
  }
}

function retryQuiz() {
  answers.value = {}
  submitted.value = false
  result.value = null
  results.value = {}
  currentQ.value = 0
  timeLeft.value = quiz.value.timeLimitMinutes > 0 ? quiz.value.timeLimitMinutes * 60 : null
  if (timeLeft.value) startTimer()
}

onMounted(loadQuiz)
onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
.back-btn { margin-bottom: 24px; }
.quiz-play { max-width: 720px; margin: 0 auto; }

/* Quiz Header */
.quiz-header { text-align: center; margin-bottom: 24px; }
.quiz-header h2 { margin-bottom: 12px; }
.quiz-progress-info { display: flex; justify-content: space-between; color: var(--gray-500); font-size: 0.875rem; margin-bottom: 12px; }
.timer { color: var(--accent); font-weight: 700; }

.progress-bar {
  height: 6px; background: var(--gray-200); border-radius: 3px; overflow: hidden;
}
.progress-fill {
  height: 100%; background: linear-gradient(90deg, var(--primary), var(--secondary));
  border-radius: 3px; transition: width 0.3s ease;
}

/* Question */
.question-card { margin-bottom: 24px; overflow: visible; }
.question-type-badge {
  display: inline-block; background: var(--primary-bg); color: var(--primary);
  padding: 4px 12px; border-radius: 100px; font-size: 0.75rem; font-weight: 600; margin-bottom: 16px;
}
.question-text {
  font-size: 1.125rem; line-height: 1.6; margin-bottom: 24px;
  word-break: break-word; overflow-wrap: break-word;
}

.options-list { display: flex; flex-direction: column; gap: 10px; }
.option-btn {
  display: flex; align-items: flex-start; gap: 12px; padding: 16px; border: 2px solid var(--gray-200);
  border-radius: var(--radius); background: white; cursor: pointer;
  transition: all var(--transition-fast); text-align: left; font-family: var(--font-mixed);
  width: 100%;
}
.option-btn:hover { border-color: var(--primary); background: var(--primary-bg); }
.option-btn.selected { border-color: var(--primary); background: var(--primary-bg); }
.option-letter {
  width: 36px; min-width: 36px; height: 36px; border-radius: 50%; background: var(--gray-100);
  display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 0.875rem; flex-shrink: 0;
  margin-top: 1px;
}
.option-btn.selected .option-letter { background: var(--primary); color: white; }
.option-text {
  flex: 1; font-size: 0.938rem; line-height: 1.6; word-break: break-word; overflow-wrap: break-word;
  padding-top: 2px;
}
.option-check {
  color: var(--primary); font-weight: 700; font-size: 1.25rem; flex-shrink: 0;
  padding-top: 2px;
}

.fill-blank { margin-top: 16px; }
.fill-blank .form-input { width: 100%; padding: 16px; font-size: 1.063rem; }

/* Quiz Nav */
.quiz-nav { display: flex; align-items: center; justify-content: space-between; gap: 16px; }
.question-dots { display: flex; gap: 6px; flex-wrap: wrap; justify-content: center; }
.dot {
  width: 32px; height: 32px; border-radius: 50%; border: 2px solid var(--gray-300);
  display: flex; align-items: center; justify-content: center; font-size: 0.75rem;
  font-weight: 600; cursor: pointer; transition: all var(--transition-fast);
}
.dot.active { border-color: var(--primary); background: var(--primary-bg); color: var(--primary); }
.dot.answered { border-color: var(--success); background: var(--success-bg); color: var(--success); }
.dot.correct { border-color: var(--success); background: var(--success); color: white; }

/* Results */
.result-card { max-width: 720px; margin: 0 auto; }
.result-header { text-align: center; padding: 32px; }
.result-header.passed { background: linear-gradient(135deg, #d1fae5, #a7f3d0); border-radius: var(--radius-lg) var(--radius-lg) 0 0; }
.result-header.failed { background: linear-gradient(135deg, #fef3c7, #fde68a); border-radius: var(--radius-lg) var(--radius-lg) 0 0; }
.result-icon { font-size: 3rem; }
.result-header h2 { margin: 8px 0; }
.score-circle {
  width: 100px; height: 100px; border-radius: 50%; background: white;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  margin: 16px auto; box-shadow: var(--shadow-md);
}
.score-value { font-size: 2rem; font-weight: 800; color: var(--primary); }
.score-unit { font-size: 0.75rem; color: var(--gray-400); }
.score-detail { color: var(--gray-600); }

.result-details { padding: 24px 0; }
.result-details h3 { margin-bottom: 16px; }
.review-item {
  display: flex; gap: 12px; padding: 16px; border-radius: var(--radius);
  margin-bottom: 12px;
}
.review-item.correct { background: var(--success-bg); }
.review-item.wrong { background: var(--error-bg); }
.review-status {
  font-size: 1.25rem; font-weight: 700; width: 28px; text-align: center; flex-shrink: 0;
}
.review-item.correct .review-status { color: var(--success); }
.review-item.wrong .review-status { color: var(--error); }
.review-question { font-size: 0.875rem; color: var(--gray-700); margin-bottom: 4px; line-height: 1.6; word-break: break-word; }
.review-answer { font-size: 0.813rem; color: var(--gray-500); word-break: break-word; }
.review-correct { font-size: 0.813rem; color: var(--success); word-break: break-word; }
.review-explanation { font-size: 0.813rem; color: var(--gray-600); margin-top: 4px; font-style: italic; word-break: break-word; }

.result-actions {
  display: flex; gap: 12px; justify-content: center; padding-top: 24px;
  border-top: 1px solid var(--gray-200);
}

@media (max-width: 768px) {
  .quiz-nav { flex-direction: column; }
  .question-dots { order: -1; }
}
</style>

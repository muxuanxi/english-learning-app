/**
 * 语音朗读工具 - 支持美式和英式发音
 * 使用浏览器 Web Speech API
 */

// 语音缓存：避免重复获取语音列表
let voiceCache = null
let voiceCacheTime = 0

/**
 * 获取可用的语音列表
 */
export function getVoices() {
  if (!window.speechSynthesis) return []

  const now = Date.now()
  if (voiceCache && now - voiceCacheTime < 60000) {
    return voiceCache
  }

  voiceCache = window.speechSynthesis.getVoices()
  voiceCacheTime = now
  return voiceCache
}

/**
 * 根据语言查找最佳语音 - 优先选择低沉温和的男声
 * @param {string} lang - 'en-US' 或 'en-GB'
 * @returns {SpeechSynthesisVoice|null}
 */
export function findVoice(lang) {
  const voices = getVoices()

  // 按优先级排序的语音名称关键词（低沉男声优先）
  const maleKeywords = ['male', 'guy', 'man', 'david', 'alex', 'daniel', 'tom', 'james', 'fred']
  const femaleKeywords = ['female', 'woman', 'girl', 'lady', 'samantha', 'karen', 'susan', 'lisa']

  // 1. 优先找原生男声
  const native = voices.filter(v => v.lang.startsWith(lang) && v.localService)
  if (native.length > 0) {
    // 找男声
    for (const kw of maleKeywords) {
      const match = native.find(v => v.name.toLowerCase().includes(kw))
      if (match) return match
    }
    // 排除女声，找中性/其他
    const nonFemale = native.filter(v =>
      !femaleKeywords.some(kw => v.name.toLowerCase().includes(kw))
    )
    if (nonFemale.length > 0) return nonFemale[0]
    return native[0]
  }

  // 2. 在线语音
  const online = voices.filter(v => v.lang.startsWith(lang))
  if (online.length > 0) {
    for (const kw of maleKeywords) {
      const match = online.find(v => v.name.toLowerCase().includes(kw))
      if (match) return match
    }
    const nonFemale = online.filter(v =>
      !femaleKeywords.some(kw => v.name.toLowerCase().includes(kw))
    )
    if (nonFemale.length > 0) return nonFemale[0]
    return online[0]
  }
  return null
}

/**
 * 朗读文本
 * @param {string} text - 要朗读的文本
 * @param {string} lang - 'en-US' 或 'en-GB'
 * @param {object} options
 * @param {number} options.rate - 语速 (0.1-10, 默认 0.9)
 * @param {number} options.pitch - 音高 (0-2, 默认 1)
 * @param {number} options.volume - 音量 (0-1, 默认 1)
 * @returns {Promise} 朗读完成后 resolve
 */
export function speak(text, lang = 'en-US', options = {}) {
  return new Promise((resolve, reject) => {
    if (!window.speechSynthesis) {
      reject(new Error('浏览器不支持语音朗读'))
      return
    }

    // 停止当前正在朗读的
    window.speechSynthesis.cancel()

    const utterance = new SpeechSynthesisUtterance(text)
    utterance.lang = lang
    // US 声音更低沉醇厚 (pitch 0.68)，UK 保持清亮 (pitch 0.95)
    const isUS = lang === 'en-US'
    utterance.rate = options.rate ?? (isUS ? 0.85 : 0.92)
    utterance.pitch = options.pitch ?? (isUS ? 0.68 : 0.95)
    utterance.volume = options.volume ?? 1

    // 查找匹配的语音
    const voice = findVoice(lang)
    if (voice) {
      utterance.voice = voice
    }

    utterance.onend = () => resolve()
    utterance.onerror = (e) => {
      if (e.error !== 'canceled' && e.error !== 'interrupted') {
        reject(e)
      } else {
        resolve()
      }
    }

    window.speechSynthesis.speak(utterance)
  })
}

/**
 * 朗读文章（支持断句，可暂停/继续/停止）
 */
export class ArticleReader {
  constructor(text) {
    this.sentences = this._splitSentences(text)
    this.currentIndex = 0
    this.isPaused = false
    this.isSpeaking = false
    this.lang = 'en-US'
  }

  _splitSentences(text) {
    // 先移除 HTML 标签
    const plain = text.replace(/<[^>]+>/g, '')
    // 按标点断句
    return plain
      .split(/(?<=[.!?])\s+/)
      .map(s => s.trim())
      .filter(s => s.length > 0)
  }

  setAccent(accent) {
    this.lang = accent === 'uk' ? 'en-GB' : 'en-US'
  }

  speak() {
    if (this.currentIndex >= this.sentences.length) {
      this.currentIndex = 0
      this.isSpeaking = false
      return Promise.resolve()
    }

    this.isSpeaking = true
    this.isPaused = false

    const speakNext = () => {
      if (this.isPaused) return
      if (this.currentIndex >= this.sentences.length) {
        this.isSpeaking = false
        this._onEnd && this._onEnd()
        return
      }

      const sentence = this.sentences[this.currentIndex]
      this._onProgress && this._onProgress(this.currentIndex, this.sentences.length)

      speak(sentence, this.lang, { rate: 0.95 }).then(() => {
        this.currentIndex++
        speakNext()
      }).catch(() => {
        this.currentIndex++
        speakNext()
      })
    }

    speakNext()
  }

  pause() {
    this.isPaused = true
    window.speechSynthesis?.cancel()
  }

  resume() {
    if (this.isPaused) {
      this.isPaused = false
      this.speak()
    }
  }

  stop() {
    this.isPaused = false
    this.isSpeaking = false
    this.currentIndex = 0
    window.speechSynthesis?.cancel()
  }

  onProgress(cb) { this._onProgress = cb }
  onEnd(cb) { this._onEnd = cb }
}

/**
 * 获取单个单词的读音链接（使用免费的词典API音频）
 */
export function getWordAudioUrl(word, accent = 'us') {
  // 使用美国/英国发音的音频链接
  // 这里使用免费在线TTS作为后备
  const encoded = encodeURIComponent(word)
  if (accent === 'uk') {
    return `https://dict.youdao.com/dictvoice?audio=${encoded}&type=1`
  }
  return `https://dict.youdao.com/dictvoice?audio=${encoded}&type=0`
}

/**
 * 使用 HTML5 Audio 播放音频 URL
 */
export function playAudioUrl(url) {
  return new Promise((resolve, reject) => {
    const audio = new Audio(url)
    audio.onended = () => resolve()
    audio.onerror = () => reject(new Error('音频加载失败'))
    audio.play().catch(reject)
  })
}

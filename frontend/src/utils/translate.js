/**
 * 翻译工具 - 使用免费 MyMemory API
 * 支持英→中翻译
 */

const CACHE_KEY = 'translate_cache'
const cache = JSON.parse(localStorage.getItem(CACHE_KEY) || '{}')

function saveCache() {
  localStorage.setItem(CACHE_KEY, JSON.stringify(cache))
}

/**
 * 翻译文本（英→中）
 * 使用 MyMemory 免费翻译API
 */
export async function translateText(text, from = 'en', to = 'zh') {
  if (!text || !text.trim()) return text

  const cacheKey = `${from}|${to}|${text}`
  if (cache[cacheKey]) return cache[cacheKey]

  try {
    const url = `https://api.mymemory.translated.net/get?q=${encodeURIComponent(text)}&langpair=${from}|${to}`
    const resp = await fetch(url)
    const data = await resp.json()

    if (data.responseStatus === 200 && data.responseData?.translatedText) {
      const translated = data.responseData.translatedText
      cache[cacheKey] = translated
      saveCache()
      return translated
    }
    return text
  } catch (e) {
    console.warn('Translation failed:', e.message)
    return text
  }
}

/**
 * 批量翻译
 */
export async function translateBatch(texts, from = 'en', to = 'zh') {
  const results = {}
  for (const text of texts) {
    if (text && text.trim()) {
      results[text] = await translateText(text, from, to)
    }
  }
  return results
}

/**
 * 清除翻译缓存
 */
export function clearTranslationCache() {
  localStorage.removeItem(CACHE_KEY)
  Object.keys(cache).forEach(k => delete cache[k])
}

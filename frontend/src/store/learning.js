import { defineStore } from 'pinia'
import { ref } from 'vue'
import { statsAPI } from '@/api'

export const useLearningStore = defineStore('learning', () => {
  const stats = ref(null)
  const checkinDays = ref([])
  const loading = ref(false)

  async function fetchStats() {
    loading.value = true
    try {
      const res = await statsAPI.getStats()
      stats.value = res.data.data
    } finally {
      loading.value = false
    }
  }

  async function doCheckin() {
    const res = await statsAPI.checkin()
    const today = new Date().toISOString().split('T')[0]
    if (!checkinDays.value.includes(today)) {
      checkinDays.value.push(today)
    }
    return res
  }

  return { stats, checkinDays, loading, fetchStats, doCheckin }
})

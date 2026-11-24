<script setup>
import { ref, onMounted } from 'vue'
import api, { errorMessage } from '@/api'

const games = ref([])
const page = ref(0)
const totalPages = ref(0)
const search = ref('')
const loading = ref(false)
const error = ref('')

async function load() {
  loading.value = true
  error.value = ''
  try {
    const { data } = await api.get('/games', {
      params: { search: search.value || undefined, page: page.value },
    })
    games.value = data.content
    totalPages.value = data.totalPages
  } catch (e) {
    error.value = errorMessage(e)
  } finally {
    loading.value = false
  }
}

function doSearch() {
  page.value = 0
  load()
}

function goTo(p) {
  page.value = p
  load()
}

function hideImage(e) {
  e.target.style.display = 'none' // докато кориците ги няма
}

onMounted(load)
</script>

<template>
  <h1>Игри</h1>

  <form class="search" @submit.prevent="doSearch">
    <input v-model="search" placeholder="Търси игра..." />
    <button type="submit">Търси</button>
  </form>

  <p v-if="error" class="error">{{ error }}</p>
  <p v-else-if="loading" class="muted">Зареждане...</p>
  <p v-else-if="!games.length" class="muted">Няма намерени игри</p>

  <div v-else class="grid">
    <RouterLink
      v-for="game in games"
      :key="game.id"
      :to="`/games/${game.id}`"
      class="card game-card"
    >
      <img v-if="game.coverUrl" :src="game.coverUrl" :alt="game.title" @error="hideImage" />
      <h3>{{ game.title }}</h3>
      <p class="muted">{{ game.averageRating ? '★ ' + game.averageRating : 'Без оценка' }}</p>
    </RouterLink>
  </div>

  <div v-if="totalPages > 1" class="pager">
    <button :disabled="page === 0" @click="goTo(page - 1)">Назад</button>
    <span>{{ page + 1 }} / {{ totalPages }}</span>
    <button :disabled="page + 1 >= totalPages" @click="goTo(page + 1)">Напред</button>
  </div>
</template>
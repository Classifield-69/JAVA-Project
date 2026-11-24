<script setup>
import { ref, onMounted } from 'vue'
import api, { errorMessage } from '@/api'
import { useAuthStore } from '@/stores/auth'

const props = defineProps({ id: String })
const auth = useAuthStore()

const game = ref(null)
const error = ref('')

// колекция
const platformId = ref('')
const collectionMsg = ref('')
const collectionError = ref('')

// ревюта
const reviews = ref([])
const reviewPage = ref(0)
const reviewTotalPages = ref(0)
const rating = ref(10)
const text = ref('')
const reviewError = ref('')

async function loadGame() {
  try {
    const { data } = await api.get(`/games/${props.id}`)
    game.value = data
    if (data.platforms.length === 1) platformId.value = data.platforms[0].id
  } catch (e) {
    error.value = errorMessage(e)
  }
}

async function loadReviews() {
  const { data } = await api.get(`/games/${props.id}/reviews`, {
    params: { page: reviewPage.value },
  })
  reviews.value = data.content
  reviewTotalPages.value = data.totalPages
}

async function addToCollection(type) {
  collectionMsg.value = ''
  collectionError.value = ''
  try {
    await api.post('/users/me/games', {
      gameId: game.value.id,
      platformId: platformId.value,
      type,
    })
    collectionMsg.value = type === 'OWNED' ? 'Добавена в колекцията' : 'Добавена в желани'
  } catch (e) {
    collectionError.value = errorMessage(e)
  }
}

async function submitReview() {
  reviewError.value = ''
  try {
    await api.post(`/games/${props.id}/reviews`, { rating: rating.value, text: text.value })
    text.value = ''
    reviewPage.value = 0
    await Promise.all([loadReviews(), loadGame()]) // новата средна оценка
  } catch (e) {
    reviewError.value = errorMessage(e)
  }
}

function goToReviewPage(p) {
  reviewPage.value = p
  loadReviews()
}

function names(list) {
  return list.map((x) => x.name).join(', ')
}

function formatDate(d) {
  return d ? new Date(d).toLocaleDateString('bg-BG') : ''
}

function hideImage(e) {
  e.target.style.display = 'none'
}

onMounted(async () => {
  await loadGame()
  if (game.value) loadReviews()
})
</script>

<template>
  <p v-if="error" class="error">{{ error }}</p>

  <div v-else-if="game">
    <div class="card details">
      <img v-if="game.coverUrl" :src="game.coverUrl" :alt="game.title" @error="hideImage" />
      <div>
        <h1>{{ game.title }}</h1>
        <p class="muted">{{ game.developer }} · {{ game.releaseDate }}</p>
        <p>{{ game.description }}</p>
        <p><strong>Жанрове:</strong> {{ names(game.genres) }}</p>
        <p><strong>Платформи:</strong> {{ names(game.platforms) }}</p>
        <p>
          <strong>Оценка:</strong> {{ game.averageRating ?? '—' }}
          <span class="muted">({{ game.reviewCount }} ревюта)</span>
        </p>

        <div v-if="auth.isLoggedIn">
          <div class="row">
            <select v-model="platformId">
              <option value="" disabled>Платформа</option>
              <option v-for="p in game.platforms" :key="p.id" :value="p.id">{{ p.name }}</option>
            </select>
            <button :disabled="!platformId" @click="addToCollection('OWNED')">Имам я</button>
            <button class="secondary" :disabled="!platformId" @click="addToCollection('WISHLIST')">
              Искам я
            </button>
          </div>
          <p v-if="collectionMsg" class="success">{{ collectionMsg }}</p>
          <p v-if="collectionError" class="error">{{ collectionError }}</p>
        </div>
        <p v-else class="muted">
          <RouterLink to="/login">Влез</RouterLink>, за да добавяш игри и да пишеш ревюта.
        </p>
      </div>
    </div>

    <div class="card">
      <h2>Ревюта</h2>

      <form v-if="auth.isLoggedIn" @submit.prevent="submitReview">
        <div class="row">
          <label>Оценка:</label>
          <select v-model.number="rating">
            <option v-for="n in 10" :key="n" :value="n">{{ n }}</option>
          </select>
        </div>
        <textarea v-model="text" rows="3" maxlength="2000" placeholder="Твоето мнение..." required />
        <p v-if="reviewError" class="error">{{ reviewError }}</p>
        <button type="submit">Публикувай</button>
      </form>

      <p v-if="!reviews.length" class="muted">Още няма ревюта</p>
      <div v-for="r in reviews" :key="r.id" class="item">
        <strong>{{ r.username }}</strong> · {{ r.rating }}/10
        <span class="muted">{{ formatDate(r.createdAt) }}</span>
        <p>{{ r.text }}</p>
      </div>

      <div v-if="reviewTotalPages > 1" class="pager">
        <button :disabled="reviewPage === 0" @click="goToReviewPage(reviewPage - 1)">Назад</button>
        <span>{{ reviewPage + 1 }} / {{ reviewTotalPages }}</span>
        <button :disabled="reviewPage + 1 >= reviewTotalPages" @click="goToReviewPage(reviewPage + 1)">
          Напред
        </button>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import api, { errorMessage } from '@/api'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const collection = ref([])
const reviews = ref([])
const tab = ref('OWNED')
const error = ref('')

const filtered = computed(() => collection.value.filter((ug) => ug.type === tab.value))
const count = (type) => collection.value.filter((ug) => ug.type === type).length

async function load() {
  try {
    const [c, r] = await Promise.all([api.get('/users/me/games'), api.get('/users/me/reviews')])
    collection.value = c.data
    reviews.value = r.data
  } catch (e) {
    error.value = errorMessage(e)
  }
}

async function removeGame(id) {
  try {
    await api.delete(`/users/me/games/${id}`)
    collection.value = collection.value.filter((ug) => ug.id !== id)
  } catch (e) {
    error.value = errorMessage(e)
  }
}

async function deleteReview(id) {
  if (!confirm('Да изтрия ли ревюто?')) return
  try {
    await api.delete(`/reviews/${id}`)
    reviews.value = reviews.value.filter((r) => r.id !== id)
  } catch (e) {
    error.value = errorMessage(e)
  }
}

onMounted(load)
</script>

<template>
  <div class="card row">
    <div>
      <h1>{{ auth.user?.username }}</h1>
      <p class="muted">{{ auth.user?.email }}</p>
    </div>
  </div>

  <p v-if="error" class="error">{{ error }}</p>

  <div class="card">
    <h2>Моята колекция</h2>
    <div class="row">
      <button :class="{ secondary: tab !== 'OWNED' }" @click="tab = 'OWNED'">
        Имам ({{ count('OWNED') }})
      </button>
      <button :class="{ secondary: tab !== 'WISHLIST' }" @click="tab = 'WISHLIST'">
        Искам ({{ count('WISHLIST') }})
      </button>
    </div>

    <p v-if="!filtered.length" class="muted">Празно</p>
    <div v-for="ug in filtered" :key="ug.id" class="item row">
      <span>
        <RouterLink :to="`/games/${ug.gameId}`">{{ ug.gameTitle }}</RouterLink>
        <span class="muted"> · {{ ug.platformName }}</span>
      </span>
      <button class="danger small" @click="removeGame(ug.id)">Премахни</button>
    </div>
  </div>

  <div class="card">
    <h2>Моите ревюта</h2>
    <p v-if="!reviews.length" class="muted">Още нямаш ревюта</p>
    <div v-for="r in reviews" :key="r.id" class="item">
      <div class="row" style="justify-content: space-between">
        <span>
          <RouterLink :to="`/games/${r.gameId}`">{{ r.gameTitle }}</RouterLink> · {{ r.rating }}/10
        </span>
        <button class="danger small" @click="deleteReview(r.id)">Изтрий</button>
      </div>
      <p>{{ r.text }}</p>
    </div>
  </div>
</template>
